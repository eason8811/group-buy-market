package xin.eason.domain.trade.service.lock.filter.node;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import xin.eason.domain.activity.model.valobj.ActivityStatus;
import xin.eason.domain.trade.adapter.repository.ITradeRepository;
import xin.eason.domain.trade.model.entity.GroupBuyActivityEntity;
import xin.eason.domain.trade.model.entity.TradeRuleFilterRequestEntity;
import xin.eason.domain.trade.model.entity.TradeRuleFilterResponseEntity;
import xin.eason.domain.trade.service.lock.filter.factory.TradeRuleFilterFactory;
import xin.eason.types.design.framework.link.multimodel.handler.ILogicChainNodeHandler;
import xin.eason.types.exception.ActivityUnavailableException;

import java.time.LocalDateTime;

/**
 * 活动可用性过滤节点
 */
@Slf4j
@Component
public class ActivityAvailableFilterNode implements ILogicChainNodeHandler<TradeRuleFilterRequestEntity, TradeRuleFilterResponseEntity, TradeRuleFilterFactory.DynamicContext> {
    /**
     * 交易 repository 仓储适配器接口
     */
    private final ITradeRepository tradeRepository;

    public ActivityAvailableFilterNode(ITradeRepository tradeRepository) {
        this.tradeRepository = tradeRepository;
    }

    /**
     * 校验 指定用户 是否能够参加 指定活动 (校验活动状态和活动是否过期)
     *
     * @param requestParam   入参
     * @param dynamicContext 动态上下文
     * @return 出参
     */
    @Override
    public TradeRuleFilterResponseEntity apply(TradeRuleFilterRequestEntity requestParam, TradeRuleFilterFactory.DynamicContext dynamicContext) {
        log.info("交易规则过滤责任链 [{}]: 活动可用性校验, activityId: {}", this.getClass().getSimpleName(), requestParam.getActivityId());
        // 查询活动
        GroupBuyActivityEntity activityEntity = tradeRepository.queryActivityByActivityId(requestParam.getActivityId());

        // 校验活动是否在有效时间内
        LocalDateTime currentTime = LocalDateTime.now();
        if (currentTime.isAfter(activityEntity.getEndTime()))
            throw new ActivityUnavailableException("活动不可用! 当前时间活动已结束!");
        if (currentTime.isBefore(activityEntity.getStartTime()))
            throw new ActivityUnavailableException("活动不可用! 当前时间活动未开始!");

        // 校验活动状态是否有效
        if (!ActivityStatus.VALIDATE.equals(activityEntity.getStatus()))
            throw new ActivityUnavailableException("活动不可用! 当前活动状态为: " + activityEntity.getStatus().getDesc());

        dynamicContext.setGroupBuyActivityEntity(activityEntity);
        return next(requestParam, dynamicContext);
    }
}
