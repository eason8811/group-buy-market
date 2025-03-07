package xin.eason.domain.trade.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import xin.eason.domain.trade.model.valobj.OrderListStatus;

import java.math.BigDecimal;

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
     * 折扣后价格
     */
    private BigDecimal discountPrice;
    /**
     * 订单明细状态
     */
    private OrderListStatus orderListStatus;
}
