package xin.eason.types.design.framework.tree;

/**
 * 抽象策略路由器 (多线程版)
 * @param <T> 入参类型
 * @param <D> 动态上下文类型
 * @param <R> 出参类型
 */
public abstract class AbstractMultiThreadStrategyRouter<T, D, R> implements StrategyHandler<T, D, R>, StrategyMapper<T, D, R>{
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

    /**
     * 处理当前节点具体逻辑
     *
     * @param requestParam   入参
     * @param dynamicContext 动态上下文
     * @return 出参
     * @throws Exception 抛出所有错误
     */
    @Override
    public R apply(T requestParam, D dynamicContext) throws Exception {
        multiThread(requestParam, dynamicContext);
        return doApply(requestParam, dynamicContext);
    }

    /**
     * 抽象方法, 用于多线程加载数据
     * @param requestParam 入参
     * @param dynamicContext 动态上下文
     */
    protected abstract void multiThread(T requestParam, D dynamicContext) throws Exception;

    /**
     * 抽象方法, 用于处理实际的策略逻辑
     * @param requestParam 入参
     * @param dynamicContext 动态上下文
     * @return 出参
     */
    protected abstract R doApply(T requestParam, D dynamicContext) throws Exception;


}
