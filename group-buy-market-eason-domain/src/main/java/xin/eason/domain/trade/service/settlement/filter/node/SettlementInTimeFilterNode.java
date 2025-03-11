package xin.eason.domain.trade.service.settlement.filter.node;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import xin.eason.domain.trade.adapter.repository.ITradeRepository;
import xin.eason.domain.trade.model.entity.PayOrderTeamEntity;
import xin.eason.domain.trade.model.entity.TradeSettlementRuleFilterRequestEntity;
import xin.eason.domain.trade.model.entity.TradeSettlementRuleFilterResponseEntity;
import xin.eason.domain.trade.service.settlement.filter.factory.TradeSettlementRuleFilterFactory;
import xin.eason.types.design.framework.link.multimodel.handler.IResponsibilityChainNodeHandler;
import xin.eason.types.exception.PayTimeOutOfTimeException;

import java.time.LocalDateTime;

/**
 * 结算是否处于可用时间段过滤节点
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SettlementInTimeFilterNode implements IResponsibilityChainNodeHandler<TradeSettlementRuleFilterRequestEntity, TradeSettlementRuleFilterResponseEntity, TradeSettlementRuleFilterFactory.DynamicContext> {
    /**
     * 交易 repository 仓储适配器接口
     */
    private final ITradeRepository tradeRepository;

    /**
     * 校验结算时是否处于拼团订单可用时间段
     *
     * @param requestParam   入参
     * @param dynamicContext 动态上下文
     * @return 出参
     */
    @Override
    public TradeSettlementRuleFilterResponseEntity apply(TradeSettlementRuleFilterRequestEntity requestParam, TradeSettlementRuleFilterFactory.DynamicContext dynamicContext) {
        LocalDateTime payTime = requestParam.getPayTime();
        String userId = requestParam.getUserId();
        String outOrderId = requestParam.getOutOrderId();
        log.info("当前责任链节点: {} 正在校验 支付时间 是否在可用时间内, payTime: {}, userId: {}, outOrderId: {}", this.getClass().getSimpleName(), payTime, userId, outOrderId);

        // 根据 userId 和 outerOrderId 获取订单明细所属的订单的相关信息
        PayOrderTeamEntity teamEntity = tradeRepository.queryTeamInfo(userId, outOrderId);

        if (payTime.isBefore(teamEntity.getValidStartTime()) || payTime.isAfter(teamEntity.getValidEndTime()))
            throw new PayTimeOutOfTimeException("userId: " + userId + ", outOrderId: " + outOrderId + ", teamId: " + teamEntity.getTeamId() + "支付时间不在队伍合法时间内!");

        // 写入动态上下文
        dynamicContext.setPayOrderTeamEntity(teamEntity);

        log.info("当前责任链节点: {} 校验已通过!", this.getClass().getSimpleName());
        return next(requestParam, dynamicContext);
    }
}
