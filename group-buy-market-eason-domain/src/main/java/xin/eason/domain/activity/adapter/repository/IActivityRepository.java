package xin.eason.domain.activity.adapter.repository;

import xin.eason.domain.activity.model.valobj.GroupBuyActivityDiscountVO;
import xin.eason.domain.activity.model.valobj.SkuVO;

/**
 * 活动 repository 仓储适配器接口
 */
public interface IActivityRepository {

    /**
     * 根据 <b>SC</b> 获取 {@link GroupBuyActivityDiscountVO} 拼团活动及其折扣类的对象
     * @param source 来源
     * @param channel 渠道
     * @return 拼团活动及其折扣类的对象
     */
    GroupBuyActivityDiscountVO queryGroupBuyActivityDiscountVO(String source, String channel);

    /**
     * 根据 ID 获取 {@link SkuVO} 商品信息值对象
     * @param goodsId 商品 ID
     * @return {@link SkuVO} 商品信息值对象
     */
    SkuVO querySkuVO(String goodsId);
}
