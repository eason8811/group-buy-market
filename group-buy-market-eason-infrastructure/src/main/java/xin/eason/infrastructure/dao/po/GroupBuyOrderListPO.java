package xin.eason.infrastructure.dao.po;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import xin.eason.domain.trade.model.valobj.OrderListStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>拼团订单明细表</p>
 * <p>存放一个拼团 team 内每个人的交易明细</p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("group_buy_order_list")
public class GroupBuyOrderListPO {
    /**
     * 自增ID
     */
    private String id;
    /**
     * 用户ID
     */
    private String userId;
    /**
     * 拼单组队ID
     */
    private String teamId;
    /**
     * 订单ID
     */
    private String orderId;
    /**
     * 活动ID
     */
    private Long activityId;
    /**
     * 活动开始时间
     */
    private LocalDateTime startTime;
    /**
     * 活动结束时间
     */
    private LocalDateTime endTime;
    /**
     * 商品ID
     */
    private String goodsId;
    /**
     * 渠道
     */
    private String source;
    /**
     * 来源
     */
    private String channel;
    /**
     * 原始价格
     */
    private BigDecimal originalPrice;
    /**
     * 折扣金额
     */
    private BigDecimal deductionPrice;
    /**
     * 状态；0初始锁定、1消费完成
     */
    private OrderListStatus status;
    /**
     * 外部交易单号-确保外部调用唯一幂等
     */
    private String outTradeNo;
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
