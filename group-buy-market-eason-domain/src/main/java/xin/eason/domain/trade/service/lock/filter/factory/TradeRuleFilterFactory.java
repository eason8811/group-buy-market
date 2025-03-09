package xin.eason.domain.trade.service.lock.filter.factory;

import lombok.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import xin.eason.domain.trade.model.entity.GroupBuyActivityEntity;
import xin.eason.domain.trade.model.entity.TradeRuleFilterRequestEntity;
import xin.eason.domain.trade.model.entity.TradeRuleFilterResponseEntity;
import xin.eason.domain.trade.service.lock.filter.node.ActivityAvailableFilterNode;
import xin.eason.domain.trade.service.lock.filter.node.UserJoinLimitFilterNode;
import xin.eason.types.design.framework.link.multimodel.LinkChainConstructor;
import xin.eason.types.design.framework.link.multimodel.chain.BusinessLinkChain;

/**
 * <p>交易规则过滤责任链工厂</p>
 * <p>用于构造责任链 <b>Bean</b> 对象</p>
 */
@Configuration
public class TradeRuleFilterFactory {

    /**
     * 向 SpringBoot 提交 Bean 对象
     * @param activityAvailableFilterNode 活动可用性过滤责任链节点
     * @param userJoinLimitFilterNode 用户参与次数过滤责任链节点
     * @return 责任链
     */
    @Bean("tradeRuleFilterResponsibilityChain")
    public BusinessLinkChain<TradeRuleFilterRequestEntity, TradeRuleFilterResponseEntity, TradeRuleFilterFactory.DynamicContext> createTradeRuleFilterResponsibilityChain(ActivityAvailableFilterNode activityAvailableFilterNode, UserJoinLimitFilterNode userJoinLimitFilterNode) {
        LinkChainConstructor<TradeRuleFilterRequestEntity, TradeRuleFilterResponseEntity, DynamicContext> tradeRuleFilterResponsibilityChain = new LinkChainConstructor<>(activityAvailableFilterNode, userJoinLimitFilterNode);
        return tradeRuleFilterResponsibilityChain.getLogicLinkChain();
    }

    /**
     * 动态上下文
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DynamicContext{
        private GroupBuyActivityEntity groupBuyActivityEntity;
    }
}
