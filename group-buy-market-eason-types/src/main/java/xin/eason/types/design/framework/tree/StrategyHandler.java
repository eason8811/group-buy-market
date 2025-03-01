package xin.eason.types.design.framework.tree;

/**
 * 策略处理器
 * @param <T> 入参类型
 * @param <D> 动态上下文类型
 * @param <R> 出参类型
 */
public interface StrategyHandler<T, D, R> {

    /**
     * <p>为当前接口的 apply 函数提供一个默认实现</p>
     * <p>DEFAULT 为一个 StrategyHandler 接口的实现类对象</p>
     * <p>内部有 apply 方法, 方法返回值固定为 null</p>
     */
    StrategyHandler DEFAULT = (requestParam, dynamicContext) -> null;

    /**
     * 处理当前节点具体逻辑
     * @param requestParam 入参
     * @param dynamicContext 动态上下文
     * @exception Exception 抛出所有错误
     * @return 出参
     */
    R apply(T requestParam, D dynamicContext) throws Exception;
}
