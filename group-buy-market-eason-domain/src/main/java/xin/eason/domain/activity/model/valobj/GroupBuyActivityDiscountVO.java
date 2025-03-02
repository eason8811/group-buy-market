package xin.eason.domain.activity.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 拼团活动及其对应折扣的值对象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupBuyActivityDiscountVO {
    /**
     * 活动ID
     */
    private Long activityId;
    /**
     * 活动名称
     */
    private String activityName;
    /**
     * 来源 与 channel 组成 SC
     */
    private String source;
    /**
     * 渠道 与 source 组成 SC
     */
    private String channel;
    /**
     * 商品 ID
     */
    private String goodsId;
    /**
     * <b>折扣对象</b>
     */
    private GroupBuyDiscount groupBuyDiscount;
    /**
     * 方式 ( 0, 自动成团 ) ( 1, 达成目标拼团 )
     */
    private GroupType groupType;
    /**
     * 拼团次数限制
     */
    private Integer takeLimitCount;
    /**
     * 拼团目标人数
     */
    private Integer target;
    /**
     * 拼团可用时长
     */
    private Integer validTime;
    /**
     * 活动状态 ( 0, 创建 ) ( 1, 生效 ) ( 2, 过期 ) ( 3, 废弃 )
     */
    private ActivityStatus status;
    /**
     * 活动开始时间
     */
    private LocalDateTime startTime;
    /**
     * 活动结束时间
     */
    private LocalDateTime endTime;
    /**
     * 人群 ID, 用于链接人群表, 表内具体存有不同的人群信息, 用于区分不同人群对该活动的可见性和可参与性
     */
    private String tagId;
    /**
     * 人群标签规则范围 多选: ( 1, 可见限制 ) ( 2, 参与限制 )
     */
    private String tagScope;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GroupBuyDiscount {
        /**
         * 折扣标题
         */
        private String discountName;
        /**
         * 折扣描述
         */
        private String discountDesc;
        /**
         * 折扣类型 ( 0, base ) ( 1, tag )
         * <p>基础类型 (对所有人群都有用), tag类型(对特定人群标签的人群有用)</p>
         */
        private DiscountType discountType;
        /**
         * 营销优惠计划 ( ZJ, 直减 ) ( MJ, 满减 ) ( N, N元购 )
         */
        private MarketPlan marketPlan;
        /**
         * 营销优惠表达式
         */
        private String marketExpr;
        /**
         * 人群标签
         */
        private String tagId;
    }
}
