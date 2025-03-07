package xin.eason.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 锁定拼团订单数据传输类 (Response)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LockMarketPayOrderResponseDTO {
    /**
     * 生成的预购订单 ID
     */
    private String orderId;
    /**
     * 拼团组队 ID
     */
    private String teamId;
    /**
     * 折扣价格
     */
    private BigDecimal discountPrice;
    /**
     * 预购订单状态
     */
    private Integer tradeOrderStatus;
}
