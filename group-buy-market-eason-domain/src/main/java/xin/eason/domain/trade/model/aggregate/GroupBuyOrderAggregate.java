package xin.eason.domain.trade.model.aggregate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import xin.eason.domain.trade.model.entity.PayOrderActivityEntity;
import xin.eason.domain.trade.model.entity.PayOrderDiscountEntity;
import xin.eason.domain.trade.model.entity.PayOrderEntity;
import xin.eason.domain.trade.model.entity.PayOrderTeamEntity;
import xin.eason.domain.trade.model.valobj.GroupBuyProgressVO;
import xin.eason.domain.trade.model.valobj.OrderListStatus;
import xin.eason.domain.trade.model.valobj.OrderStatus;
import xin.eason.types.exception.LockOrderException;

import java.time.LocalDateTime;

/**
 * 拼团订单聚合
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupBuyOrderAggregate {
    /**
     * 用户 ID
     */
    private String userId;
    /**
     * 外部订单 ID (保持唯一幂等)
     */
    private String outerOrderId;
    /**
     * 参加该活动的次数
     */
    private Long joinTimes;
    /**
     * 拼团订单实体
     */
    private PayOrderEntity payOrderEntity;
    /**
     * 拼团订单所属活动实体
     */
    private PayOrderActivityEntity payOrderActivityEntity;
    /**
     * 拼团订单的折扣信息实体
     */
    private PayOrderDiscountEntity payOrderDiscountEntity;
    /**
     * 拼团队伍实体
     */
    private PayOrderTeamEntity payOrderTeamEntity;

    /**
     * 初始化 {@link #payOrderTeamEntity} 属性
     */
    public void initTeamEntity() {
        LocalDateTime currentTime = LocalDateTime.now();

        payOrderTeamEntity.setTeamId(RandomStringUtils.randomNumeric(8));
        payOrderTeamEntity.setOrderStatus(OrderStatus.GROUPING);
        payOrderTeamEntity.setTeamProgress(
                GroupBuyProgressVO.builder()
                        .targetCount(payOrderActivityEntity.getTargetCount())
                        .completeCount(0)
                        .lockCount(0)
                        .build()
        );
        payOrderTeamEntity.setValidStartTime(currentTime);
        payOrderTeamEntity.setValidEndTime(currentTime.plusMinutes(payOrderActivityEntity.getValidTime()));
    }

    /**
     * 锁定一个订单
     */
    public void lockOrder() {
        // 如果 payOrderTeamEntity 为 null 就先初始化
        if (payOrderTeamEntity.getTeamId() == null)
            initTeamEntity();
        if (payOrderTeamEntity.getOrderStatus() != OrderStatus.GROUPING)
            // 如果拼团订单状态不在 拼单中 代表无法加入队伍, 抛出异常
            throw new LockOrderException("锁定拼团订单错误, userId: " + userId + ", outerOrderId: " + outerOrderId + ", teamId: " + payOrderTeamEntity.getTeamId() + " 订单状态为: " + OrderStatus.GROUPING.getDesc());
        // 加入拼团队伍
        GroupBuyProgressVO teamProgress = payOrderTeamEntity.getTeamProgress();
        payOrderTeamEntity.setTeamProgress(
                GroupBuyProgressVO.builder()
                        .targetCount(teamProgress.getTargetCount())
                        .completeCount(teamProgress.getCompleteCount())
                        .lockCount(teamProgress.getLockCount() + 1)
                        .build()
        );
        // 生成拼团系统内部订单号 orderId
        payOrderEntity.setOrderId(RandomStringUtils.randomNumeric(12));
        // 设置订单明细状态
        payOrderEntity.setOrderListStatus(OrderListStatus.INIT_LOCK);
    }
}
