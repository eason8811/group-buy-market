package xin.eason.types.design.framework.tree;

/**
 * 抽象策略路由器
 * @param <T> 入参类型
 * @param <D> 动态上下文类型
 * @param <R> 出参类型
 */
public abstract class AbstractStrategyRouter<T, D, R> implements StrategyHandler<T, D, R>, StrategyMapper<T, D, R> {

    /**
     * StrategyHandler 接口中的默认实现
     */
    private final StrategyHandler<T, D, R> defaultHandler = StrategyHandler.DEFAULT;

    R router(T requestParam, D dynamicContext) throws Exception {
        StrategyHandler<T, D, R> handler = get(requestParam, dynamicContext);
        if (handler != null)
            return handler.apply(requestParam, dynamicContext);
        return defaultHandler.apply(requestParam, dynamicContext);
    }
}
