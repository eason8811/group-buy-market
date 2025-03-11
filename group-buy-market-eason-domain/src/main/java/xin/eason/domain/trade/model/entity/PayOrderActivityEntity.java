package xin.eason.domain.trade.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * <p>拼团订单活动实体</p>
 * <p>描述这个拼团订单是拼的哪个活动, 活动信息有哪些</p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PayOrderActivityEntity {
    /**
     * 活动 ID
     */
    private Long activityId;
    /**
     * 活动名称
     */
    private String activityName;
    /**
     * 活动开始时间
     */
    private LocalDateTime startTime;
    /**
     * 活动结束时间
     */
    private LocalDateTime endTime;
    /**
     * 拼团可用时长
     */
    private Integer validTime;
    /**
     * 拼团目标人数
     */
    private Integer targetCount;
}
