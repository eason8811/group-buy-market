package xin.eason.domain.activity.service.discount;

import xin.eason.domain.activity.model.valobj.GroupBuyActivityDiscountVO;

import java.math.BigDecimal;

/**
 * 折扣计算接口, 为服务实现类提供规范
 */
public interface IDiscountCalculateService {
    /**
     * 计算打折后的价格
     * @param userId 用户 ID, 用于校验人群标签 (活动是否对该用户可见, 用户是否可以参与这个活动)
     * @param originalPrice 原始价格
     * @param groupBuyDiscount 拼团活动折扣值对象
     * @return 折后价格
     */
    BigDecimal calculate(String userId, BigDecimal originalPrice, GroupBuyActivityDiscountVO.GroupBuyDiscount groupBuyDiscount);
}
