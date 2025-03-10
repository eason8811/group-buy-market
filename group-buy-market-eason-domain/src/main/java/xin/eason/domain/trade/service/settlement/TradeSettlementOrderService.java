package xin.eason.domain.trade.service.settlement;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import xin.eason.domain.trade.adapter.repository.ITradeRepository;
import xin.eason.domain.trade.model.aggregate.GroupBuyOrderAggregate;
import xin.eason.domain.trade.model.entity.OrderSettlementSuccessEntity;
import xin.eason.domain.trade.model.entity.PayOrderDiscountEntity;
import xin.eason.domain.trade.model.entity.PayOrderEntity;
import xin.eason.domain.trade.service.ITradeSettlementOrderService;

/**
 * trade 领域 <b>结算订单</b> 服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TradeSettlementOrderService implements ITradeSettlementOrderService {

    /**
     * 拼团交易 trade 领域仓储
     */
    private final ITradeRepository tradeRepository;

    /**
     * <p>根据订单聚合内的相关信息进行订单结算</p>
     * <p>返回 <b>SC 值, userId, outerOrderId, teamId, activityId</b></p>
     *
     * @param groupBuyOrderAggregate 订单聚合
     * @return 订单结算成功实体
     */
    @Override
    public OrderSettlementSuccessEntity settlementPayOrder(GroupBuyOrderAggregate groupBuyOrderAggregate) {
        log.info("拼团交易-支付订单结算, userId: {} outOrderId: {}", groupBuyOrderAggregate.getUserId(), groupBuyOrderAggregate.getOuterOrderId());
        // 根据 外部订单ID 查询数据库内是否有这个订单, 如没有, 则直接返回 null
        PayOrderEntity payOrderEntity = tradeRepository.queryUnpayOrder(groupBuyOrderAggregate.getUserId(), groupBuyOrderAggregate.getOuterOrderId());
        if (payOrderEntity == null) {
            log.error("不存在的外部交易单号或用户已退单，不需要做支付订单结算, userId: {} outOrderId: {}", groupBuyOrderAggregate.getUserId(), groupBuyOrderAggregate.getOuterOrderId());
            return null;
        }

        // 进行订单结算
        tradeRepository.settlementPayOrder(groupBuyOrderAggregate);

        PayOrderDiscountEntity discountEntity = groupBuyOrderAggregate.getPayOrderDiscountEntity();
        return OrderSettlementSuccessEntity.builder()
                .source(discountEntity.getSource())
                .channel(discountEntity.getChannel())
                .userId(groupBuyOrderAggregate.getUserId())
                .outTradeNo(groupBuyOrderAggregate.getOuterOrderId())
                .teamId(groupBuyOrderAggregate.getPayOrderTeamEntity().getTeamId())
                .activityId(groupBuyOrderAggregate.getPayOrderActivityEntity().getActivityId())
                .build();
    }
}
