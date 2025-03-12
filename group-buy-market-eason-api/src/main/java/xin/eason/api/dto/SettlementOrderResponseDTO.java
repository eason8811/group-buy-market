package xin.eason.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 订单结算响应数据传输对象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SettlementOrderResponseDTO {
    /** 渠道 */
    private String source;
    /** 来源 */
    private String channel;
    /** 用户ID */
    private String userId;
    /** 外部订单 ID */
    private String outerOrderId;
    /** 拼单组队ID */
    private String teamId;
    /** 活动ID */
    private Long activityId;
}
