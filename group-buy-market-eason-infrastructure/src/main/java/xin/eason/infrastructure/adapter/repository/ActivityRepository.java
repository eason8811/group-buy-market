package xin.eason.infrastructure.adapter.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBitSet;
import org.springframework.stereotype.Component;
import xin.eason.domain.activity.adapter.repository.IActivityRepository;
import xin.eason.domain.activity.model.entity.UserTeamInfoEntity;
import xin.eason.domain.activity.model.valobj.GroupBuyActivityDiscountVO;
import xin.eason.domain.activity.model.valobj.SkuVO;
import xin.eason.domain.activity.model.valobj.TeamStatisticVO;
import xin.eason.domain.trade.model.valobj.OrderListStatus;
import xin.eason.domain.trade.model.valobj.OrderStatus;
import xin.eason.infrastructure.dao.*;
import xin.eason.infrastructure.dao.po.*;
import xin.eason.infrastructure.dcc.DCCService;
import xin.eason.infrastructure.redis.IRedisService;
import xin.eason.types.exception.NoMarketConfigException;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 活动 repository 仓储适配器接口
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ActivityRepository implements IActivityRepository {
    /**
     * 拼团活动表对应 Mapper
     */
    private final IGroupBuyActivity groupBuyActivity;
    /**
     * 拼团折扣表对应 Mapper
     */
    private final IGroupBuyDiscount groupBuyDiscount;
    /**
     * 拼团商品表对应 Mapper
     */
    private final IGroupBuySku groupBuySku;
    /**
     * 拼团商品 - 拼团活动表对应 Mapper
     */
    private final ISCSkuActivity skuActivity;
    /**
     * Redis 服务
     */
    private final IRedisService redisService;
    /**
     * DCC 动态配置管理服务
     */
    private final DCCService dccService;
    /**
     * 拼团订单明细表对应 Mapper
     */
    private final IGroupBuyOrderList groupBuyOrderList;
    /**
     * 拼团订单表对应 Mapper
     */
    private final IGroupBuyOrder groupBuyOrder;

    /**
     * 根据 <b>SC</b> 获取 {@link GroupBuyActivityDiscountVO} 拼团活动及其折扣类的对象
     *
     * @param source  来源
     * @param channel 渠道
     * @param goodsId 拼团商品 ID
     * @return 拼团活动及其折扣类的对象
     */
    @Override
    public GroupBuyActivityDiscountVO queryGroupBuyActivityDiscountVO(String source, String channel, String goodsId) {
        // 根据 source 和 channel 在 商品-活动表 中查询商品信息, 根据商品信息中的 goodsId 获取最新的活动数据
        LambdaQueryWrapper<SCSkuActivityPO> scSkuActivityWrapper = new LambdaQueryWrapper<>();
        scSkuActivityWrapper.eq(SCSkuActivityPO::getSource, source).eq(SCSkuActivityPO::getChannel, channel).eq(SCSkuActivityPO::getGoodsId, goodsId).orderByAsc(SCSkuActivityPO::getId);
        SCSkuActivityPO scSkuActivityPO = skuActivity.selectOne(scSkuActivityWrapper);
        if (scSkuActivityPO == null) {
            // 如果为 null 则找不到营销配置, 抛出无营销配置异常
            throw new NoMarketConfigException("来源: " + source + ", 渠道: " + channel + " 无营销配置");
        }

        LambdaQueryWrapper<GroupBuyActivityPO> activityWrapper = new LambdaQueryWrapper<>();
        activityWrapper.eq(GroupBuyActivityPO::getActivityId, scSkuActivityPO.getActivityId()).orderByDesc(GroupBuyActivityPO::getUpdateTime);
        GroupBuyActivityPO activityPO = groupBuyActivity.selectOne(activityWrapper);

        // 根据获取到的 groupBuyActivityPO 对象中的 discountId 属性到折扣表中查询具体折扣信息
        LambdaQueryWrapper<GroupBuyDiscountPO> discountWrapper = new LambdaQueryWrapper<>();
        discountWrapper.eq(GroupBuyDiscountPO::getDiscountId, activityPO.getDiscountId());
        GroupBuyDiscountPO discountPO = groupBuyDiscount.selectOne(discountWrapper);

        GroupBuyActivityDiscountVO.GroupBuyDiscount discountVO = GroupBuyActivityDiscountVO.GroupBuyDiscount.builder()
                .discountName(discountPO.getDiscountName())
                .discountDesc(discountPO.getDiscountDesc())
                .discountType(discountPO.getDiscountType())
                .marketPlan(discountPO.getMarketPlan())
                .marketExpr(discountPO.getMarketExpr())
                .tagId(discountPO.getTagId())
                .build();

        return GroupBuyActivityDiscountVO.builder()
                .activityId(activityPO.getActivityId())
                .activityName(activityPO.getActivityName())
                .source(scSkuActivityPO.getSource())
                .channel(scSkuActivityPO.getChannel())
                .goodsId(scSkuActivityPO.getGoodsId())
                .groupBuyDiscount(discountVO)
                .groupType(activityPO.getGroupType())
                .takeLimitCount(activityPO.getTakeLimitCount())
                .target(activityPO.getTarget())
                .validTime(activityPO.getValidTime())
                .status(activityPO.getStatus())
                .startTime(activityPO.getStartTime())
                .endTime(activityPO.getEndTime())
                .tagId(activityPO.getTagId())
                .tagScope(activityPO.getTagScope())
                .build();
    }

    /**
     * 根据 ID 获取 {@link SkuVO} 商品信息值对象
     *
     * @param goodsId 商品 ID
     * @return {@link SkuVO} 商品信息值对象
     */
    @Override
    public SkuVO querySkuVO(String goodsId) {
        LambdaQueryWrapper<SkuPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SkuPO::getGoodsId, goodsId);
        SkuPO skuPO = groupBuySku.selectOne(wrapper);

        return SkuVO.builder()
                .goodsId(skuPO.getGoodsId())
                .goodsName(skuPO.getGoodsName())
                .originalPrice(skuPO.getOriginalPrice())
                .build();
    }

    /**
     * 根据 tagId 和 userId 在 redis 的位图中判断用户是否在人群范围内
     *
     * @param tagId  人群标签 ID
     * @param userId 用户 ID
     * @return 是否在人群标签内
     */
    @Override
    public Boolean queryUserInCrowd(String tagId, String userId) {
        RBitSet bitSet = redisService.getBitSet(tagId);
        // 是否存在
        return bitSet.get(redisService.getIndexFromUserId(userId));
    }

    /**
     * 判断服务是否降级
     *
     * @return 服务降级情况
     */
    @Override
    public boolean downGrade() {
        return dccService.isDownGrade();
    }

    /**
     * 判断服务对该用户是否切量
     *
     * @param userId 用户 ID
     * @return 服务切量情况
     */
    @Override
    public boolean cutRange(String userId) {
        return dccService.isCutRange(userId);
    }

    /**
     * 查询用户参与的拼团队伍列表
     *
     * @param activityId 活动 ID
     * @param userId     用户 ID (需要查询用户参与的拼团队伍)
     * @param ownerCount 需要查询的队伍数量
     * @return 拼团队伍信息列表
     */
    @Override
    public List<UserTeamInfoEntity> queryUserOwnerTeamInfoList(Long activityId, String userId, Integer ownerCount) {
        // 根据 activityId 和 userId 查询 ownerCount 条订单明细记录
        LambdaQueryWrapper<GroupBuyOrderListPO> orderListWrapper = new LambdaQueryWrapper<>();
        orderListWrapper.eq(GroupBuyOrderListPO::getActivityId, activityId)
                .eq(GroupBuyOrderListPO::getUserId, userId)
                .in(GroupBuyOrderListPO::getStatus, OrderListStatus.INIT_LOCK, OrderListStatus.PAY_COMPLETE)
                .gt(GroupBuyOrderListPO::getEndTime, LocalDateTime.now())
                .orderByDesc(GroupBuyOrderListPO::getId);
        List<GroupBuyOrderListPO> orderListPOList = groupBuyOrderList.selectList(orderListWrapper);
        if (orderListPOList == null || orderListPOList.isEmpty())
            return Collections.emptyList();
        orderListPOList = orderListPOList.subList(0, ownerCount);
        List<String> teamIdList = orderListPOList.stream().map(GroupBuyOrderListPO::getTeamId).toList();

        // 构造 teamId -> orderList Map用于组装数据时根据 teamId 获取订单明细
        Map<String, GroupBuyOrderListPO> teamIdTeamInfoMap = orderListPOList.stream().collect(Collectors.toMap(GroupBuyOrderListPO::getTeamId, orderListPO -> orderListPO));

        // 根据明细记录获取到的 teamId 查询拼团队伍详细信息
        LambdaQueryWrapper<GroupBuyOrderPO> orderWrapper = new LambdaQueryWrapper<>();
        orderWrapper.in(GroupBuyOrderPO::getTeamId, teamIdList);
        List<GroupBuyOrderPO> orderPOList = groupBuyOrder.selectList(orderWrapper);

        return orderPOList.stream()
                .map(order ->
                        UserTeamInfoEntity.builder()
                                .userId(userId)
                                .teamId(order.getTeamId())
                                .activityId(order.getActivityId())
                                .targetCount(order.getTargetCount())
                                .completeCount(order.getCompleteCount())
                                .lockCount(order.getLockCount())
                                .validStartTime(order.getValidStartTime())
                                .validEndTime(order.getValidEndTime())
                                .outerOrderId(teamIdTeamInfoMap.get(order.getTeamId()).getOutTradeNo())
                                .build()
                )
                .toList();
    }

    /**
     * 随机查询用户没有参与的拼团队伍列表
     *
     * @param activityId  活动 ID
     * @param userId      用户 ID
     * @param randomCount 随机查询的数量
     * @return 拼团队伍信息列表
     */
    @Override
    public List<UserTeamInfoEntity> queryUserRamdomTeamInfoList(Long activityId, String userId, Integer randomCount) {
        List<GroupBuyOrderListPO> orderListPOList = groupBuyOrderList.queryUserRamdomTeamInfoList(activityId, userId, randomCount * 2);
        if (orderListPOList == null || orderListPOList.isEmpty())
            return Collections.emptyList();

        // 若列表长度大于 randomCount 则打乱数据
        if (orderListPOList.size() >= randomCount) {
            Collections.shuffle(orderListPOList);
            orderListPOList = orderListPOList.subList(0, randomCount);
        }

        List<String> teamIdList = orderListPOList.stream().map(GroupBuyOrderListPO::getTeamId).toList();

        // 构造 teamId -> outerOrderId Map用于组装数据时根据 teamId 获取随机一个外部订单 ID
        Map<String, GroupBuyOrderListPO> teamIdTeamInfoMap = new HashMap<>();
        orderListPOList.forEach(orderListPO -> {
            if (teamIdTeamInfoMap.containsKey(orderListPO.getTeamId()))
                return;
            teamIdTeamInfoMap.put(orderListPO.getTeamId(), orderListPO);
        });

        // 根据 teamIdList 获取队伍详细信息
        LambdaQueryWrapper<GroupBuyOrderPO> orderWrapper = new LambdaQueryWrapper<>();
        orderWrapper.in(GroupBuyOrderPO::getTeamId, teamIdList);
        List<GroupBuyOrderPO> orderPOList = groupBuyOrder.selectList(orderWrapper);


        return orderPOList.stream()
                .map(orderPO -> {
                    GroupBuyOrderListPO groupBuyOrderListPO = teamIdTeamInfoMap.get(orderPO.getTeamId());
                    return UserTeamInfoEntity.builder()
                            .userId(groupBuyOrderListPO.getUserId())
                            .teamId(orderPO.getTeamId())
                            .activityId(orderPO.getActivityId())
                            .targetCount(orderPO.getTargetCount())
                            .completeCount(orderPO.getCompleteCount())
                            .lockCount(orderPO.getLockCount())
                            .validStartTime(orderPO.getValidStartTime())
                            .validEndTime(orderPO.getValidEndTime())
                            .outerOrderId(groupBuyOrderListPO.getOutTradeNo())
                            .build();
                })
                .toList();
    }

    /**
     * 统计指定 activityId 活动内的拼团队伍数据
     *
     * @param activityId 活动 ID
     * @return 拼团队伍数据值对象
     */
    @Override
    public TeamStatisticVO queryTeamStatistic(Long activityId) {
        // 查询开团的总队伍数 (属于该活动的, 状态不为失败的队伍)
        LambdaQueryWrapper<GroupBuyOrderPO> totalTeamCountWrapper = new LambdaQueryWrapper<>();
        totalTeamCountWrapper.eq(GroupBuyOrderPO::getActivityId, activityId).ne(GroupBuyOrderPO::getStatus, OrderStatus.FAIL);
        Long totalTeamCount = groupBuyOrder.selectCount(totalTeamCountWrapper);

        // 查询完成拼团的队伍数 (属于该活动的, 状态为完成的队伍)
        LambdaQueryWrapper<GroupBuyOrderPO> totalCompleteTeamCountWrapper = new LambdaQueryWrapper<>();
        totalCompleteTeamCountWrapper.eq(GroupBuyOrderPO::getActivityId, activityId).eq(GroupBuyOrderPO::getStatus, OrderStatus.COMPLETE);
        Long totalCompleteTeamCount = groupBuyOrder.selectCount(totalCompleteTeamCountWrapper);

        // 查询参与拼团的总人数 (属于该活动的拼团订单明细数量, 状态不为退单的用户  去重)
        LambdaQueryWrapper<GroupBuyOrderListPO> totalTeamUserCountWrapper = new LambdaQueryWrapper<>();
        totalTeamUserCountWrapper.eq(GroupBuyOrderListPO::getActivityId, activityId).ne(GroupBuyOrderListPO::getStatus, OrderListStatus.CLOSE);
        Long totalTeamUserCount = groupBuyOrderList.queryTotalTeamUserCount(totalTeamUserCountWrapper);

        return TeamStatisticVO.builder()
                .totalTeamCount(totalTeamCount)
                .totalCompleteTeamCount(totalCompleteTeamCount)
                .totalTeamUserCount(totalTeamUserCount)
                .build();
    }
}
