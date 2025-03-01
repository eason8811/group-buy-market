package xin.eason.domain.activity.service.trail.node;

import org.springframework.stereotype.Component;
import xin.eason.domain.activity.model.entity.MarketProductEntity;
import xin.eason.domain.activity.model.entity.TrailResultEntity;
import xin.eason.domain.activity.service.trail.AbstractGroupBuyMarketSupport;
import xin.eason.domain.activity.service.trail.factory.DefaultActivityStrategyFactory;
import xin.eason.types.design.framework.tree.StrategyHandler;

/**
 * 规则树根节点
 */
@Component
public class RootNode extends AbstractGroupBuyMarketSupport {

    /**
     * 处理当前节点具体逻辑
     *
     * @param requestParam   入参
     * @param dynamicContext 动态上下文
     * @return 出参
     * @throws Exception 抛出所有错误
     */
    @Override
    public TrailResultEntity apply(MarketProductEntity requestParam, DefaultActivityStrategyFactory.DynamicContext dynamicContext) throws Exception {
        return null;
    }

    /**
     * 获取下一节点的策略处理器 StrategyHandler
     *
     * @param requestParam   入参
     * @param dynamicContext 动态上下文
     * @return 返回下一节点的策略处理器 StrategyHandler
     * @throws Exception 抛出所有错误
     */
    @Override
    public StrategyHandler<MarketProductEntity, DefaultActivityStrategyFactory.DynamicContext, TrailResultEntity> get(MarketProductEntity requestParam, DefaultActivityStrategyFactory.DynamicContext dynamicContext) throws Exception {
        return null;
    }
}
