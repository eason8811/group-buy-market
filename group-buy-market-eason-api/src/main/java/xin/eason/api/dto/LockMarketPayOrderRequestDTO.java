package xin.eason.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 锁定拼团订单数据传输类 (Request)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LockMarketPayOrderRequestDTO {
    /**
     * 用户 ID
     */
    private String userId;
    /**
     * 组队 ID (可以为 null)
     */
    private String teamId;
    /**
     * 活动 ID (组队 ID 为 null 时需要用来确定参加的活动)
     */
    private Long activityId;
    /**
     * 商品 ID (与 SC 值构成唯一, 确认要购买的商品)
     */
    private String goodsId;
    /**
     * 商品来源 (与 channel 渠道构成 SC 值)
     */
    private String source;
    /**
     * 商品渠道 (与 source 来源构成 SC 值)
     */
    private String channel;
    /**
     * 外部订单 ID 确保系统内部唯一幂等
     */
    private String outerOrderId;
}
