package xin.eason.domain.trade.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 订单结算成功实体
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderSettlementSuccessEntity {
    /** 渠道 */
    private String source;
    /** 来源 */
    private String channel;
    /** 用户ID */
    private String userId;
    /** 外部交易单号 */
    private String outTradeNo;
    /** 拼单组队ID */
    private String teamId;
    /** 活动ID */
    private Long activityId;
}
