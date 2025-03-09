package xin.eason.domain.activity.service.discount;

import lombok.RequiredArgsConstructor;
import xin.eason.domain.activity.adapter.repository.IActivityRepository;
import xin.eason.domain.activity.model.valobj.DiscountType;
import xin.eason.domain.activity.model.valobj.GroupBuyActivityDiscountVO;

import java.math.BigDecimal;

@RequiredArgsConstructor
public abstract class AbstractDiscountCalculate implements IDiscountCalculateService{

    /**
     * 活动 repository 仓储适配器接口
     */
    private final IActivityRepository activityRepository;

    /**
     * 计算打折后的价格
     *
     * @param userId           用户 ID, 用于校验人群标签 (活动是否对该用户可见, 用户是否可以参与这个活动)
     * @param originalPrice    原始价格
     * @param groupBuyDiscount 拼团活动折扣值对象
     * @return 折后价格
     */
    @Override
    public BigDecimal calculate(String userId, BigDecimal originalPrice, GroupBuyActivityDiscountVO.GroupBuyDiscount groupBuyDiscount) {
        if (DiscountType.TAG.equals(groupBuyDiscount.getDiscountType())) {
            // 如果折扣类型是 tag (使用标签) 则进行人群标签校验
            boolean isCrowdRange = filterTagId(groupBuyDiscount.getTagId(), userId);
            if (isCrowdRange)
                return doCalculate(originalPrice, groupBuyDiscount);
            return originalPrice;
        }
        // 否则直接放行
        return doCalculate(originalPrice, groupBuyDiscount);
    }

    /**
     * 过滤人群标签
     * @param userId 用户 ID
     * @return 用户是否可以享受该活动的折扣
     */
    public boolean filterTagId(String tagId, String userId) {
        // 进行人群标签过滤
        return activityRepository.queryUserInCrowd(tagId, userId);
    }

    /**
     * 执行计算的抽象方法, 交给子类进行实现
     * @param originalPrice 商品原始价格
     * @param groupBuyDiscount 拼团活动折扣值对象
     * @return 折后价格
     */
    public abstract BigDecimal doCalculate(BigDecimal originalPrice, GroupBuyActivityDiscountVO.GroupBuyDiscount groupBuyDiscount);
}
