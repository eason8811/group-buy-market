package xin.eason.domain.activity.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 营销产品实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MarketProductEntity {

    /** 用户ID */
    private String userId;
    /** 商品ID */
    private String goodsId;
    /** 渠道 */
    private String source;
    /** 来源 */
    private String channel;

}
