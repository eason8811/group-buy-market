package xin.eason.domain.activity.service.trail;

import xin.eason.domain.activity.model.entity.MarketProductEntity;
import xin.eason.domain.activity.model.entity.TrailResultEntity;
import xin.eason.domain.activity.service.trail.factory.DefaultActivityStrategyFactory;
import xin.eason.types.design.framework.tree.AbstractStrategyRouter;

/**
 * 抽象拼团服务支持类
 */
public abstract class AbstractGroupBuyMarketSupport extends AbstractStrategyRouter<MarketProductEntity, DefaultActivityStrategyFactory.DynamicContext, TrailResultEntity> {

}
