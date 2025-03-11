package xin.eason.domain.trade.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 订单结算实体, 用于提供订单结算所需信息
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderSettlementEntity {
    /** 渠道 */
    private String source;
    /** 来源 */
    private String channel;
    /** 用户ID */
    private String userId;
    /** 外部订单 ID */
    private String outerOrderId;
    /** 支付时间 */
    private LocalDateTime payTime;
}
