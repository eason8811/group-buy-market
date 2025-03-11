package xin.eason.domain.activity.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 试算结果实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrailResultEntity {

    /** 商品ID */
    private String goodsId;
    /** 商品名称 */
    private String goodsName;
    /** 原始价格 */
    private BigDecimal originalPrice;
    /** 扣减金额 */
    private BigDecimal deductionPrice;
    /**
     * 支付价格
     */
    private BigDecimal payPrice;
    /** 拼团目标数量 */
    private Integer targetCount;
    /** 拼团开始时间 */
    private LocalDateTime startTime;
    /** 拼团结束时间 */
    private LocalDateTime endTime;
    /**
     * 拼团可用时长
     */
    private Integer validTime;
    /** 是否可见拼团 */
    private Boolean isVisible;
    /** 是否可参与进团 */
    private Boolean isEnable;
}
