package xin.eason.domain.activity.service.trail.thread;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import xin.eason.domain.activity.adapter.repository.IActivityRepository;
import xin.eason.domain.activity.model.valobj.GroupBuyActivityDiscountVO;
import xin.eason.types.exception.NoMarketConfigException;

import java.util.concurrent.Callable;

@Slf4j
@RequiredArgsConstructor
public class QueryGroupBuyActivityDiscountVO implements Callable<GroupBuyActivityDiscountVO> {

    /**
     * 来源, 与 {@link #channel} 组成 <b>SC</b>
     */
    private final String source;
    /**
     * 渠道, 与 {@link #source} 组成 <b>SC</b>
     */
    private final String channel;
    /**
     * 拼团商品 ID
     */
    private final String goodsId;
    /**
     * 活动 repository 仓储适配器接口
     */
    private final IActivityRepository activityRepository;

    /**
     * 通过仓储根据 {@link #source} 和 {@link #channel} 获取 {@link GroupBuyActivityDiscountVO} 活动和折扣的值对象
     * @return {@link GroupBuyActivityDiscountVO} 活动和折扣的值对象
     * @throws Exception 抛出所有错误
     */
    @Override
    public GroupBuyActivityDiscountVO call() throws Exception {
        try {
            // 没有错误直接返回
            return activityRepository.queryGroupBuyActivityDiscountVO(source, channel, goodsId);
        } catch (NoMarketConfigException e) {
            // 捕捉 无营销配置异常, 其他异常正常抛出
            log.error("{}", e.getMessage(), e);
            return null;
        }
    }
}
