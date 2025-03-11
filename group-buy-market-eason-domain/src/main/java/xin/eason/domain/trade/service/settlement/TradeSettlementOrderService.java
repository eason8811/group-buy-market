package xin.eason.domain.trade.service.settlement;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import xin.eason.domain.trade.adapter.repository.ITradeRepository;
import xin.eason.domain.trade.model.aggregate.GroupBuyOrderAggregate;
import xin.eason.domain.trade.model.entity.*;
import xin.eason.domain.trade.service.ITradeSettlementOrderService;
import xin.eason.domain.trade.service.settlement.filter.factory.TradeSettlementRuleFilterFactory;
import xin.eason.types.design.framework.link.multimodel.chain.BusinessLinkList;

import java.time.LocalDateTime;

/**
 * trade 领域 <b>结算订单</b> 服务
 */
@Slf4j
@Service
public class TradeSettlementOrderService implements ITradeSettlementOrderService {

    /**
     * 拼团交易 trade 领域仓储
     */
    private final ITradeRepository tradeRepository;
    /**
     * 交易结算规则过滤责任链对象
     */
    private final BusinessLinkList<TradeSettlementRuleFilterRequestEntity, TradeSettlementRuleFilterResponseEntity, TradeSettlementRuleFilterFactory.DynamicContext> tradeSettlementRuleFilterResponsibilityChain;

    public TradeSettlementOrderService(ITradeRepository tradeRepository, @Qualifier("tradeSettlementRuleFilterResponsibilityChain") BusinessLinkList<TradeSettlementRuleFilterRequestEntity, TradeSettlementRuleFilterResponseEntity, TradeSettlementRuleFilterFactory.DynamicContext> tradeSettlementRuleFilterResponsibilityChain) {
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
        tradeRepository.settlementPayOrder(groupBuyOrderAggregate);

        return OrderSettlementSuccessEntity.builder()
                .source(orderSettlementEntity.getSource())
                .channel(orderSettlementEntity.getChannel())
                .userId(orderSettlementEntity.getUserId())
                .outTradeNo(orderSettlementEntity.getOuterOrderId())
                .teamId(groupBuyOrderAggregate.getPayOrderTeamEntity().getTeamId())
                .activityId(groupBuyOrderAggregate.getPayOrderActivityEntity().getActivityId())
                .build();
    }
}
