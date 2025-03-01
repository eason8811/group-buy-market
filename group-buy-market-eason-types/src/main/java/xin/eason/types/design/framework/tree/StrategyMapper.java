package xin.eason.types.design.framework.tree;

/**
 * 策略映射器
 * @param <T> 入参类型
 * @param <D> 动态上下文类型
 * @param <R> 出参类型
 */
public interface StrategyMapper<T, D, R> {

    /**
     * 获取下一节点的策略处理器 StrategyHandler
     * @param requestParam 入参
     * @param dynamicContext 动态上下文
     * @return 返回下一节点的策略处理器 StrategyHandler
     * @throws Exception 抛出所有错误
     */
    StrategyHandler<T, D, R> get(T requestParam, D dynamicContext) throws Exception;

}
