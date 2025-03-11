package xin.eason.domain.trade.service.settlement.filter.factory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import xin.eason.domain.trade.model.entity.PayOrderEntity;
import xin.eason.domain.trade.model.entity.PayOrderTeamEntity;
import xin.eason.domain.trade.model.entity.TradeSettlementRuleFilterRequestEntity;
import xin.eason.domain.trade.model.entity.TradeSettlementRuleFilterResponseEntity;
import xin.eason.domain.trade.service.settlement.filter.node.EndFilterNode;
import xin.eason.domain.trade.service.settlement.filter.node.OutOrderIdFilterNode;
import xin.eason.domain.trade.service.settlement.filter.node.SCBlackListFilterNode;
import xin.eason.domain.trade.service.settlement.filter.node.SettlementInTimeFilterNode;
import xin.eason.types.design.framework.link.multimodel.LinkListConstructor;
import xin.eason.types.design.framework.link.multimodel.chain.BusinessLinkList;

@Configuration
public class TradeSettlementRuleFilterFactory {

    @Bean("tradeSettlementRuleFilterResponsibilityChain")
    public BusinessLinkList<TradeSettlementRuleFilterRequestEntity, TradeSettlementRuleFilterResponseEntity, TradeSettlementRuleFilterFactory.DynamicContext> createTradeSettlementRuleFilterResponsibilityChain (SCBlackListFilterNode scBlackListFilterNode, OutOrderIdFilterNode outOrderIdFilterNode, SettlementInTimeFilterNode settlementInTimeFilterNode, EndFilterNode endFilterNode) {
        LinkListConstructor<TradeSettlementRuleFilterRequestEntity, TradeSettlementRuleFilterResponseEntity, DynamicContext> linkListConstructor = new LinkListConstructor<>(scBlackListFilterNode, outOrderIdFilterNode, settlementInTimeFilterNode, endFilterNode);
        return linkListConstructor.getLogicLinkChain();
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DynamicContext {
        /**
         * 拼团订单实体
         */
        private PayOrderEntity payOrderEntity;
        /**
         * 拼团队伍实体
         */
        private PayOrderTeamEntity payOrderTeamEntity;
    }
}
