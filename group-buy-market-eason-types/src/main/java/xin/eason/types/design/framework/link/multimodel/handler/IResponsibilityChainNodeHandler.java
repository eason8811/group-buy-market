package xin.eason.types.design.framework.link.multimodel.handler;

/**
 * 节点的逻辑处理接口, 所有实现类都要重写 {@link #apply} 方法, 以处理节点内的具体逻辑
 * @param <T> 入参
 * @param <R> 出参
 * @param <D> 动态上下文
 */
public interface IResponsibilityChainNodeHandler<T, R, D> {
    /**
     * 进入下一个逻辑的方法
     * @param requestParameter 入参
     * @param dynamicContext 动态上下文
     * @return 出参
     */
    default R next(T requestParameter, D dynamicContext) {
        return null;
    }
    /**
     * 处理节点内的逻辑
     * @param requestParam 入参
     * @param dynamicContext 动态上下文
     * @return 出参
     */
    R apply(T requestParam, D dynamicContext);
}
