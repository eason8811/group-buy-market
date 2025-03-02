package xin.eason.domain.activity.service.trail.thread;

import lombok.RequiredArgsConstructor;
import xin.eason.domain.activity.adapter.repository.IActivityRepository;
import xin.eason.domain.activity.model.valobj.SkuVO;

import java.util.concurrent.Callable;

@RequiredArgsConstructor
public class QuerySkuVO implements Callable<SkuVO> {
    /**
     * 商品的唯一 ID
     */
    private final String goodsId;
    /**
     * 活动 repository 仓储适配器接口
     */
    private final IActivityRepository activityRepository;

    /**
     * 根据 {@link #goodsId} 获取 {@link SkuVO} 商品信息值对象
     * @return {@link SkuVO} 商品信息值对象
     * @throws Exception 抛出所有错误
     */
    @Override
    public SkuVO call() throws Exception {
        return activityRepository.querySkuVO(goodsId);
    }
}
