package xin.eason.domain.trade.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 订单结算规则过滤请求实体
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TradeSettlementRuleFilterRequestEntity {
    /** 渠道 */
    private String source;
    /** 来源 */
    private String channel;
    /** 用户ID */
    private String userId;
    /** 外部订单 ID */
    private String outOrderId;
    /** 支付时间 */
    private LocalDateTime payTime;
}
