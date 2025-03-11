package xin.eason.domain.trade.service.settlement.filter.node;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import xin.eason.domain.trade.adapter.repository.ITradeRepository;
import xin.eason.domain.trade.model.entity.TradeSettlementRuleFilterRequestEntity;
import xin.eason.domain.trade.model.entity.TradeSettlementRuleFilterResponseEntity;
import xin.eason.domain.trade.service.settlement.filter.factory.TradeSettlementRuleFilterFactory;
import xin.eason.types.design.framework.link.multimodel.handler.IResponsibilityChainNodeHandler;
import xin.eason.types.exception.SCValueInBlackListException;

/**
 * SC 值黑名单过滤节点
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SCBlackListFilterNode implements IResponsibilityChainNodeHandler<TradeSettlementRuleFilterRequestEntity, TradeSettlementRuleFilterResponseEntity, TradeSettlementRuleFilterFactory.DynamicContext> {
    /**
     * 交易 repository 仓储适配器接口
     */
    private final ITradeRepository tradeRepository;

    /**
     * 校验 SC 值是否处于黑名单中
     *
     * @param requestParam   入参
     * @param dynamicContext 动态上下文
     * @return 出参
     */
    @Override
    public TradeSettlementRuleFilterResponseEntity apply(TradeSettlementRuleFilterRequestEntity requestParam, TradeSettlementRuleFilterFactory.DynamicContext dynamicContext) {
        String source = requestParam.getSource();
        String channel = requestParam.getChannel();
        log.info("当前责任链节点: {} 正在校验 SC值 是否处于黑名单中, source: {}, channel: {}", this.getClass().getSimpleName(), source, channel);
        if (tradeRepository.SCBlackList(source, channel))
            throw new SCValueInBlackListException("source: " + source + " , channel: " + channel + " 处于黑名单中! 已拦截订单结算流程!");

        log.info("当前责任链节点: {} 校验已通过!", this.getClass().getSimpleName());
        return next(requestParam, dynamicContext);
    }
}
