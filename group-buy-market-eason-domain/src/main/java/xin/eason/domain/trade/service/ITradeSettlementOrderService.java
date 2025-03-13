package xin.eason.domain.trade.service;

import xin.eason.domain.trade.model.entity.OrderSettlementEntity;
import xin.eason.domain.trade.model.entity.OrderSettlementSuccessEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * trade 领域 <b>结算订单</b> 服务接口
 */
public interface ITradeSettlementOrderService {
    /**
     * <p>根据订单结算实体的相关信息进行订单结算</p>
     * <p>返回 <b>SC 值, userId, outerOrderId, teamId, activityId</b></p>
     *
     * @param orderSettlementEntity 订单结算实体
     * @return 订单结算成功实体
     */
    OrderSettlementSuccessEntity settlementPayOrder(OrderSettlementEntity orderSettlementEntity);

    /**
     * 执行全部未进行回调的回调任务
     * @return 回调任务的响应信息
     */
    Map<String, Integer> execNotifyJob();

    /**
     * 进行指定 teamId 的回调任务
     * @param teamId 拼团队伍 ID
     * @return 回调任务的响应信息
     */
    Map<String, Integer> execNotifyJob(String teamId);

    /**
     * 根据当前时间查询状态 不为成功 的队伍是否存在, 如果存在, 则将其状态更改为 失败, 连带修改队伍中所有用户的订单明细状态为 超时关单
     *
     * @param currentTime 当前时间
     * @return 修改成功的 teamId 列表
     */
    List<String> setInvalidTeamToFailed(LocalDateTime currentTime);
}
