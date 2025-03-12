package xin.eason.domain.trade.service.settlement;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import xin.eason.domain.trade.adapter.port.ITradePort;
import xin.eason.domain.trade.adapter.repository.ITradeRepository;
import xin.eason.domain.trade.model.aggregate.GroupBuyOrderAggregate;
import xin.eason.domain.trade.model.entity.*;
import xin.eason.domain.trade.service.ITradeSettlementOrderService;
import xin.eason.domain.trade.service.settlement.filter.factory.TradeSettlementRuleFilterFactory;
import xin.eason.types.design.framework.link.multimodel.chain.BusinessLinkList;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * trade 领域 <b>结算订单</b> 服务
 */
@Slf4j
@Service
public class TradeSettlementOrderService implements ITradeSettlementOrderService {
    /**
     * 拼团交易 trade 领域接口
     */
    private final ITradePort tradePort;
    /**
     * 拼团交易 trade 领域仓储
     */
    private final ITradeRepository tradeRepository;
    /**
     * 交易结算规则过滤责任链对象
     */
    private final BusinessLinkList<TradeSettlementRuleFilterRequestEntity, TradeSettlementRuleFilterResponseEntity, TradeSettlementRuleFilterFactory.DynamicContext> tradeSettlementRuleFilterResponsibilityChain;

    public TradeSettlementOrderService(ITradePort tradePort, ITradeRepository tradeRepository, @Qualifier("tradeSettlementRuleFilterResponsibilityChain") BusinessLinkList<TradeSettlementRuleFilterRequestEntity, TradeSettlementRuleFilterResponseEntity, TradeSettlementRuleFilterFactory.DynamicContext> tradeSettlementRuleFilterResponsibilityChain) {
        this.tradePort = tradePort;
        this.tradeRepository = tradeRepository;
        this.tradeSettlementRuleFilterResponsibilityChain = tradeSettlementRuleFilterResponsibilityChain;
    }

    /**
     * <p>根据订单结算实体的相关信息进行订单结算</p>
     * <p>返回 <b>SC 值, userId, outerOrderId, teamId, activityId</b></p>
     *
     * @param orderSettlementEntity 订单结算实体
     * @return 订单结算成功实体
     */
    @Override
    public OrderSettlementSuccessEntity settlementPayOrder(OrderSettlementEntity orderSettlementEntity) {
        log.info("拼团交易-支付订单结算, userId: {} outOrderId: {}", orderSettlementEntity.getUserId(), orderSettlementEntity.getOuterOrderId());
        LocalDateTime payTime = orderSettlementEntity.getPayTime();

        TradeSettlementRuleFilterRequestEntity settlementRuleFilterRequest = TradeSettlementRuleFilterRequestEntity.builder()
                .source(orderSettlementEntity.getSource())
                .channel(orderSettlementEntity.getChannel())
                .userId(orderSettlementEntity.getUserId())
                .outOrderId(orderSettlementEntity.getOuterOrderId())
                .payTime(payTime)
                .build();

        TradeSettlementRuleFilterResponseEntity responseEntity = tradeSettlementRuleFilterResponsibilityChain.apply(settlementRuleFilterRequest, new TradeSettlementRuleFilterFactory.DynamicContext());

        PayOrderTeamEntity teamEntity = tradeRepository.queryTeamInfo(orderSettlementEntity.getUserId(), orderSettlementEntity.getOuterOrderId());
        PayOrderActivityEntity activityEntity = tradeRepository.queryActivityInfo(teamEntity.getTeamId());
        PayOrderEntity orderEntity = PayOrderEntity.builder().payTime(payTime).build();

        // 进行订单结算
        GroupBuyOrderAggregate groupBuyOrderAggregate = GroupBuyOrderAggregate.builder()
                .userId(orderSettlementEntity.getUserId())
                .outerOrderId(settlementRuleFilterRequest.getOutOrderId())
                .payOrderEntity(orderEntity)
                .payOrderTeamEntity(teamEntity)
                .payOrderActivityEntity(activityEntity)
                .build();

        Boolean needToNotify = tradeRepository.settlementPayOrder(groupBuyOrderAggregate);
        log.info("userId: {}, outOrderId: {} 订单已结算完成!", groupBuyOrderAggregate.getUserId(), groupBuyOrderAggregate.getOuterOrderId());
        if (needToNotify) {
            log.info("已记录回调信息!");
            Map<String, Integer> notifyResultMap = execNotifyJob(teamEntity.getTeamId());
            log.info("已收到回调的状态信息: {}", JSON.toJSONString(notifyResultMap));
        }

        return OrderSettlementSuccessEntity.builder()
                .source(orderSettlementEntity.getSource())
                .channel(orderSettlementEntity.getChannel())
                .userId(orderSettlementEntity.getUserId())
                .outerOrderId(orderSettlementEntity.getOuterOrderId())
                .teamId(groupBuyOrderAggregate.getPayOrderTeamEntity().getTeamId())
                .activityId(groupBuyOrderAggregate.getPayOrderActivityEntity().getActivityId())
                .build();
    }

    /**
     * 执行全部未进行回调的回调任务
     *
     * @return 回调任务的响应信息
     */
    @Override
    public Map<String, Integer> execNotifyJob() {
        log.info("执行所有未执行的回调任务");
        List<NotifyTaskEntity> notifyTaskEntityList = tradeRepository.queryNoNotifyTaskList();
        return execNotifyJob(notifyTaskEntityList);
    }


    /**
     * 进行指定 teamId 的回调任务
     *
     * @param teamId 拼团队伍 ID
     * @return 回调任务的响应信息
     */
    @Override
    public Map<String, Integer> execNotifyJob(String teamId) {
        log.info("执行指定 teamId: {} 的回调任务", teamId);
        List<NotifyTaskEntity> notifyTaskEntityList = tradeRepository.queryNoNotifyTaskList(teamId);
        return execNotifyJob(notifyTaskEntityList);
    }

    /**
     * 根据 notifyTask 中的相关信息执行回调任务
     *
     * @param notifyTaskEntityList 回调实体列表
     * @return 回调任务的相应信息
     */
    private Map<String, Integer> execNotifyJob(List<NotifyTaskEntity> notifyTaskEntityList) {
        int successCount = 0, errorCount = 0, retryCount = 0;
        for (NotifyTaskEntity notifyTaskEntity : notifyTaskEntityList) {
            log.info("正在进行回调, 回调地址: {}", notifyTaskEntity.getNotifyUrl());
            String response = tradePort.groupBuyNotify(notifyTaskEntity);
            if ("success".equals(response)) {
                // 如果回调成功, 将库表中的回调明细的 回调次数 +1, 将明细状态更改为 成功
                int updateRow = tradeRepository.updateNotifyStatusSuccess(notifyTaskEntity);
                if (updateRow == 1)
                    // 修改成功, 成功统计量 +1
                    successCount ++;
            } else if ("error".equals(response)){
                // 总共回调 5 次, 超过则不再回调
                if (notifyTaskEntity.getNotifyCount() >= 5) {
                    // 回调次数在 5 次以上, 且回调失败, 回调次数 +1, 将明细状态更改为 失败
                    int updateRow = tradeRepository.updateNotifyStatusError(notifyTaskEntity);
                    if (updateRow == 1)
                        // 修改成功, 失败统计量 +1
                        errorCount ++;
                } else {
                    // 回调次数在 5 次以内, 且回调失败, 回调次数 +1, 将明细状态改为 重试
                    int updateRow = tradeRepository.updateNotifyStatusRetry(notifyTaskEntity);
                    if (updateRow == 1)
                        // 修改成功, 重试统计量 +1
                        retryCount ++;
                }
            }
        }

        Map<String, Integer> resultMap = new HashMap<>();
        resultMap.put("waitCount", notifyTaskEntityList.size());
        resultMap.put("successCount", successCount);
        resultMap.put("errorCount", errorCount);
        resultMap.put("retryCount", retryCount);

        return resultMap;
    }
}
