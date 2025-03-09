package xin.eason.domain.trade.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import xin.eason.domain.activity.model.valobj.ActivityStatus;
import xin.eason.domain.activity.model.valobj.GroupType;
import xin.eason.domain.activity.model.valobj.TagScope;

import java.time.LocalDateTime;

/**
 * 活动的实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupBuyActivityEntity {
    /** 活动ID */
    private Long activityId;
    /** 活动名称 */
    private String activityName;
    /** 折扣ID */
    private String discountId;
    /** 拼团方式（0自动成团、1达成目标拼团） */
    private GroupType groupType;
    /** 拼团活动参加次数限制 */
    private Integer joinLimitCount;
    /** 拼团目标 */
    private Integer target;
    /** 拼团时长（分钟） */
    private Integer validTime;
    /** 活动状态（0创建、1生效、2过期、3废弃） */
    private ActivityStatus status;
    /** 活动开始时间 */
    private LocalDateTime startTime;
    /** 活动结束时间 */
    private LocalDateTime endTime;
    /** 人群标签规则标识 */
    private String tagId;
    /** 人群标签规则范围 */
    private TagScope tagScope;
}
