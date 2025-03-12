package xin.eason.domain.activity.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 用户的组队信息实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserTeamInfoEntity {
    /**
     * 拼团队伍对账的 用户ID
     */
    private String userId;
    /**
     * 拼团队伍 ID
     */
    private String teamId;
    /**
     * 拼团队伍所参加的活动 ID
     */
    private Long activityId;
    /**
     * 拼团目标人数
     */
    private Integer targetCount;
    /**
     * 拼团完成人数
     */
    private Integer completeCount;
    /**
     * 拼团锁单人数
     */
    private Integer lockCount;
    /**
     * 拼团队伍开始拼团时间 (有效开始时间)
     */
    private LocalDateTime validStartTime;
    /**
     * 拼团队伍拼团结束时间 (有效结束时间 = 有效开始时间 + 活动有效时长)
     */
    private LocalDateTime validEndTime;
    /**
     * 外部订单 ID
     */
    private String outerOrderId;
}
