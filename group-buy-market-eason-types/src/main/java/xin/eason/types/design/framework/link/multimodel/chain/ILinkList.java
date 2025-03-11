package xin.eason.types.design.framework.link.multimodel.chain;

/**
 * 链表接口, 对链表的实现提供规范
 * @param <E> 链表内部储存的对象类型
 */
public interface ILinkList<E> {
    /**
     * 向链表中添加元素
     * @param element 需要添加的元素
     * @return 添加情况, 添加成功: true, 添加失败: false
     */
    boolean add(E element);

    /**
     * 根据索引获取链表内的元素
     * @param index 索引
     * @return 元素内容
     */
    E get(Integer index);
}
