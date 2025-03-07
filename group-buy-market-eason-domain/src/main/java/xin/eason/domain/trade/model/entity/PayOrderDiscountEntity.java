package xin.eason.domain.trade.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 拼团折扣实体
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PayOrderDiscountEntity {
    /**
     * 商品来源
     */
    private String source;
    /**
     * 商品渠道
     */
    private String channel;
    /**
     * 商品 ID
     */
    private String goodsId;
    /**
     * 商品名称
     */
    private String goodsName;
    /**
     * 商品原价
     */
    private BigDecimal originalPrice;
    /**
     * 商品折扣价格
     */
    private BigDecimal discountPrice;
}
