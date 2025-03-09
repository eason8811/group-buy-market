package xin.eason.domain.trade.service.lock.filter.node;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import xin.eason.domain.trade.adapter.repository.ITradeRepository;
import xin.eason.domain.trade.model.entity.TradeRuleFilterRequestEntity;
import xin.eason.domain.trade.model.entity.TradeRuleFilterResponseEntity;
import xin.eason.domain.trade.service.lock.filter.factory.TradeRuleFilterFactory;
import xin.eason.types.design.framework.link.multimodel.handler.ILogicChainNodeHandler;
import xin.eason.types.exception.JoinLimitOverException;

/**
 * 用户参与限制过滤节点
 */
@Slf4j
@Component
public class UserJoinLimitFilterNode implements ILogicChainNodeHandler<TradeRuleFilterRequestEntity, TradeRuleFilterResponseEntity, TradeRuleFilterFactory.DynamicContext> {
    /**
     * 交易 repository 仓储适配器接口
     */
    private final ITradeRepository tradeRepository;

    public UserJoinLimitFilterNode(ITradeRepository tradeRepository) {
        this.tradeRepository = tradeRepository;
    }

    /**
     * 处理节点内的逻辑
     *
     * @param requestParam   入参
     * @param dynamicContext 动态上下文
     * @return 出参
     */
    @Override
    public TradeRuleFilterResponseEntity apply(TradeRuleFilterRequestEntity requestParam, TradeRuleFilterFactory.DynamicContext dynamicContext) {
        log.info("交易规则过滤责任链 [{}]: 用户参与活动次数校验, activityId: {}", this.getClass().getSimpleName(), requestParam.getActivityId());
        // 根据 用户ID 和 活动ID 查询一个用户参与该活动的次数
        Long joinTimes = tradeRepository.queryUserJoinActivityTimes(requestParam.getActivityId(), requestParam.getUserId());
        if (dynamicContext.getGroupBuyActivityEntity().getJoinLimitCount() <= joinTimes)
            throw new JoinLimitOverException("用户 ID:" + requestParam.getUserId() + " 已经参加过 " + joinTimes + " 次活动, 活动 ID: " + requestParam.getActivityId() + ", 不可继续参加!");
        return TradeRuleFilterResponseEntity.builder()
                .userJoinTimes(joinTimes)
                .build();
    }
}
