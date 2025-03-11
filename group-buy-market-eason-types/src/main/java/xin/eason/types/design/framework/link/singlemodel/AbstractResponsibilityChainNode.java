package xin.eason.types.design.framework.link.singlemodel;

/**
 * <p>抽象责任链节点</p>
 * <p><b>此为抽象模版类</b>, 实现了 {@link IResponsibilityChainNode} 责任链节点接口, 为子类提供一个统一的模版</p>
 * @param <T> 入参
 * @param <R> 出参
 * @param <D> 动态上下文
 */
public abstract class AbstractResponsibilityChainNode<T, R, D> implements IResponsibilityChainNode<T, R, D> {
    protected IResponsibilityChainNode<T, R, D> next;

    /**
     * 获取下一节点
     *
     * @return 下一责任链节点
     */
    @Override
    public IResponsibilityChainNode<T, R, D> next() {
        return next;
    }

    /**
     * 尾接法添加一个责任链节点
     *
     * @param next 需要添加的下一个节点
     * @return 刚刚添加的责任链节点
     */
    @Override
    public IResponsibilityChainNode<T, R, D> appendNext(IResponsibilityChainNode<T, R, D> next) {
        this.next = next;
        return next;
    }

    /**
     * 流转到下一个节点
     * @param requestParam 入参
     * @param dynamicContext 动态上下文
     * @return 出参
     */
    protected R routeNextNode(T requestParam, D dynamicContext) {
        return next().apply(requestParam, dynamicContext);
    }
}
