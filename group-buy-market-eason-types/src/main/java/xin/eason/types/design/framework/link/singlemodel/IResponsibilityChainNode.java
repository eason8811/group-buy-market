package xin.eason.types.design.framework.link.singlemodel;

/**
 * <p>责任链节点接口</p>
 * <p>责任链中每一个节点都要提供一个 {@link #apply} 方法用于处理责任链节点的逻辑</p>
 * <p>只有一个 <b>节点</b> 的责任链也是责任链, 因此 <b>责任链节点接口</b> 要继承 {@link IResponsibilityChainLinkList} 责任链接口</p>
 * @param <T> 入参
 * @param <R> 出参
 * @param <D> 动态上下文
 */
public interface IResponsibilityChainNode<T, R, D> extends IResponsibilityChainLinkList<T, R, D> {
    /**
     * 处理责任链节点的逻辑
     * @param requestParam 入参
     * @param dynamicContext 动态上下文
     * @return 出参
     */
    R apply(T requestParam, D dynamicContext);
}
