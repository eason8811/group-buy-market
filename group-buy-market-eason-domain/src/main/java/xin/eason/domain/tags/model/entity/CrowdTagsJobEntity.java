package xin.eason.domain.tags.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import xin.eason.domain.activity.model.valobj.TagStatus;
import xin.eason.domain.activity.model.valobj.TagType;

import java.time.LocalDateTime;

/**
 * 人群标签任务实体
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CrowdTagsJobEntity {
    /**
     * 批次ID
     */
    private String batchId;
    /**
     * 标签类型 ( 0, 参与量 ) ( 1, 消费金额 )
     */
    private TagType tagType;
    /**
     * 标签规则 (限定类型 N次)
     */
    private String tagRule;
    /**
     * 统计数据，开始时间
     */
    private LocalDateTime statStartTime;
    /**
     * 统计数据，结束时间
     */
    private LocalDateTime statEndTime;
    /**
     * 状态；( 0, 初始 )  ( 1, 计划 [进入执行阶段] )  ( 2, 重置 ), ( 3, 完成 )
     */
    private TagStatus status;
}
