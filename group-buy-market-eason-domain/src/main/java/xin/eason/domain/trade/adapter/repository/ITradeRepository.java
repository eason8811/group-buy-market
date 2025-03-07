package xin.eason.domain.trade.adapter.repository;

import xin.eason.domain.trade.model.aggregate.GroupBuyOrderAggregate;
import xin.eason.domain.trade.model.entity.PayOrderEntity;
import xin.eason.domain.trade.model.entity.PayOrderTeamEntity;

/**
 * trade 领域仓储接口
 */
public interface ITradeRepository {
    /**
     * 查询指定 用户ID, 外部订单ID 组合是否有未支付订单 orderStatus = INIT_LOCK(0, "初始锁定")
     * @param userId 用户 ID
     * @param outerOrderId 外部订单 ID
     * @return 如果存在未支付订单, 返回该订单, 否则返回 null
     */
    PayOrderEntity queryUnpayOrder(String userId, String outerOrderId);

    /**
     * 根据组队 ID 查询队伍的拼团进度
     *
     * @param teamId 组队 ID
     * @return 拼团队伍的实体类对象
     */
    PayOrderTeamEntity queryTeamProgress(String teamId);

    /**
     * 根据订单聚合内的信息进行锁单操作
     * @param groupBuyOrderAggregate 订单聚合
     */
    void lockOrder(GroupBuyOrderAggregate groupBuyOrderAggregate);
}
