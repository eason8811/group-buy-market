package xin.eason.infrastructure.dao.po;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import xin.eason.domain.xxx.model.valobj.DiscountType;
import xin.eason.domain.xxx.model.valobj.MarketPlan;

/**
 * 拼团活动折扣表
 * <p>用于存放不同的折扣规则</p>
 */
@Data
@TableName("group_buy_discount")
public class GroupBuyDiscountPO {
    /**
     * 主键 ID
     */
    private Long id;
    /**
     * 折扣 ID
     */
    private Integer discountId;
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
    /**
     * 创建时间
     */
    private String createTime;
    /**
     * 更新时间
     */
    private String updateTime;
}
