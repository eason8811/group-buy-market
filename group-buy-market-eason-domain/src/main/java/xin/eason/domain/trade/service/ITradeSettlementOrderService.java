package xin.eason.domain.trade.service;

import xin.eason.domain.trade.model.entity.OrderSettlementEntity;
import xin.eason.domain.trade.model.entity.OrderSettlementSuccessEntity;

/**
 * trade 领域 <b>结算订单</b> 服务接口
 */
public interface ITradeSettlementOrderService {
    /**
     * <p>根据订单结算实体的相关信息进行订单结算</p>
     * <p>返回 <b>SC 值, userId, outerOrderId, teamId, activityId</b></p>
     *
     * @param orderSettlementEntity 订单结算实体
     * @return 订单结算成功实体
     */
    OrderSettlementSuccessEntity settlementPayOrder(OrderSettlementEntity orderSettlementEntity);
}
