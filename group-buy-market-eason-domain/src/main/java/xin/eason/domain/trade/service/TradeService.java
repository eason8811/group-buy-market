package xin.eason.domain.trade.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import xin.eason.domain.trade.adapter.repository.ITradeRepository;
import xin.eason.domain.trade.model.aggregate.GroupBuyOrderAggregate;
import xin.eason.domain.trade.model.entity.PayOrderEntity;
import xin.eason.domain.trade.model.entity.PayOrderTeamEntity;

/**
 * trade 领域服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TradeService implements ITradeService {
    /**
     * 拼团交易 trade 领域仓储
     */
    private final ITradeRepository tradeRepository;

    /**
     * 查询该用户的未支付订单
     *
     * @param userId       用户 ID
     * @param outerOrderId 外部订单 ID
     * @return 拼团订单实体
     */
    @Override
    public PayOrderEntity checkUnpayOrder(String userId, String outerOrderId) {
        log.info("拼团 trade 领域正在查询用户 ID: {}, 外部订单 ID: {} 是否有未支付订单...", userId, outerOrderId);
        return tradeRepository.queryUnpayOrder(userId, outerOrderId);
    }

    /**
     * 根据组队 ID 查询队伍的拼团进度
     *
     * @param teamId 组队 ID
     * @return 拼团队伍的实体类对象
     */
    @Override
    public PayOrderTeamEntity checkTeamProgress(String teamId) {
        log.info("拼团 trade 领域正在查询队伍 ID: {} 的拼团进度...", teamId);
        return tradeRepository.queryTeamProgress(teamId);
    }

    /**
     * 根据拼团订单聚合内的信息进行锁定订单操作
     *
     * @param groupBuyOrderAggregate 拼团订单聚合
     * @return 锁定订单完成后的拼团订单聚合
     */
    @Override
    public GroupBuyOrderAggregate lockGroupBuyOrder(GroupBuyOrderAggregate groupBuyOrderAggregate) {
        log.info("用户 ID: {}, 外部订单 ID: {}, 正在锁定订单...", groupBuyOrderAggregate.getUserId(), groupBuyOrderAggregate.getOuterOrderId());
        // 锁定订单
        tradeRepository.lockOrder(groupBuyOrderAggregate);

        // 返回完成后的订单聚合
        return groupBuyOrderAggregate;
    }
}
