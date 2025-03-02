package xin.eason.domain.activity.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import xin.eason.domain.activity.model.entity.MarketProductEntity;
import xin.eason.domain.activity.model.entity.TrailResultEntity;
import xin.eason.domain.activity.service.trail.factory.DefaultActivityStrategyFactory;
import xin.eason.types.design.framework.tree.StrategyHandler;

/**
 * 拼团首页活动领域服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class IndexGroupBuyMarketService implements IIndexGroupBuyMarketService{

    /**
     * @see DefaultActivityStrategyFactory
     */
    private final DefaultActivityStrategyFactory defaultActivityStrategyFactory;

    /**
     * <p>进行首页优惠产品试算</p>
     * <p>出现异常则返回空的 {@link TrailResultEntity} 对象</p>
     *
     * @param marketProductEntity 营销产品实体类对象
     * @return 试算结果实体类对象
     */
    @Override
    public TrailResultEntity indexTrail(MarketProductEntity marketProductEntity) {
        StrategyHandler<MarketProductEntity, DefaultActivityStrategyFactory.DynamicContext, TrailResultEntity> strategyHandler = defaultActivityStrategyFactory.createStrategyHandler();
        try {
            return strategyHandler.apply(marketProductEntity, new DefaultActivityStrategyFactory.DynamicContext());
        }
        catch (Exception e) {
            log.error("优惠试算过程错误! ", e);
            return new TrailResultEntity();
        }
    }
}
