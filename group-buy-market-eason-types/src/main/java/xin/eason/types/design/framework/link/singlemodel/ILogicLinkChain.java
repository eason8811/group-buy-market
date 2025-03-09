package xin.eason.types.design.framework.link.singlemodel;

/**
 * <p>责任链接口</p>
 * <p>提供用于组装责任链的 {@link #appendNext} 方法, 和获取下一节点的 {@link #next} 方法</p>
 * <p>只有一个 <b>节点</b> 的责任链也是责任链, 因此 {@link ILogicLinkChain} 责任链节点接口 要继承 责任链接口</p>
 * @param <T> 入参
 * @param <R> 出参
 * @param <D> 动态上下文
 */
public interface ILogicLinkChain<T, R, D> {

    /**
     * 获取下一节点
     * @return 下一责任链节点
     */
    ILogicLinkChainNode<T, R, D> next();

    /**
     * 尾接法添加一个责任链节点
     * @param next 需要添加的下一个节点
     * @return 刚刚添加的责任链节点
     */
    ILogicLinkChainNode<T, R, D> appendNext(ILogicLinkChainNode<T, R, D> next);
}
