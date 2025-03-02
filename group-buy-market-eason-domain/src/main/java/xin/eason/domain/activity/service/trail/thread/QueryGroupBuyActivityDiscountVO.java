package xin.eason.domain.activity.service.trail.thread;

import lombok.RequiredArgsConstructor;
import xin.eason.domain.activity.adapter.repository.IActivityRepository;
import xin.eason.domain.activity.model.valobj.GroupBuyActivityDiscountVO;

import java.util.concurrent.Callable;

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
        return activityRepository.queryGroupBuyActivityDiscountVO(source, channel);
    }
}
