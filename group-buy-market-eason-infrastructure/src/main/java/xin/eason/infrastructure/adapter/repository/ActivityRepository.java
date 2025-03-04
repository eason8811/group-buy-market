package xin.eason.infrastructure.adapter.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import xin.eason.domain.activity.adapter.repository.IActivityRepository;
import xin.eason.domain.activity.model.valobj.GroupBuyActivityDiscountVO;
import xin.eason.domain.activity.model.valobj.SkuVO;
import xin.eason.infrastructure.dao.IGroupBuyActivity;
import xin.eason.infrastructure.dao.IGroupBuyDiscount;
import xin.eason.infrastructure.dao.IGroupBuySku;
import xin.eason.infrastructure.dao.ISCSkuActivity;
import xin.eason.infrastructure.dao.po.GroupBuyActivityPO;
import xin.eason.infrastructure.dao.po.GroupBuyDiscountPO;
import xin.eason.infrastructure.dao.po.SCSkuActivityPO;
import xin.eason.infrastructure.dao.po.SkuPO;
import xin.eason.types.exception.NoMarketConfigException;

/**
 * 活动 repository 仓储适配器接口
 */
@Slf4j
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
     * 拼团商品 - 拼团活动表对应 Mapper
     */
    private final ISCSkuActivity skuActivity;

    /**
     * 根据 <b>SC</b> 获取 {@link GroupBuyActivityDiscountVO} 拼团活动及其折扣类的对象
     *
     * @param source  来源
     * @param channel 渠道
     * @param goodsId 拼团商品 ID
     * @return 拼团活动及其折扣类的对象
     */
    @Override
    public GroupBuyActivityDiscountVO queryGroupBuyActivityDiscountVO(String source, String channel, String goodsId) {
        // 根据 source 和 channel 在 商品-活动表 中查询商品信息, 根据商品信息中的 goodsId 获取最新的活动数据
        LambdaQueryWrapper<SCSkuActivityPO> scSkuActivityWrapper = new LambdaQueryWrapper<>();
        scSkuActivityWrapper.eq(SCSkuActivityPO::getSource, source).eq(SCSkuActivityPO::getChannel, channel).eq(SCSkuActivityPO::getGoodsId, goodsId).orderByAsc(SCSkuActivityPO::getId);
        SCSkuActivityPO scSkuActivityPO = skuActivity.selectOne(scSkuActivityWrapper);
        if (scSkuActivityPO == null) {
            // 如果为 null 则找不到营销配置, 抛出无营销配置异常
            throw new NoMarketConfigException("来源: " + source + ", 渠道: " + channel + " 无营销配置");
        }

        LambdaQueryWrapper<GroupBuyActivityPO> activityWrapper = new LambdaQueryWrapper<>();
        activityWrapper.eq(GroupBuyActivityPO::getActivityId, scSkuActivityPO.getActivityId()).orderByDesc(GroupBuyActivityPO::getUpdateTime);
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
                .source(scSkuActivityPO.getSource())
                .channel(scSkuActivityPO.getChannel())
                .goodsId(scSkuActivityPO.getGoodsId())
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
