package xin.eason.domain.trade.service;

import xin.eason.domain.trade.model.aggregate.GroupBuyOrderAggregate;
import xin.eason.domain.trade.model.entity.PayOrderEntity;
import xin.eason.domain.trade.model.entity.PayOrderTeamEntity;

/**
 * trade 领域服务接口
 */
public interface ITradeService {
    /**
     * 查询未支付订单
     * @param userId 用户 ID
     * @param outerOrderId 外部订单 ID
     * @return 拼团订单实体
     */
    PayOrderEntity checkUnpayOrder(String userId, String outerOrderId);

    /**
     * 根据组队 ID 查询队伍的拼团进度
     * @param teamId 组队 ID
     * @return 拼团队伍实体
     */
    PayOrderTeamEntity checkTeamProgress(String teamId);

    /**
     * 根据拼团订单聚合内的信息进行锁定订单操作
     * @param groupBuyOrderAggregate 拼团订单聚合
     * @return 锁定订单完成后的拼团订单聚合
     */
    GroupBuyOrderAggregate lockGroupBuyOrder(GroupBuyOrderAggregate groupBuyOrderAggregate);
}
