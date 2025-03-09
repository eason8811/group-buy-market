package xin.eason.types.design.framework.link.multimodel;

import xin.eason.types.design.framework.link.multimodel.chain.BusinessLinkChain;
import xin.eason.types.design.framework.link.multimodel.handler.ILogicChainNodeHandler;

/**
 * 责任链构造器, 专门用来构造责任链
 */
public class LinkChainConstructor<T, R, D> {
    /**
     * 处理业务的链表
     */
    private final BusinessLinkChain<T, R, D> businessLinkChain;

    @SafeVarargs
    public LinkChainConstructor(ILogicChainNodeHandler<T, R, D> ...logicChainNodeHandlers) {
        businessLinkChain = new BusinessLinkChain<>();
        for (ILogicChainNodeHandler<T, R, D> handler : logicChainNodeHandlers) {
            businessLinkChain.add(handler);
        }
    }

    public BusinessLinkChain<T, R, D> getLogicLinkChain() {
        return businessLinkChain;
    }

}
