package xin.eason.domain.trade.service.lock;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import xin.eason.domain.trade.adapter.repository.ITradeRepository;
import xin.eason.domain.trade.model.aggregate.GroupBuyOrderAggregate;
import xin.eason.domain.trade.model.entity.PayOrderEntity;
import xin.eason.domain.trade.model.entity.PayOrderTeamEntity;
import xin.eason.domain.trade.model.entity.TradeLockRuleFilterRequestEntity;
import xin.eason.domain.trade.model.entity.TradeLockRuleFilterResponseEntity;
import xin.eason.domain.trade.service.ITradeLockOrderService;
import xin.eason.domain.trade.service.lock.filter.factory.TradeLockRuleFilterFactory;
import xin.eason.types.design.framework.link.multimodel.chain.BusinessLinkList;

/**
 * trade 领域 <b>锁单</b> 服务
 */
@Slf4j
@Service
public class TradeLockOrderService implements ITradeLockOrderService {
    /**
     * 拼团交易 trade 领域仓储
     */
    private final ITradeRepository tradeRepository;
    /**
     * 交易锁单规则过滤责任链对象
     */
    private final BusinessLinkList<TradeLockRuleFilterRequestEntity, TradeLockRuleFilterResponseEntity, TradeLockRuleFilterFactory.DynamicContext> tradeLockRuleFilterResponsibilityChain;

    public TradeLockOrderService(ITradeRepository tradeRepository, @Qualifier("tradeLockRuleFilterResponsibilityChain") BusinessLinkList<TradeLockRuleFilterRequestEntity, TradeLockRuleFilterResponseEntity, TradeLockRuleFilterFactory.DynamicContext> tradeLockRuleFilterResponsibilityChain) {
        this.tradeRepository = tradeRepository;
        this.tradeLockRuleFilterResponsibilityChain = tradeLockRuleFilterResponsibilityChain;
    }

    /**
     * 查询该用户的未支付订单
     *
     * @param userId       用户 ID
     * @param outerOrderId 外部订单 ID
     * @return 拼团订单实体
     */
    @Override
    public PayOrderEntity checkUnpayOrder(String userId, String outerOrderId) {
        log.info("拼团 trade 领域正在查询用户 ID: {}, 外部订单 ID: {} 是否有未支付订单...", userId, outerOrderId);
        return tradeRepository.queryUnpayOrder(userId, outerOrderId);
    }

    /**
     * 根据组队 ID 查询队伍的拼团进度
     *
     * @param teamId 组队 ID
     * @return 拼团队伍的实体类对象
     */
    @Override
    public PayOrderTeamEntity checkTeamProgress(String teamId) {
        log.info("拼团 trade 领域正在查询队伍 ID: {} 的拼团进度...", teamId);
        return tradeRepository.queryTeamProgress(teamId);
    }

    /**
     * 根据拼团订单聚合内的信息进行锁定订单操作
     *
     * @param groupBuyOrderAggregate 拼团订单聚合
     * @return 锁定订单完成后的拼团订单聚合
     */
    @Override
    public GroupBuyOrderAggregate lockGroupBuyOrder(GroupBuyOrderAggregate groupBuyOrderAggregate) {
        log.info("用户 ID: {}, 外部订单 ID: {}, 正在锁定订单...", groupBuyOrderAggregate.getUserId(), groupBuyOrderAggregate.getOuterOrderId());

        // 进行交易规则校验
        TradeLockRuleFilterRequestEntity requestParam = TradeLockRuleFilterRequestEntity.builder()
                .activityId(groupBuyOrderAggregate.getPayOrderActivityEntity().getActivityId())
                .userId(groupBuyOrderAggregate.getUserId())
                .build();
        // 返回用户已经参与了该活动的次数
        TradeLockRuleFilterResponseEntity tradeLockRuleFilterResponseEntity = tradeLockRuleFilterResponsibilityChain.apply(requestParam, new TradeLockRuleFilterFactory.DynamicContext());
        groupBuyOrderAggregate.setJoinTimes(tradeLockRuleFilterResponseEntity.getUserJoinTimes());
        // 锁定订单
        tradeRepository.lockOrder(groupBuyOrderAggregate);

        // 返回完成后的订单聚合
        return groupBuyOrderAggregate;
    }
}
