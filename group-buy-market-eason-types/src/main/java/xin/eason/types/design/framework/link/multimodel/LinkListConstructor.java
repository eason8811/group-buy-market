package xin.eason.types.design.framework.link.multimodel;

import xin.eason.types.design.framework.link.multimodel.chain.BusinessLinkList;
import xin.eason.types.design.framework.link.multimodel.handler.IResponsibilityChainNodeHandler;

/**
 * 责任链构造器, 专门用来构造责任链
 */
public class LinkListConstructor<T, R, D> {
    /**
     * 处理业务的链表
     */
    private final BusinessLinkList<T, R, D> businessLinkList;

    @SafeVarargs
    public LinkListConstructor(IResponsibilityChainNodeHandler<T, R, D>...logicChainNodeHandlers) {
        businessLinkList = new BusinessLinkList<>();
        for (IResponsibilityChainNodeHandler<T, R, D> handler : logicChainNodeHandlers) {
            businessLinkList.add(handler);
        }
    }

    public BusinessLinkList<T, R, D> getLogicLinkChain() {
        return businessLinkList;
    }

}
