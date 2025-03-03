package xin.eason.infrastructure.dao.po;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 人群标签明细表
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("crowd_tags_detail")
public class CrowdTagsDetailPO {
    /**
     * 自增ID
     */
    private Integer id;
    /**
     * 人群ID
     */
    private String tagId;
    /**
     * 用户ID
     */
    private String userId;
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

}
