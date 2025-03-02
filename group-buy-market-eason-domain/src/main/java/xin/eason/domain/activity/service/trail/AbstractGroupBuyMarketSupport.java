package xin.eason.domain.activity.service.trail;

import xin.eason.domain.activity.model.entity.MarketProductEntity;
import xin.eason.domain.activity.model.entity.TrailResultEntity;
import xin.eason.domain.activity.service.trail.factory.DefaultActivityStrategyFactory;
import xin.eason.types.design.framework.tree.AbstractMultiThreadStrategyRouter;

/**
 * 抽象拼团服务支持类
 */
public abstract class AbstractGroupBuyMarketSupport extends AbstractMultiThreadStrategyRouter<MarketProductEntity, DefaultActivityStrategyFactory.DynamicContext, TrailResultEntity> {
    /**
     * 抽象方法, 用于多线程加载数据 (默认实现)
     *
     * @param requestParam   入参
     * @param dynamicContext 动态上下文
     */
    @Override
    protected void multiThread(MarketProductEntity requestParam, DefaultActivityStrategyFactory.DynamicContext dynamicContext) throws Exception {

    }
}
