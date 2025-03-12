package xin.eason.api;

import xin.eason.api.dto.LockMarketPayOrderRequestDTO;
import xin.eason.api.dto.LockMarketPayOrderResponseDTO;
import xin.eason.api.dto.SettlementOrderRequestDTO;
import xin.eason.api.dto.SettlementOrderResponseDTO;
import xin.eason.api.response.Result;

/**
 * 对外提供的拼团支付 API
 */
public interface IMarketTradeController {

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
    Result<LockMarketPayOrderResponseDTO> lockMarketPayOrder(LockMarketPayOrderRequestDTO lockMarketPayOrderRequestDTO);

    /**
     * <p>根据 订单SC值, 用户 ID, 外部订单 ID, 支付时间 进行订单结算</p>
     *
     * @param settlementOrderRequestDTO 订单结算请求数据传输对象
     * @return 订单结算响应数据传输对象
     */
    Result<SettlementOrderResponseDTO> settleMarketPayOrder(SettlementOrderRequestDTO settlementOrderRequestDTO);
}
