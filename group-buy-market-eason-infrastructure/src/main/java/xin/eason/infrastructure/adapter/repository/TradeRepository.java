package xin.eason.infrastructure.adapter.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import xin.eason.domain.trade.adapter.repository.ITradeRepository;
import xin.eason.domain.trade.model.aggregate.GroupBuyOrderAggregate;
import xin.eason.domain.trade.model.entity.PayOrderActivityEntity;
import xin.eason.domain.trade.model.entity.PayOrderDiscountEntity;
import xin.eason.domain.trade.model.entity.PayOrderEntity;
import xin.eason.domain.trade.model.entity.PayOrderTeamEntity;
import xin.eason.domain.trade.model.valobj.GroupBuyProgressVO;
import xin.eason.domain.trade.model.valobj.OrderListStatus;
import xin.eason.infrastructure.dao.IGroupBuyOrder;
import xin.eason.infrastructure.dao.IGroupBuyOrderList;
import xin.eason.infrastructure.dao.po.GroupBuyOrderListPO;
import xin.eason.infrastructure.dao.po.GroupBuyOrderPO;

import java.math.BigDecimal;

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
                .payPrice(new BigDecimal("0.00"))
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
                .build();
        groupBuyOrderList.insert(groupBuyOrderListPO);
    }
}
