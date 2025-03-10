package xin.eason.infrastructure.adapter.repository;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.RequiredArgsConstructor;
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
import xin.eason.infrastructure.dao.po.NotifyTask;
import xin.eason.types.exception.UpdateAmountZeroException;

import java.util.HashMap;
import java.util.List;

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
        boolean isNewTeam = groupBuyOrderAggregate.getPayOrderTeamEntity() == null;
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
     */
    @Override
    @Transactional
    public void settlementPayOrder(GroupBuyOrderAggregate groupBuyOrderAggregate) {
        PayOrderTeamEntity teamEntity = groupBuyOrderAggregate.getPayOrderTeamEntity();
        PayOrderActivityEntity activityEntity = groupBuyOrderAggregate.getPayOrderActivityEntity();

        // 更新 拼团明细表group_buy_order_list 中的订单状态
        LambdaUpdateWrapper<GroupBuyOrderListPO> orderListUpdateWrapper = new LambdaUpdateWrapper<>();
        orderListUpdateWrapper
                .eq(GroupBuyOrderListPO::getUserId, groupBuyOrderAggregate.getUserId())
                .eq(GroupBuyOrderListPO::getOutTradeNo, groupBuyOrderAggregate.getOuterOrderId())
                .set(GroupBuyOrderListPO::getStatus, OrderListStatus.PAY_COMPLETE);
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

            NotifyTask notifyTask = NotifyTask.builder()
                    .activityId(activityEntity.getActivityId())
                    .teamId(teamEntity.getTeamId())
                    .notifyUrl("暂无 URL")
                    .notifyCount(0)
                    .notifyStatus(0)
                    .parameterJson(JSON.toJSONString(jsonDataMap))
                    .build();

            notifyTaskMapper.insert(notifyTask);
        }
    }
}
