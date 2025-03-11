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

/**
 * 责任链收尾节点
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class EndFilterNode implements IResponsibilityChainNodeHandler<TradeSettlementRuleFilterRequestEntity, TradeSettlementRuleFilterResponseEntity, TradeSettlementRuleFilterFactory.DynamicContext> {
    /**
     * 交易 repository 仓储适配器接口
     */
    private final ITradeRepository tradeRepository;

    /**
     * 责任链收尾节点, 用于组装返回参数
     *
     * @param requestParam   入参
     * @param dynamicContext 动态上下文
     * @return 出参
     */
    @Override
    public TradeSettlementRuleFilterResponseEntity apply(TradeSettlementRuleFilterRequestEntity requestParam, TradeSettlementRuleFilterFactory.DynamicContext dynamicContext) {
        log.info("当前责任链节点: {} 正在进行收尾工作 组装参数", this.getClass().getSimpleName());
        PayOrderTeamEntity teamEntity = dynamicContext.getPayOrderTeamEntity();
        Long activityId = tradeRepository.queryActivityInfo(teamEntity.getTeamId()).getActivityId();

        log.info("当前责任链节点: {} 校验已通过!", this.getClass().getSimpleName());
        log.info("当前责任链流程已结束");
        return TradeSettlementRuleFilterResponseEntity.builder()
                .teamId(teamEntity.getTeamId())
                .activityId(activityId)
                .teamProgress(teamEntity.getTeamProgress())
                .status(teamEntity.getOrderStatus())
                .validStartTime(teamEntity.getValidStartTime())
                .validEndTime(teamEntity.getValidEndTime())
                .build();

    }
}
