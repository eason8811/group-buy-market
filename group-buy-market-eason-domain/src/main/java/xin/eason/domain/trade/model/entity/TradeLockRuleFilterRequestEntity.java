package xin.eason.domain.trade.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 交易规则过滤请求实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TradeLockRuleFilterRequestEntity {
    /**
     * 活动 ID
     */
    private Long activityId;
    /**
     * 用户 ID
     */
    private String userId;
}
