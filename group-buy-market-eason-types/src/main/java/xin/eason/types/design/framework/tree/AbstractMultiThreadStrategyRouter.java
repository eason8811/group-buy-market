package xin.eason.types.design.framework.tree;

import lombok.extern.slf4j.Slf4j;

/**
 * 抽象策略路由器 (多线程版)
 * @param <T> 入参类型
 * @param <D> 动态上下文类型
 * @param <R> 出参类型
 */
@Slf4j
public abstract class AbstractMultiThreadStrategyRouter<T, D, R> implements StrategyHandler<T, D, R>, StrategyMapper<T, D, R>{
    /**
     * StrategyHandler 接口中的默认实现
     */
    protected final StrategyHandler<T, D, R> defaultHandler = StrategyHandler.DEFAULT;

    /**
     * 默认路由方法, 若下一节点不为空, 则执行下一节点逻辑, 否则执行 {@link StrategyHandler} 接口中的默认实现
     * @param requestParam 入参
     * @param dynamicContext 动态上下文
     * @return 节点 {@link #apply} 方法执行结果
     * @throws Exception 抛出任何错误
     */
    public R router(T requestParam, D dynamicContext) throws Exception {
        StrategyHandler<T, D, R> handler = get(requestParam, dynamicContext);
        if (handler != null){
            log.info("当前节点 {} -> 下一节点 {}", this.getClass().getSimpleName(), handler.getClass().getSimpleName());
            return handler.apply(requestParam, dynamicContext);
        }
        log.info("当前节点 {} -> 下一节点 默认节点", this.getClass().getSimpleName());
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
