package xin.eason.domain.activity.service.trail.node;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import xin.eason.domain.activity.model.entity.MarketProductEntity;
import xin.eason.domain.activity.model.entity.TrailResultEntity;
import xin.eason.domain.activity.service.trail.AbstractGroupBuyMarketSupport;
import xin.eason.domain.activity.service.trail.factory.DefaultActivityStrategyFactory;
import xin.eason.types.design.framework.tree.StrategyHandler;

/**
 * 规则树开关节点
 */
@Component
@RequiredArgsConstructor
public class SwitchNode extends AbstractGroupBuyMarketSupport {

    /**
     * 规则树人群标签处理节点
     */
    private final TagNode tagNode;
    /**
     * 处理错误的节点
     */
    private final ErrorNode errorNode;

    /**
     * 处理当前节点具体逻辑
     *
     * @param requestParam   入参
     * @param dynamicContext 动态上下文
     * @return 出参
     * @throws Exception 抛出所有错误
     */
    @Override
    public TrailResultEntity doApply(MarketProductEntity requestParam, DefaultActivityStrategyFactory.DynamicContext dynamicContext) throws Exception {
        return router(requestParam, dynamicContext);
    }

    /**
     * 获取下一节点的策略处理器 {@link StrategyHandler}
     *
     * @param requestParam   入参
     * @param dynamicContext 动态上下文
     * @return 下一节点的 {@link MarketNode} 节点对象
     * @throws Exception 抛出所有错误
     */
    @Override
    public StrategyHandler<MarketProductEntity, DefaultActivityStrategyFactory.DynamicContext, TrailResultEntity> get(MarketProductEntity requestParam, DefaultActivityStrategyFactory.DynamicContext dynamicContext) throws Exception {
        if (dynamicContext.getException() != null)
            return errorNode;
        return tagNode;
    }
}
