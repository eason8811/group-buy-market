package xin.eason.domain.trade.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import xin.eason.domain.trade.model.valobj.GroupBuyProgressVO;
import xin.eason.domain.trade.model.valobj.OrderStatus;

import java.time.LocalDateTime;

/**
 * 订单结算规则过滤响应实体
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TradeSettlementRuleFilterResponseEntity {
    /** 拼单组队ID */
    private String teamId;
    /** 活动ID */
    private Long activityId;
    /**
     * 队伍进度
     */
    private GroupBuyProgressVO teamProgress;
    /** 状态（0-拼单中、1-完成、2-失败） */
    private OrderStatus status;
    /** 拼团开始时间 - 参与拼团时间 */
    private LocalDateTime validStartTime;
    /** 拼团结束时间 - 拼团有效时长 */
    private LocalDateTime validEndTime;
}
