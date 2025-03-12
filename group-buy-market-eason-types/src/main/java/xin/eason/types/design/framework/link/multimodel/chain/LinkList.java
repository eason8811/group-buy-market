package xin.eason.types.design.framework.link.multimodel.chain;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 链表实现类, 实现了 {@link ILinkList}
 *
 * @param <E> 链表内部储存的对象类型
 */
@Slf4j
public class LinkList<E> implements ILinkList<E> {

    /**
     * 链表的第一个元素
     */
    protected Node<E> firstNode;
    /**
     * 链表的最后一个元素
     */
    protected Node<E> lastNode;
    /**
     * 链表的长度
     */
    protected Integer size = 0;

    /**
     * 尾插法向链表中添加元素
     *
     * @param element 需要添加的元素
     * @return 添加情况, 添加成功: true, 添加失败: false
     */
    @Override
    public boolean add(E element) {
        try {
            // 最后一个元素不存在, 则链表为空, 新增一个节点, 将 first 和 last 指向这个节点
            Node<E> newNode = new Node<>(element, null, null);
            if (lastNode == null) {
                firstNode = newNode;
                lastNode = firstNode;
                size++;
                return true;
            }

            // 最后一个元素存在, 执行尾插
            newNode.prev = lastNode;
            lastNode.next = newNode;
            lastNode = newNode;
            size++;
            return true;
        } catch (Exception e) {
            log.info("尾插法添加元素错误!", e);
            return false;
        }
    }

    /**
     * 根据索引获取链表内的元素
     *
     * @param index 索引
     * @return 元素内容
     */
    @Override
    public E get(Integer index) {
        // 索引越界则返回 null
        if (index >= size)
            return null;

        // 判断需要查找的索引在链表的前一半还是后一半
        if (index < (size >> 1)) {
            // 从前往后搜索
            Node<E> current = firstNode;
            for (int i = 0; i < index; i++) {
                current = current.next;
            }
            return current.data;
        }
        // 从后往前搜索
        Node<E> current = lastNode;
        for (int i = size - 1; i > index; i--) {
            current = current.prev;
        }
        return current.data;
    }

    /**
     * 链表节点类
     *
     * @param <E> 节点中存储的数据的类型
     */
    @AllArgsConstructor
    protected static class Node<E> {
        /**
         * 具体存储的数据
         */
        E data;
        /**
         * 前一个元素
         */
        Node<E> prev;
        /**
         * 下一个元素
         */
        Node<E> next;
    }
}
