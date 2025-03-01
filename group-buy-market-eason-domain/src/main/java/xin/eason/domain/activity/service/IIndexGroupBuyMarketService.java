package xin.eason.domain.activity.service;

import xin.eason.domain.activity.model.entity.MarketProductEntity;
import xin.eason.domain.activity.model.entity.TrailResultEntity;

/**
 * 拼团首页活动领域服务接口
 */
public interface IIndexGroupBuyMarketService {

    /**
     * 进行首页优惠产品试算
     * @param marketProductEntity 营销产品实体类对象
     * @return 试算结果实体类对象
     */
    TrailResultEntity indexTrail(MarketProductEntity marketProductEntity);
}
