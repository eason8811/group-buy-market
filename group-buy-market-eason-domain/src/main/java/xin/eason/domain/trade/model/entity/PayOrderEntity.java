package xin.eason.domain.trade.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import xin.eason.domain.trade.model.valobj.OrderListStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 支付订单实体
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PayOrderEntity {
    /**
     * 内部订单 ID
     */
    private String orderId;
    /**
     * 原始价格
     */
    private BigDecimal originalPrice;
    /**
     * 折扣金额
     */
    private BigDecimal discountPrice;
    /**
     * 折扣后价格
     */
    private BigDecimal payPrice;
    /**
     * 该明细交易时间
     */
    private LocalDateTime payTime;
    /**
     * 订单明细状态
     */
    private OrderListStatus orderListStatus;
}
