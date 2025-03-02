package xin.eason.infrastructure.adapter.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import xin.eason.domain.activity.adapter.repository.IActivityRepository;
import xin.eason.domain.activity.model.valobj.GroupBuyActivityDiscountVO;
import xin.eason.domain.activity.model.valobj.SkuVO;
import xin.eason.infrastructure.dao.IGroupBuyActivity;
import xin.eason.infrastructure.dao.IGroupBuyDiscount;
import xin.eason.infrastructure.dao.IGroupBuySku;
import xin.eason.infrastructure.dao.po.GroupBuyActivityPO;
import xin.eason.infrastructure.dao.po.GroupBuyDiscountPO;
import xin.eason.infrastructure.dao.po.SkuPO;

/**
 * 活动 repository 仓储适配器接口
 */
@Component
@RequiredArgsConstructor
public class ActivityRepository implements IActivityRepository {
    /**
     * 拼团活动表对应 Mapper
     */
    private final IGroupBuyActivity groupBuyActivity;
    /**
     * 拼团折扣表对应 Mapper
     */
    private final IGroupBuyDiscount groupBuyDiscount;
    /**
     * 拼团商品表对应 Mapper
     */
    private final IGroupBuySku groupBuySku;

    /**
     * 根据 <b>SC</b> 获取 {@link GroupBuyActivityDiscountVO} 拼团活动及其折扣类的对象
     *
     * @param source  来源
     * @param channel 渠道
     * @return 拼团活动及其折扣类的对象
     */
    @Override
    public GroupBuyActivityDiscountVO queryGroupBuyActivityDiscountVO(String source, String channel) {
        // 根据 source 和 channel 查询活动信息, 并获取最新的数据
        LambdaQueryWrapper<GroupBuyActivityPO> activityWrapper = new LambdaQueryWrapper<>();
        activityWrapper.eq(GroupBuyActivityPO::getSource, source).eq(GroupBuyActivityPO::getChannel, channel).orderByDesc(GroupBuyActivityPO::getId);
        GroupBuyActivityPO activityPO = groupBuyActivity.selectOne(activityWrapper);

        // 根据获取到的 groupBuyActivityPO 对象中的 discountId 属性到折扣表中查询具体折扣信息
        LambdaQueryWrapper<GroupBuyDiscountPO> discountWrapper = new LambdaQueryWrapper<>();
        discountWrapper.eq(GroupBuyDiscountPO::getDiscountId, activityPO.getDiscountId());
        GroupBuyDiscountPO discountPO = groupBuyDiscount.selectOne(discountWrapper);

        GroupBuyActivityDiscountVO.GroupBuyDiscount discountVO = GroupBuyActivityDiscountVO.GroupBuyDiscount.builder()
                .discountName(discountPO.getDiscountName())
                .discountDesc(discountPO.getDiscountDesc())
                .discountType(discountPO.getDiscountType())
                .marketPlan(discountPO.getMarketPlan())
                .marketExpr(discountPO.getMarketExpr())
                .tagId(discountPO.getTagId())
                .build();

        return GroupBuyActivityDiscountVO.builder()
                .activityId(activityPO.getActivityId())
                .activityName(activityPO.getActivityName())
                .source(activityPO.getSource())
                .channel(activityPO.getChannel())
                .goodsId(activityPO.getGoodsId())
                .groupBuyDiscount(discountVO)
                .groupType(activityPO.getGroupType())
                .takeLimitCount(activityPO.getTakeLimitCount())
                .target(activityPO.getTarget())
                .validTime(activityPO.getValidTime())
                .status(activityPO.getStatus())
                .startTime(activityPO.getStartTime())
                .endTime(activityPO.getEndTime())
                .tagId(activityPO.getTagId())
                .tagScope(activityPO.getTagScope())
                .build();
    }

    /**
     * 根据 ID 获取 {@link SkuVO} 商品信息值对象
     *
     * @param goodsId 商品 ID
     * @return {@link SkuVO} 商品信息值对象
     */
    @Override
    public SkuVO querySkuVO(String goodsId) {
        LambdaQueryWrapper<SkuPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SkuPO::getGoodsId, goodsId);
        SkuPO skuPO = groupBuySku.selectOne(wrapper);

        return SkuVO.builder()
                .goodsId(skuPO.getGoodsId())
                .goodsName(skuPO.getGoodsName())
                .originalPrice(skuPO.getOriginalPrice())
                .build();
    }
}
