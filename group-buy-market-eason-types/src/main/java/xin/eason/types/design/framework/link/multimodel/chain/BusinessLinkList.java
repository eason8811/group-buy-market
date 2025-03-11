package xin.eason.types.design.framework.link.multimodel.chain;

import xin.eason.types.design.framework.link.multimodel.handler.IResponsibilityChainNodeHandler;

/**
 * 处理业务的链表
 * @param <T> 入参
 * @param <R> 出参
 * @param <D> 动态上下文
 */
public class BusinessLinkList<T, R, D> extends LinkList<IResponsibilityChainNodeHandler<T, R, D>> implements IResponsibilityChainNodeHandler<T, R, D> {

    /**
     * 责任链的入口方法, 从这里进入责任链, 遍历责任链节点的处理逻辑
     * @param requestParam 入参
     * @param dynamicContext 动态上下文
     * @return 出参
     */
    public R apply(T requestParam, D dynamicContext) {
        // 将 当前 指针指向第一个元素
        Node<IResponsibilityChainNodeHandler<T, R, D>> current = firstNode;
        // 使用 do while 遍历所有责任链的节点
        do {
            // 取出节点内容, 执行其中的逻辑
            IResponsibilityChainNodeHandler<T, R, D> data = current.data;
            R result = data.apply(requestParam, dynamicContext);
            // 如果执行逻辑的结果不为空, 则返回
            if (result != null)
                return result;
            // 当前 指针移动到下一节点
            current = current.next;
        } while (current != null);
        return null;
    }
}
