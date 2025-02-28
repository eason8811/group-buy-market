package xin.eason.infrastructure.dao.po;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import xin.eason.domain.xxx.model.valobj.ActivityStatus;
import xin.eason.domain.xxx.model.valobj.GroupType;

import java.time.LocalDateTime;

/**
 * 拼团活动表
 * <p>用于存放不同的拼团活动</p>
 */
@Data
@TableName("group_buy_activity")
public class GroupBuyActivityPO {
    /**
     * 主键 ID
     */
    private Long id;
    /**
     * 活动ID
     */
    private Long activityId;
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
     * 折扣 ID 用于关联折扣条目, 折扣条目中存有具体的折扣方式, 折扣公式等
     */
    private String discountId;
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
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
