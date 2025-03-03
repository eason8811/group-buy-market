package xin.eason.infrastructure.dao.po;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 人群标签表
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("crowd_tags")
public class CrowdTagsPO {
    /**
     * 自增ID
     */
    private Integer id;
    /**
     * 人群ID
     */
    private String tagId;
    /**
     * 人群名称
     */
    private String tagName;
    /**
     * 人群描述
     */
    private String tagDesc;
    /**
     * 人群标签统计量
     */
    private Integer statistics;
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;


}

