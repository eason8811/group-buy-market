package xin.eason.domain.activity.service.trail.factory;

import lombok.*;
import org.springframework.stereotype.Component;
import xin.eason.domain.activity.model.entity.MarketProductEntity;
import xin.eason.domain.activity.model.entity.TrailResultEntity;
import xin.eason.domain.activity.model.valobj.GroupBuyActivityDiscountVO;
import xin.eason.domain.activity.model.valobj.SkuVO;
import xin.eason.domain.activity.service.trail.node.RootNode;
import xin.eason.types.design.framework.tree.StrategyHandler;

import java.math.BigDecimal;

/**
 * <p>默认活动策略工厂</p>
 * <p>( 简单工厂模式 )</p>
 * <p>{@link #createStrategyHandler} 方法提供一个默认的入口节点</p>
 */
@Data
@Builder
@RequiredArgsConstructor
@Component
public class DefaultActivityStrategyFactory {

    /**
     * 规则树起点节点
     */
    private final RootNode rootNode;

    /**
     * 为规则树提供一个固定的统一入口 {@link RootNode}
     * @return 规则树起点节点
     */
    public StrategyHandler<MarketProductEntity, DefaultActivityStrategyFactory.DynamicContext, TrailResultEntity> createStrategyHandler() {return rootNode;}

    /**
     * 动态上下文内部类
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DynamicContext {
        /**
         * @see GroupBuyActivityDiscountVO
         */
        private GroupBuyActivityDiscountVO groupBuyActivityDiscountVO;
        /**
         * @see SkuVO
         */
        private SkuVO skuVO;
        /**
         * @see TrailResultEntity#deductionPrice
         */
        private BigDecimal deductionPrice;
        /**
         * 记录运行中的错误
         */
        private RuntimeException exception;
    }
}
