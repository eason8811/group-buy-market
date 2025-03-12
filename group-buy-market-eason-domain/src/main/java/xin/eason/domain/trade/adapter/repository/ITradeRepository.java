package xin.eason.domain.trade.adapter.repository;

import xin.eason.domain.trade.model.aggregate.GroupBuyOrderAggregate;
import xin.eason.domain.trade.model.entity.*;

import java.util.List;

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

    /**
     * 根据 activityId 获取活动实体对象
     * @param activityId 活动 ID
     * @return 活动实体对象
     */
    GroupBuyActivityEntity queryActivityByActivityId(Long activityId);

    /**
     * 根据 activityId 和 userId 获取用户参与一个活动的次数
     * @param activityId 活动 ID
     * @param userId 用户 ID
     * @return 用户参与活动次数
     */
    Long queryUserJoinActivityTimes(Long activityId, String userId);

    /**
     * 进行订单结算具体操作
     * @param groupBuyOrderAggregate 订单聚合
     * @return 是否需要回调
     */
    Boolean settlementPayOrder(GroupBuyOrderAggregate groupBuyOrderAggregate);

    /**
     * 根据 source 和 channel 校验是否属于黑名单内
     * @param source 来源
     * @param channel 渠道
     * @return SC值 是否处于黑名单内
     */
    Boolean SCBlackList(String source, String channel);

    /**
     * 根据 userId 和 outOrderId 查询所属的队伍信息
     * @param userId 用户 ID
     * @param outOrderId 外部订单 ID
     * @return 所属队伍信息
     */
    PayOrderTeamEntity queryTeamInfo(String userId, String outOrderId);

    /**
     * 根据 teamId 获取 activityId
     *
     * @param teamId 队伍 ID
     * @return 订单活动实体
     */
    PayOrderActivityEntity queryActivityInfo(String teamId);

    /**
     * 查询所有未回调的回调任务
     * @return 回调任务列表
     */
    List<NotifyTaskEntity> queryNoNotifyTaskList();

    /**
     * 查询指定 teamId 的回调任务信息
     * @param teamId 队伍 ID
     * @return 回调任务列表
     */
    List<NotifyTaskEntity> queryNoNotifyTaskList(String teamId);

    /**
     * 根据 notify 实体将回调明细的 回调次数 +1 并将状态修改为 成功
     * @param notifyTaskEntity 回调任务实体
     * @return 受修改的行数
     */
    int updateNotifyStatusSuccess(NotifyTaskEntity notifyTaskEntity);

    /**
     * 根据 notify 实体将回调明细的 回调次数 +1 并将状态修改为 失败
     * @param notifyTaskEntity 回调任务实体
     * @return 受修改的行数
     */
    int updateNotifyStatusError(NotifyTaskEntity notifyTaskEntity);

    /**
     * 根据 notify 实体将回调明细的 回调次数 +1 并将状态修改为 重试
     * @param notifyTaskEntity 回调任务实体
     * @return 受修改的行数
     */
    int updateNotifyStatusRetry(NotifyTaskEntity notifyTaskEntity);
}
