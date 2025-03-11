package xin.eason.domain.activity.service.trail.node;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import xin.eason.domain.activity.model.entity.MarketProductEntity;
import xin.eason.domain.activity.model.entity.TrailResultEntity;
import xin.eason.domain.activity.model.valobj.GroupBuyActivityDiscountVO;
import xin.eason.domain.activity.model.valobj.SkuVO;
import xin.eason.domain.activity.service.trail.AbstractGroupBuyMarketSupport;
import xin.eason.domain.activity.service.trail.factory.DefaultActivityStrategyFactory;
import xin.eason.types.design.framework.tree.StrategyHandler;

/**
 * <p>规则树收尾节点</p>
 * <p>主要做数据组装工作</p>
 */
@Slf4j
@Component
public class EndNode extends AbstractGroupBuyMarketSupport {
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
        log.info("拼团商品优惠试算规则树 -> {}, userId: {}, requestParam: {}", this.getClass().getSimpleName(), requestParam.getUserId(), requestParam);
        SkuVO skuVO = dynamicContext.getSkuVO();
        GroupBuyActivityDiscountVO groupBuyActivityDiscountVO = dynamicContext.getGroupBuyActivityDiscountVO();
        return TrailResultEntity.builder()
                .goodsId(requestParam.getGoodsId())
                .goodsName(skuVO.getGoodsName())
                .originalPrice(skuVO.getOriginalPrice())
                .deductionPrice(dynamicContext.getDeductionPrice())
                .payPrice(dynamicContext.getPayPrice())
                .targetCount(groupBuyActivityDiscountVO.getTarget())
                .startTime(groupBuyActivityDiscountVO.getStartTime())
                .endTime(groupBuyActivityDiscountVO.getEndTime())
                .validTime(groupBuyActivityDiscountVO.getValidTime())
                .isVisible(dynamicContext.getIsVisible())
                .isEnable(dynamicContext.getIsParticipable())
                .build();
    }

    /**
     * 获取下一节点的策略处理器 {@link StrategyHandler}
     *
     * @param requestParam   入参
     * @param dynamicContext 动态上下文
     * @return {@link StrategyHandler} 的默认实现
     * @throws Exception 抛出所有错误
     */
    @Override
    public StrategyHandler<MarketProductEntity, DefaultActivityStrategyFactory.DynamicContext, TrailResultEntity> get(MarketProductEntity requestParam, DefaultActivityStrategyFactory.DynamicContext dynamicContext) throws Exception {
        return defaultHandler;
    }
}
