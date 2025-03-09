package xin.eason.domain.trade.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 交易规则过滤返回实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TradeRuleFilterResponseEntity {
    private Long userJoinTimes;
}
