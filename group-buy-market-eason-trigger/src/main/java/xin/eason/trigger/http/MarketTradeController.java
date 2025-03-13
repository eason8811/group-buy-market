package xin.eason.trigger.http;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;
import xin.eason.api.IMarketTradeController;
import xin.eason.api.dto.LockMarketPayOrderRequestDTO;
import xin.eason.api.dto.LockMarketPayOrderResponseDTO;
import xin.eason.api.dto.SettlementOrderRequestDTO;
import xin.eason.api.dto.SettlementOrderResponseDTO;
import xin.eason.api.response.Result;
import xin.eason.domain.activity.model.entity.MarketProductEntity;
import xin.eason.domain.activity.model.entity.TrailResultEntity;
import xin.eason.domain.activity.service.IIndexGroupBuyMarketService;
import xin.eason.domain.trade.model.aggregate.GroupBuyOrderAggregate;
import xin.eason.domain.trade.model.entity.*;
import xin.eason.domain.trade.service.ITradeLockOrderService;
import xin.eason.domain.trade.service.ITradeSettlementOrderService;

import java.time.LocalDateTime;

@Slf4j
@CrossOrigin("*")
@RestController
@RequestMapping("/api/v1/gbm/trade")
@RequiredArgsConstructor
public class MarketTradeController implements IMarketTradeController {

    /**
     * 提供 trade 领域 锁定订单服务
     */
    private final ITradeLockOrderService tradeLockService;
    /**
     * 提供 trade 领域 结算订单服务
     */
    private final ITradeSettlementOrderService tradeSettlementOrderService;
    /**
     * 提供拼团活动领域服务
     */
    private final IIndexGroupBuyMarketService marketService;

    /**
     * <p>根据
     * <table>
     * <tbody>
     * <tr>
     * <td><b>字段名</b></td>
     * <td>userId</td>
     * <td>teamId</td>
     * <td>activityId</td>
     * <td>source</td>
     * <td>channel</td>
     * <td>outerOrderId</td>
     * </tr>
     * <tr>
     * <td><b>字段含义</b></td>
     * <td>用户 ID</td>
     * <td>组队 ID</td>
     * <td>拼团活动 ID</td>
     * <td>商品来源</td>
     * <td>商品渠道</td>
     * <td>外部订单 ID</td>
     * </tr>
     * </tbody>
     * </table>
     * </p>
     * <p>获取拼团结果, 包括:
     * <table>
     * <tbody>
     * <tr>
     * <td><b>字段名</b></td>
     * <td>orderId</td>
     * <td>teamId</td>
     * <td>discountPrice</td>
     * <td>tradeOrderStatus</td>
     * </tr>
     * <tr>
     * <td><b>字段含义</b></td>
     * <td>预购订单 ID</td>
     * <td>生成 (传入) 的 ID</td>
     * <td>商品折扣价格</td>
     * <td>订单状态</td>
     * </tr>
     * </tbody>
     * </table>
     * 并锁定优惠(锁定订单, 进入拼团队伍占住坑位)</p>
     *
     * @param lockMarketPayOrderRequestDTO 锁定拼团订单数据传输类对象
     * @return 锁定拼团订单信息
     */
    @Override
    @PostMapping("/lock_market_pay_order")
    public Result<LockMarketPayOrderResponseDTO> lockMarketPayOrder(@RequestBody LockMarketPayOrderRequestDTO lockMarketPayOrderRequestDTO) {
        try {
            // 查询是否有未支付订单
            String outerOrderId = lockMarketPayOrderRequestDTO.getOuterOrderId();
            String userId = lockMarketPayOrderRequestDTO.getUserId();
            String teamId = lockMarketPayOrderRequestDTO.getTeamId();
            String goodsId = lockMarketPayOrderRequestDTO.getGoodsId();
            String source = lockMarketPayOrderRequestDTO.getSource();
            String channel = lockMarketPayOrderRequestDTO.getChannel();
            Long activityId = lockMarketPayOrderRequestDTO.getActivityId();
            String notifyUrl = lockMarketPayOrderRequestDTO.getNotifyUrl();

            log.info("正在进行锁定订单, userId: {}, outerOrderId: {}", userId, outerOrderId);
            if (StringUtils.isBlank(userId) || StringUtils.isBlank(source) || StringUtils.isBlank(channel) || StringUtils.isBlank(goodsId) || null == activityId || StringUtils.isBlank(notifyUrl)) {
                log.error("参数非法! param: {}", lockMarketPayOrderRequestDTO);
                return Result.error("参数非法");
            }

            // 返回内部订单编号, 折扣价格, 订单状态
            PayOrderEntity payOrderEntity = tradeLockService.checkUnpayOrder(userId, outerOrderId);

            // 如有, 直接返回
            if (payOrderEntity != null) {
                log.info("userId: {}, outerOrderId: {} 已有订单, 订单处于初始锁定状态, 直接返回订单信息: {}", userId, outerOrderId, payOrderEntity);
                return Result.success(
                        LockMarketPayOrderResponseDTO.builder()
                                .discountPrice(payOrderEntity.getDiscountPrice())
                                .orderId(payOrderEntity.getOrderId())
                                .teamId(teamId)
                                .tradeOrderStatus(payOrderEntity.getOrderListStatus().getCode())
                                .build()
                );
            }

            log.info("userId: {}, outerOrderId: {} 未有订单, 正在检测用户是否为拼团团长...", userId, outerOrderId);

            // 检测是否是拼团团长
            PayOrderTeamEntity payOrderTeamEntity;
            if (teamId != null && !teamId.isBlank()) {
                // 不是拼团团长, 查询拼团队伍是否满员, 如有, 直接返回拼团失败
                log.info("用户不是拼团团长, 正在加入 teamId: {} 的队伍, 正在查询该队伍的名额是否已满...", teamId);
                payOrderTeamEntity = tradeLockService.checkTeamProgress(teamId);
                // 判断队伍是否可以加入
                if (payOrderTeamEntity.getTeamProgress().teamIsAvailable()) {
                    log.info("用户 ID: {}, outerOrderId: {}, teamId: {}, 队伍已满, 拼团失败!", userId, outerOrderId, teamId);
                    return Result.error("拼团失败! 队伍已满!");
                }
                log.info("teamId: {} 的队伍可以加入", teamId);
            } else {
                log.info("用户 ID: {}, 是拼团队伍团长", userId);
                payOrderTeamEntity = PayOrderTeamEntity.builder().notifyUrl(notifyUrl).build();
            }

            // 进行拼团试算
            log.info("正在进行拼团试算...");
            MarketProductEntity marketProductEntity = MarketProductEntity.builder()
                    .userId(userId)
                    .goodsId(goodsId)
                    .source(source)
                    .channel(channel)
                    .build();
            TrailResultEntity trailResultEntity = marketService.indexTrail(marketProductEntity);
            if (!trailResultEntity.getIsVisible()){
                // 不可见, 直接返回失败
                log.error("活动 activityId: {} 对用户 userId: {} 不可见!", activityId, userId);
                return Result.error("活动 activityId: " + activityId + " 对用户 userId: " + userId + " 不可见!");
            }

            if (!trailResultEntity.getIsEnable()) {
                // 不可参与, 直接返回失败
                log.error("活动 activityId: {} 对用户 userId: {} 不可参与!", activityId, userId);
                return Result.error("活动 activityId: " + activityId + " 对用户 userId: " + userId + " 不可参与!");
            }

            // 锁定拼团订单 (事务)
            log.info("正在锁定拼团订单...");
            GroupBuyOrderAggregate groupBuyOrderAggregateReq = GroupBuyOrderAggregate.builder()
                    .userId(userId)
                    .outerOrderId(outerOrderId)
                    .payOrderTeamEntity(payOrderTeamEntity)
                    .payOrderActivityEntity(
                            PayOrderActivityEntity.builder()
                                    .activityId(activityId)
                                    .targetCount(trailResultEntity.getTargetCount())
                                    .startTime(trailResultEntity.getStartTime())
                                    .endTime(trailResultEntity.getEndTime())
                                    .validTime(trailResultEntity.getValidTime())
                                    .build()
                    )
                    .payOrderDiscountEntity(
                            PayOrderDiscountEntity.builder()
                                    .source(source)
                                    .channel(channel)
                                    .goodsId(goodsId)
                                    .goodsName(trailResultEntity.getGoodsName())
                                    .originalPrice(trailResultEntity.getOriginalPrice())
                                    .discountPrice(trailResultEntity.getDeductionPrice())
                                    .build()
                    )
                    .payOrderEntity(
                            PayOrderEntity.builder()
                                    .discountPrice(trailResultEntity.getDeductionPrice())
                                    .payPrice(trailResultEntity.getPayPrice())
                                    .build()
                    )
                    .build();
            GroupBuyOrderAggregate groupBuyOrderAggregateRes = tradeLockService.lockGroupBuyOrder(groupBuyOrderAggregateReq);

            // 返回已锁定的拼团订单信息
            LockMarketPayOrderResponseDTO responseDTO = LockMarketPayOrderResponseDTO.builder()
                    .orderId(groupBuyOrderAggregateRes.getPayOrderEntity().getOrderId())
                    .teamId(groupBuyOrderAggregateRes.getPayOrderTeamEntity().getTeamId())
                    .discountPrice(groupBuyOrderAggregateRes.getPayOrderDiscountEntity().getDiscountPrice())
                    .tradeOrderStatus(groupBuyOrderAggregateRes.getPayOrderEntity().getOrderListStatus().getCode())
                    .build();
            log.info("拼团订单内部 ID: {}, 外部订单 ID: {}, userId: {} 锁定优惠成功! 拼团订单信息: {}", responseDTO.getOrderId(), outerOrderId, userId, responseDTO);
            return Result.success(responseDTO);
        } catch (Exception e) {
            log.error("{}", e.getMessage(), e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * <p>根据 订单SC值, 用户 ID, 外部订单 ID, 支付时间 进行订单结算</p>
     *
     * @param settlementOrderRequestDTO 订单结算请求数据传输对象
     * @return 订单结算响应数据传输对象
     */
    @Override
    @PostMapping("/settlement_market_pay_order")
    public Result<SettlementOrderResponseDTO> settleMarketPayOrder(@RequestBody SettlementOrderRequestDTO settlementOrderRequestDTO) {
        try {
            String userId = settlementOrderRequestDTO.getUserId();
            String source = settlementOrderRequestDTO.getSource();
            String channel = settlementOrderRequestDTO.getChannel();
            String outerOrderId = settlementOrderRequestDTO.getOuterOrderId();
            LocalDateTime payTime = settlementOrderRequestDTO.getPayTime();
            log.info("营销交易组队结算开始, userId: {}, outTradeNo: {}", userId, outerOrderId);

            if (StringUtils.isBlank(userId) || StringUtils.isBlank(source) || StringUtils.isBlank(channel) || StringUtils.isBlank(outerOrderId) || null == payTime) {
                log.error("参数非法! param: {}", settlementOrderRequestDTO);
                return Result.error("参数非法!");
            }
            // 进行订单结算服务
            OrderSettlementEntity orderSettlementEntity = OrderSettlementEntity.builder()
                    .source(source)
                    .channel(channel)
                    .userId(userId)
                    .outerOrderId(outerOrderId)
                    .payTime(payTime)
                    .build();

            OrderSettlementSuccessEntity orderSettlementSuccessEntity = tradeSettlementOrderService.settlementPayOrder(orderSettlementEntity);

            // 返回数据
            SettlementOrderResponseDTO settlementOrderResponseDTO = SettlementOrderResponseDTO.builder()
                    .source(orderSettlementSuccessEntity.getSource())
                    .channel(orderSettlementSuccessEntity.getChannel())
                    .userId(orderSettlementSuccessEntity.getUserId())
                    .outerOrderId(orderSettlementSuccessEntity.getOuterOrderId())
                    .teamId(orderSettlementSuccessEntity.getTeamId())
                    .activityId(orderSettlementSuccessEntity.getActivityId())
                    .build();
            return Result.success(settlementOrderResponseDTO);
        } catch (Exception e) {
            log.error("{}", e.getMessage(), e);
            return Result.error(e.getMessage());
        }
    }
}
