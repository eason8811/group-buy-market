package xin.eason.domain.trade.service.settlement.filter.node;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import xin.eason.domain.trade.adapter.repository.ITradeRepository;
import xin.eason.domain.trade.model.entity.PayOrderEntity;
import xin.eason.domain.trade.model.entity.TradeSettlementRuleFilterRequestEntity;
import xin.eason.domain.trade.model.entity.TradeSettlementRuleFilterResponseEntity;
import xin.eason.domain.trade.service.settlement.filter.factory.TradeSettlementRuleFilterFactory;
import xin.eason.types.design.framework.link.multimodel.handler.IResponsibilityChainNodeHandler;
import xin.eason.types.exception.OutOrderNoExistException;

/**
 * 外部订单 ID 存在性过滤节点
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OutOrderIdFilterNode implements IResponsibilityChainNodeHandler<TradeSettlementRuleFilterRequestEntity, TradeSettlementRuleFilterResponseEntity, TradeSettlementRuleFilterFactory.DynamicContext> {
    /**
     * 交易 repository 仓储适配器接口
     */
    private final ITradeRepository tradeRepository;

    /**
     * 校验 外部订单ID 是否存在
     *
     * @param requestParam   入参
     * @param dynamicContext 动态上下文
     * @return 出参
     */
    @Override
    public TradeSettlementRuleFilterResponseEntity apply(TradeSettlementRuleFilterRequestEntity requestParam, TradeSettlementRuleFilterFactory.DynamicContext dynamicContext) {
        String userId = requestParam.getUserId();
        String outOrderId = requestParam.getOutOrderId();
        log.info("当前责任链节点: {} 正在校验 外部订单ID 是否存在, userId: {}, outOrderId: {}", this.getClass().getSimpleName(), userId, outOrderId);

        // 根据 外部订单ID 查询数据库内是否有这个订单, 如没有, 则直接返回 null
        PayOrderEntity payOrderEntity = tradeRepository.queryUnpayOrder(userId, outOrderId);
        if (payOrderEntity == null)
            throw new OutOrderNoExistException("外部订单编号不存在! outOrderId: " + outOrderId + ", userId: " + userId);

        //写入动态上下文
        dynamicContext.setPayOrderEntity(payOrderEntity);

        log.info("当前责任链节点: {} 校验已通过!", this.getClass().getSimpleName());
        return next(requestParam, dynamicContext);
    }
}
