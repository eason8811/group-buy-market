package xin.eason.domain.trade.service.lock.filter.node;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import xin.eason.domain.trade.adapter.repository.ITradeRepository;
import xin.eason.domain.trade.model.entity.TradeLockRuleFilterRequestEntity;
import xin.eason.domain.trade.model.entity.TradeLockRuleFilterResponseEntity;
import xin.eason.domain.trade.service.lock.filter.factory.TradeLockRuleFilterFactory;
import xin.eason.types.design.framework.link.multimodel.handler.IResponsibilityChainNodeHandler;
import xin.eason.types.exception.JoinLimitOverException;

/**
 * 用户参与限制过滤节点
 */
@Slf4j
@Component
public class UserJoinLimitFilterNode implements IResponsibilityChainNodeHandler<TradeLockRuleFilterRequestEntity, TradeLockRuleFilterResponseEntity, TradeLockRuleFilterFactory.DynamicContext> {
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
    public TradeLockRuleFilterResponseEntity apply(TradeLockRuleFilterRequestEntity requestParam, TradeLockRuleFilterFactory.DynamicContext dynamicContext) {
        log.info("交易规则过滤责任链 [{}]: 用户参与活动次数校验, activityId: {}", this.getClass().getSimpleName(), requestParam.getActivityId());
        // 根据 用户ID 和 活动ID 查询一个用户参与该活动的次数
        Long joinTimes = tradeRepository.queryUserJoinActivityTimes(requestParam.getActivityId(), requestParam.getUserId());
        if (dynamicContext.getGroupBuyActivityEntity().getJoinLimitCount() <= joinTimes)
            throw new JoinLimitOverException("用户 ID:" + requestParam.getUserId() + " 已经参加过 " + joinTimes + " 次活动, 活动 ID: " + requestParam.getActivityId() + ", 不可继续参加!");
        log.info("交易规则过滤责任链 [{}] 校验完成", this.getClass().getSimpleName());
        return TradeLockRuleFilterResponseEntity.builder()
                .userJoinTimes(joinTimes)
                .build();
    }
}
