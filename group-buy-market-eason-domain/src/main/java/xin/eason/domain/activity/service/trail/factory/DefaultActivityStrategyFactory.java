package xin.eason.domain.activity.service.trail.factory;

import lombok.*;
import org.springframework.stereotype.Component;
import xin.eason.domain.activity.model.entity.MarketProductEntity;
import xin.eason.domain.activity.model.entity.TrailResultEntity;
import xin.eason.domain.activity.service.trail.node.RootNode;
import xin.eason.types.design.framework.tree.StrategyHandler;

/**
 * <p>默认活动策略工厂</p>
 * <p>( 简单工厂模式 )</p>
 * <p><b>createStrategyHandler</b> 方法提供一个默认的入口节点</p>
 */
@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
@Component
public class DefaultActivityStrategyFactory {

    private final RootNode rootNode;

    public StrategyHandler<MarketProductEntity, DefaultActivityStrategyFactory.DynamicContext, TrailResultEntity> createStrategyHandler() {return rootNode;}

    /**
     * 动态上下文内部类
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DynamicContext {

    }
}
