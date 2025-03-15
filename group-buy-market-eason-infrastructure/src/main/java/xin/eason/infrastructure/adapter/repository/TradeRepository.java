package xin.eason.infrastructure.adapter.repository;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import xin.eason.domain.trade.adapter.repository.ITradeRepository;
import xin.eason.domain.trade.model.aggregate.GroupBuyOrderAggregate;
import xin.eason.domain.trade.model.entity.*;
import xin.eason.domain.trade.model.valobj.GroupBuyProgressVO;
import xin.eason.domain.trade.model.valobj.OrderListStatus;
import xin.eason.domain.trade.model.valobj.OrderStatus;
import xin.eason.infrastructure.dao.IGroupBuyActivity;
import xin.eason.infrastructure.dao.IGroupBuyOrder;
import xin.eason.infrastructure.dao.IGroupBuyOrderList;
import xin.eason.infrastructure.dao.INotifyTask;
import xin.eason.infrastructure.dao.po.GroupBuyActivityPO;
import xin.eason.infrastructure.dao.po.GroupBuyOrderListPO;
import xin.eason.infrastructure.dao.po.GroupBuyOrderPO;
import xin.eason.infrastructure.dao.po.NotifyTaskPO;
import xin.eason.infrastructure.dcc.DCCService;
import xin.eason.types.exception.UpdateAmountZeroException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class TradeRepository implements ITradeRepository {
    /**
     * 拼团订单明细表对应 Mapper
     */
    private final IGroupBuyOrderList groupBuyOrderList;
    /**
     * 拼团订单表对应 Mapper
     */
    private final IGroupBuyOrder groupBuyOrder;
    /**
     * 拼团活动表对应 Mapper
     */
    private final IGroupBuyActivity groupBuyActivity;
    /**
     * 回调任务表对应 Mapper
     */
    private final INotifyTask notifyTaskMapper;
    /**
     * 动态配置管理服务
     */
    private final DCCService dccService;

    /**
     * 查询指定 用户ID, 外部订单ID 组合是否有未支付订单 orderStatus = INIT_LOCK(0, "初始锁定")
     *
     * @param userId       用户 ID
     * @param outerOrderId 外部订单 ID
     * @return 如果存在未支付订单, 返回该订单, 否则返回 null
     */
    @Override
    public PayOrderEntity queryUnpayOrder(String userId, String outerOrderId) {
        LambdaQueryWrapper<GroupBuyOrderListPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GroupBuyOrderListPO::getUserId, userId)
                .eq(GroupBuyOrderListPO::getOutTradeNo, outerOrderId)
                .eq(GroupBuyOrderListPO::getStatus, OrderListStatus.INIT_LOCK);
        GroupBuyOrderListPO groupBuyOrderListPO = groupBuyOrderList.selectOne(wrapper);
        if (groupBuyOrderListPO == null)
            return null;
        return PayOrderEntity.builder()
                .orderId(groupBuyOrderListPO.getOrderId())
                .orderListStatus(groupBuyOrderListPO.getStatus())
                .discountPrice(groupBuyOrderListPO.getDeductionPrice())
                .payPrice(groupBuyOrderListPO.getPayPrice())
                .build();
    }

    /**
     * 根据组队 ID 查询队伍的拼团进度
     *
     * @param teamId 组队 ID
     * @return 拼团队伍的实体类对象
     */
    @Override
    public PayOrderTeamEntity queryTeamProgress(String teamId) {
        LambdaQueryWrapper<GroupBuyOrderPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GroupBuyOrderPO::getTeamId, teamId);
        GroupBuyOrderPO groupBuyOrderPO = groupBuyOrder.selectOne(wrapper);
        if (groupBuyOrderPO == null)
            return null;
        return PayOrderTeamEntity.builder()
                .teamId(teamId)
                .orderStatus(groupBuyOrderPO.getStatus())
                .teamProgress(
                        GroupBuyProgressVO.builder()
                                .targetCount(groupBuyOrderPO.getTargetCount())
                                .completeCount(groupBuyOrderPO.getCompleteCount())
                                .lockCount(groupBuyOrderPO.getLockCount())
                                .build()
                )
                .validStartTime(groupBuyOrderPO.getValidStartTime())
                .validEndTime(groupBuyOrderPO.getValidEndTime())
                .build();
    }

    /**
     * 根据订单聚合内的信息进行锁单操作
     *
     * @param groupBuyOrderAggregate 订单聚合
     */
    @Override
    @Transactional
    public void lockOrder(GroupBuyOrderAggregate groupBuyOrderAggregate) {
        boolean isNewTeam = groupBuyOrderAggregate.getPayOrderTeamEntity().getTeamId() == null;
        // 更改聚合中有关拼团订单名额的信息
        groupBuyOrderAggregate.lockOrder();

        PayOrderTeamEntity teamEntity = groupBuyOrderAggregate.getPayOrderTeamEntity();
        PayOrderActivityEntity activityEntity = groupBuyOrderAggregate.getPayOrderActivityEntity();
        PayOrderDiscountEntity discountEntity = groupBuyOrderAggregate.getPayOrderDiscountEntity();
        PayOrderEntity payOrderEntity = groupBuyOrderAggregate.getPayOrderEntity();

        GroupBuyOrderPO groupBuyOrderPO = GroupBuyOrderPO.builder()
                .teamId(teamEntity.getTeamId())
                .activityId(activityEntity.getActivityId())
                .source(discountEntity.getSource())
                .channel(discountEntity.getChannel())
                .originalPrice(discountEntity.getOriginalPrice())
                .deductionPrice(discountEntity.getDiscountPrice())
                .payPrice(payOrderEntity.getPayPrice())
                .targetCount(teamEntity.getTeamProgress().getTargetCount())
                .completeCount(teamEntity.getTeamProgress().getCompleteCount())
                .lockCount(teamEntity.getTeamProgress().getLockCount())
                .status(teamEntity.getOrderStatus())
                .validStartTime(teamEntity.getValidStartTime())
                .validEndTime(teamEntity.getValidEndTime())
                .notifyUrl(teamEntity.getNotifyUrl())
                .build();

        // 若是新增的队伍 (isNewTeam == true), 则 group_buy_order 表插入内容
        if (isNewTeam)
            groupBuyOrder.insert(groupBuyOrderPO);
            // 若是加入的队伍 (isNewTeam == false), 则 group_buy_order 表更新内容
        else {
            LambdaUpdateWrapper<GroupBuyOrderPO> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(GroupBuyOrderPO::getTeamId, teamEntity.getTeamId());
            groupBuyOrder.update(groupBuyOrderPO, updateWrapper);
        }

        // 向 group_buy_order_list 表中添加 group_buy_order_list 订单明细
        GroupBuyOrderListPO groupBuyOrderListPO = GroupBuyOrderListPO.builder()
                .userId(groupBuyOrderAggregate.getUserId())
                .teamId(teamEntity.getTeamId())
                .orderId(payOrderEntity.getOrderId())
                .activityId(activityEntity.getActivityId())
                .startTime(activityEntity.getStartTime())
                .endTime(activityEntity.getEndTime())
                .goodsId(discountEntity.getGoodsId())
                .source(discountEntity.getSource())
                .channel(discountEntity.getChannel())
                .originalPrice(discountEntity.getOriginalPrice())
                .deductionPrice(discountEntity.getDiscountPrice())
                .payPrice(payOrderEntity.getPayPrice())
                .status(payOrderEntity.getOrderListStatus())
                .outTradeNo(groupBuyOrderAggregate.getOuterOrderId())
                .bizId(activityEntity.getActivityId() + "_" + groupBuyOrderAggregate.getUserId() + "_" + (groupBuyOrderAggregate.getJoinTimes() + 1))
                .build();
        groupBuyOrderList.insert(groupBuyOrderListPO);
    }

    /**
     * 根据 activityId 获取活动实体对象
     *
     * @param activityId 活动 ID
     * @return 活动实体对象
     */
    @Override
    public GroupBuyActivityEntity queryActivityByActivityId(Long activityId) {
        GroupBuyActivityPO activityPO = groupBuyActivity.selectOne(new LambdaQueryWrapper<GroupBuyActivityPO>().eq(GroupBuyActivityPO::getActivityId, activityId));
        return GroupBuyActivityEntity.builder()
                .activityId(activityPO.getActivityId())
                .activityName(activityPO.getActivityName())
                .discountId(activityPO.getDiscountId())
                .groupType(activityPO.getGroupType())
                .joinLimitCount(activityPO.getTakeLimitCount())
                .target(activityPO.getTarget())
                .validTime(activityPO.getValidTime())
                .status(activityPO.getStatus())
                .startTime(activityPO.getStartTime())
                .endTime(activityPO.getEndTime())
                .tagId(activityPO.getTagId())
                .tagScope(activityPO.getTagScope())
                .build();
    }

    /**
     * 根据 activityId 和 userId 获取用户参与一个活动的次数
     *
     * @param activityId 活动 ID
     * @param userId     用户 ID
     * @return 用户参与活动次数
     */
    @Override
    public Long queryUserJoinActivityTimes(Long activityId, String userId) {
        LambdaQueryWrapper<GroupBuyOrderListPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GroupBuyOrderListPO::getActivityId, activityId).eq(GroupBuyOrderListPO::getUserId, userId);
        return groupBuyOrderList.selectCount(wrapper);
    }

    /**
     * 进行订单结算具体操作
     *
     * @param groupBuyOrderAggregate 订单聚合
     * @return 是否需要回调
     */
    @Override
    @Transactional
    public Boolean settlementPayOrder(GroupBuyOrderAggregate groupBuyOrderAggregate) {
        PayOrderTeamEntity teamEntity = groupBuyOrderAggregate.getPayOrderTeamEntity();
        PayOrderActivityEntity activityEntity = groupBuyOrderAggregate.getPayOrderActivityEntity();

        // 更新 拼团明细表group_buy_order_list 中的订单状态, 并更新 支付时间
        LambdaUpdateWrapper<GroupBuyOrderListPO> orderListUpdateWrapper = new LambdaUpdateWrapper<>();
        orderListUpdateWrapper
                .eq(GroupBuyOrderListPO::getUserId, groupBuyOrderAggregate.getUserId())
                .eq(GroupBuyOrderListPO::getOutTradeNo, groupBuyOrderAggregate.getOuterOrderId())
                .set(GroupBuyOrderListPO::getStatus, OrderListStatus.PAY_COMPLETE)
                .set(GroupBuyOrderListPO::getPayTime, groupBuyOrderAggregate.getPayOrderEntity().getPayTime());
        int rowCount = groupBuyOrderList.update(orderListUpdateWrapper);
        if (rowCount != 1)
            throw new UpdateAmountZeroException("group_buy_order_list 表更新订单明细状态, 受影响表记录为 0 !");


        // 更新 拼团订单表group_buy_order 中的 拼团完成数量complete_count
        LambdaUpdateWrapper<GroupBuyOrderPO> orderUpdateWrapper = new LambdaUpdateWrapper<>();
        orderUpdateWrapper.eq(GroupBuyOrderPO::getTeamId, teamEntity.getTeamId());
        rowCount = groupBuyOrder.updateOrderCompleteCountByTeamId(orderUpdateWrapper);
        if (rowCount != 1)
            throw new UpdateAmountZeroException("group_buy_order 表更新订单已完成数量, 受影响表记录为 0 !");

        // 如果当前完成的订单是最后一个达成目标的订单
        if (teamEntity.getTeamProgress().getTargetCount() == teamEntity.getTeamProgress().getCompleteCount() + 1) {

            // 更新 拼团订单表group_buy_order 中的订单状态
            LambdaUpdateWrapper<GroupBuyOrderPO> updateOrderStatusWrapper = new LambdaUpdateWrapper<>();
            updateOrderStatusWrapper
                    .eq(GroupBuyOrderPO::getTeamId, teamEntity.getTeamId())
                    .set(GroupBuyOrderPO::getStatus, OrderStatus.COMPLETE);
            rowCount = groupBuyOrder.update(updateOrderStatusWrapper);
            if (rowCount != 1)
                throw new UpdateAmountZeroException("group_buy_order 表更新订单状态, 受影响表记录为 0 !");

            // 获取该 teamId 在 group_buy_order_list 表中所有的 outerOrderId, 装配 notifyEntity
            LambdaQueryWrapper<GroupBuyOrderListPO> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(GroupBuyOrderListPO::getTeamId, teamEntity.getTeamId());
            List<GroupBuyOrderListPO> groupBuyOrderListOfTeam = groupBuyOrderList.selectList(queryWrapper);
            // 组装回调json数据
            HashMap<String, Object> jsonDataMap = new HashMap<>();
            jsonDataMap.put("teamId", teamEntity.getTeamId());
            jsonDataMap.put("outerOrderId", groupBuyOrderListOfTeam.stream().map(GroupBuyOrderListPO::getOutTradeNo).toList());

            NotifyTaskPO notifyTask = NotifyTaskPO.builder()
                    .activityId(activityEntity.getActivityId())
                    .teamId(teamEntity.getTeamId())
                    .notifyUrl(teamEntity.getNotifyUrl())
                    .notifyCount(0)
                    .notifyStatus(0)
                    .parameterJson(JSON.toJSONString(jsonDataMap))
                    .build();

            notifyTaskMapper.insert(notifyTask);
            return true;
        }
        return false;
    }

    /**
     * 根据 source 和 channel 校验是否属于黑名单内
     *
     * @param source  来源
     * @param channel 渠道
     * @return SC值 是否处于黑名单内
     */
    @Override
    public Boolean SCBlackList(String source, String channel) {
        return dccService.isSCBlackList(source, channel);
    }

    /**
     * 根据 userId 和 outOrderId 查询所属的队伍信息
     *
     * @param userId     用户 ID
     * @param outOrderId 外部订单 ID
     * @return 所属队伍信息
     */
    @Override
    public PayOrderTeamEntity queryTeamInfo(String userId, String outOrderId) {
        // 根据 userId 和 outOrderId 查询 订单明细信息
        LambdaQueryWrapper<GroupBuyOrderListPO> orderListWrapper = new LambdaQueryWrapper<>();
        orderListWrapper.eq(GroupBuyOrderListPO::getUserId, userId)
                .eq(GroupBuyOrderListPO::getOutTradeNo, outOrderId)
                .eq(GroupBuyOrderListPO::getStatus, OrderListStatus.INIT_LOCK);
        GroupBuyOrderListPO groupBuyOrderListPO = groupBuyOrderList.selectOne(orderListWrapper);

        // 根据获取到的订单明细信息所属的 teamId 获取队伍实体信息
        String teamId = groupBuyOrderListPO.getTeamId();
        LambdaQueryWrapper<GroupBuyOrderPO> orderWrapper = new LambdaQueryWrapper<>();
        orderWrapper.eq(GroupBuyOrderPO::getTeamId, teamId);
        GroupBuyOrderPO groupBuyOrderPO = groupBuyOrder.selectOne(orderWrapper);
        return PayOrderTeamEntity.builder()
                .teamId(teamId)
                .orderStatus(groupBuyOrderPO.getStatus())
                .validStartTime(groupBuyOrderPO.getValidStartTime())
                .validEndTime(groupBuyOrderPO.getValidEndTime())
                .teamProgress(
                        GroupBuyProgressVO.builder()
                                .targetCount(groupBuyOrderPO.getTargetCount())
                                .completeCount(groupBuyOrderPO.getCompleteCount())
                                .lockCount(groupBuyOrderPO.getLockCount())
                                .build()
                )
                .notifyUrl(groupBuyOrderPO.getNotifyUrl())
                .build();
    }

    /**
     * 根据 teamId 获取 activityId
     *
     * @param teamId 队伍 ID
     * @return 订单活动实体
     */
    @Override
    public PayOrderActivityEntity queryActivityInfo(String teamId) {
        LambdaQueryWrapper<GroupBuyOrderPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GroupBuyOrderPO::getTeamId, teamId);
        GroupBuyOrderPO groupBuyOrderPO = groupBuyOrder.selectOne(wrapper);
        return PayOrderActivityEntity.builder()
                .activityId(groupBuyOrderPO.getActivityId())
                .targetCount(groupBuyOrderPO.getTargetCount())
                .build();
    }

    /**
     * 查询所有未回调的回调任务
     *
     * @return 回调任务列表
     */
    @Override
    public List<NotifyTaskEntity> queryNoNotifyTaskList() {
        LambdaQueryWrapper<NotifyTaskPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(NotifyTaskPO::getNotifyStatus, List.of(0, 2));
        List<NotifyTaskPO> notifyTaskList = notifyTaskMapper.selectList(wrapper);
        return notifyTaskList.stream()
                .map(notifyTask ->
                        NotifyTaskEntity.builder()
                                .teamId(notifyTask.getTeamId())
                                .notifyUrl(notifyTask.getNotifyUrl())
                                .notifyCount(notifyTask.getNotifyCount())
                                .parameterJson(notifyTask.getParameterJson())
                                .build())
                .toList();
    }

    /**
     * 查询指定 teamId 的回调任务信息
     *
     * @param teamId 队伍 ID
     * @return 回调任务列表
     */
    @Override
    public List<NotifyTaskEntity> queryNoNotifyTaskList(String teamId) {
        LambdaQueryWrapper<NotifyTaskPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(NotifyTaskPO::getTeamId, teamId).in(NotifyTaskPO::getNotifyStatus, List.of(0, 2));
        List<NotifyTaskPO> notifyTaskList = notifyTaskMapper.selectList(wrapper);
        return notifyTaskList.stream()
                .map(notifyTask ->
                        NotifyTaskEntity.builder()
                                .teamId(notifyTask.getTeamId())
                                .notifyUrl(notifyTask.getNotifyUrl())
                                .notifyCount(notifyTask.getNotifyCount())
                                .parameterJson(notifyTask.getParameterJson())
                                .build())
                .toList();
    }

    /**
     * 根据 notify 实体将回调明细的 回调次数 +1 并将状态修改为 成功
     *
     * @param notifyTaskEntity 回调任务实体
     * @return 受修改的行数
     */
    @Override
    public int updateNotifyStatusSuccess(NotifyTaskEntity notifyTaskEntity) {
        LambdaQueryWrapper<NotifyTaskPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(NotifyTaskPO::getTeamId, notifyTaskEntity.getTeamId());
        return notifyTaskMapper.updateNotifyStatusSuccess(wrapper);
    }

    /**
     * 根据 notify 实体将回调明细的 回调次数 +1 并将状态修改为 失败
     *
     * @param notifyTaskEntity 回调任务实体
     * @return 受修改的行数
     */
    @Override
    public int updateNotifyStatusError(NotifyTaskEntity notifyTaskEntity) {
        LambdaQueryWrapper<NotifyTaskPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(NotifyTaskPO::getTeamId, notifyTaskEntity.getTeamId());
        return notifyTaskMapper.updateNotifyStatusError(wrapper);
    }

    /**
     * 根据 notify 实体将回调明细的 回调次数 +1 并将状态修改为 重试
     *
     * @param notifyTaskEntity 回调任务实体
     * @return 受修改的行数
     */
    @Override
    public int updateNotifyStatusRetry(NotifyTaskEntity notifyTaskEntity) {
        LambdaQueryWrapper<NotifyTaskPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(NotifyTaskPO::getTeamId, notifyTaskEntity.getTeamId());
        return notifyTaskMapper.updateNotifyStatusRetry(wrapper);
    }

    /**
     * 根据当前时间查询状态 不为成功 的队伍是否存在, 如果存在, 则将其状态更改为 失败, 连带修改队伍中所有用户的订单明细状态为 超时关单
     *
     * @param currentTime 当前时间
     * @return 修改成功的 teamId 列表
     */
    @Override
    @Transactional
    public List<String> setInvalidTeamToFailed(LocalDateTime currentTime) {
        // 获取不在合法时间内 且 状态为正在拼团中 的队伍的 teamId
        LambdaQueryWrapper<GroupBuyOrderPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.lt(GroupBuyOrderPO::getValidEndTime, currentTime).eq(GroupBuyOrderPO::getStatus, OrderStatus.GROUPING);
        List<GroupBuyOrderPO> invalidOrderPOList = groupBuyOrder.selectList(queryWrapper);
        List<String> teamIdList = invalidOrderPOList.stream().map(GroupBuyOrderPO::getTeamId).toList();

        // 将这些 teamId 的队伍状态修改为 失败
        if (teamIdList.isEmpty()) {
            log.info("暂无需要修改状态为 失败 的队伍!");
            return teamIdList;
        }
        LambdaUpdateWrapper<GroupBuyOrderPO> orderUpdateWrapper = new LambdaUpdateWrapper<>();
        orderUpdateWrapper.set(GroupBuyOrderPO::getStatus, OrderStatus.FAIL).in(GroupBuyOrderPO::getTeamId, teamIdList);
        int updateRowCount = groupBuyOrder.update(orderUpdateWrapper);
        if (updateRowCount != teamIdList.size())
            throw new UpdateAmountZeroException("设置不合法队伍状态为失败过程出现异常! group_buy_order表 更改行数与查询到的 teamId 数不符! teamIdList : " + teamIdList);

        LambdaUpdateWrapper<GroupBuyOrderListPO> orderListUpdateWrapper = new LambdaUpdateWrapper<>();
        orderListUpdateWrapper.in(GroupBuyOrderListPO::getTeamId, teamIdList).set(GroupBuyOrderListPO::getStatus, OrderListStatus.CLOSE);
        updateRowCount = groupBuyOrderList.update(orderListUpdateWrapper);
        if (updateRowCount == 0)
            throw new UpdateAmountZeroException("设置不合法队伍状态为失败过程出现异常! group_buy_order_list表 更改行数为 0");
        return teamIdList;
    }
}
