package xin.eason.infrastructure.dao.po;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("notify_task")
public class NotifyTask {

    /** 自增ID */
    private Long id;
    /** 活动ID */
    private Long activityId;
    /** 拼单组队ID */
    private String teamId;
    /** 回调接口 */
    private String notifyUrl;
    /** 回调次数 */
    private Integer notifyCount;
    /** 回调状态【0初始、1完成、2重试、3失败】 */
    private Integer notifyStatus;
    /** 参数对象 */
    private String parameterJson;
    /** 创建时间 */
    private LocalDateTime createTime;
    /** 更新时间 */
    private LocalDateTime updateTime;

}