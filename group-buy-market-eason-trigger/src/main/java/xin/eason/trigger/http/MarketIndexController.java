package xin.eason.trigger.http;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import xin.eason.api.IMarketIndexService;
import xin.eason.api.dto.GoodsMarketRequestDTO;
import xin.eason.api.dto.GoodsMarketResponseDTO;
import xin.eason.api.response.Result;
import xin.eason.domain.activity.model.entity.MarketProductEntity;
import xin.eason.domain.activity.model.entity.TrailResultEntity;
import xin.eason.domain.activity.model.entity.UserTeamInfoEntity;
import xin.eason.domain.activity.model.valobj.TeamStatisticVO;
import xin.eason.domain.activity.service.IIndexGroupBuyMarketService;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@CrossOrigin("*")
@RestController
@RequestMapping("/api/v1/gbm/index")
@RequiredArgsConstructor
public class MarketIndexController implements IMarketIndexService {
    /**
     * 拼团首页 activity 领域服务
     */
    private final IIndexGroupBuyMarketService indexGroupBuyMarketService;

    /**
     * 查询拼团营销配置
     *
     * @param goodsMarketRequestDTO 拼团首页展示信息请求数据传输类对象
     * @return 拼团首页展示响应数据传输类对象
     */
    @Override
    @PostMapping("/query_group_buy_market_config")
    public Result<GoodsMarketResponseDTO> queryGroupBuyMarketConfig(@RequestBody GoodsMarketRequestDTO goodsMarketRequestDTO) {
        try {
            log.info("正在查询拼团配置, userId: {}, goodsId: {}", goodsMarketRequestDTO.getUserId(), goodsMarketRequestDTO.getGoodsId());

            // 进行拼团试算
            log.info("正在进行拼团试算...");
            TrailResultEntity trailResultEntity = indexGroupBuyMarketService.indexTrail(MarketProductEntity.builder()
                    .userId(goodsMarketRequestDTO.getUserId())
                    .goodsId(goodsMarketRequestDTO.getGoodsId())
                    .source(goodsMarketRequestDTO.getSource())
                    .channel(goodsMarketRequestDTO.getChannel())
                    .build()
            );

            // 获取拼团组队信息
            Long activityId = trailResultEntity.getGroupBuyActivityDiscountVO().getActivityId();
            String userId = goodsMarketRequestDTO.getUserId();
            log.info("正在获取拼团组队信息, activityId: {}, userId: {}", activityId, userId);
            List<UserTeamInfoEntity> userTeamInfoEntityList = indexGroupBuyMarketService.queryUserTeamInfoList(activityId, userId, 1, 2);
            List<GoodsMarketResponseDTO.Team> teamList = userTeamInfoEntityList.stream()
                    .map(userTeamInfoEntity -> {
                        GoodsMarketResponseDTO.Team teamInfo = GoodsMarketResponseDTO.Team.builder()
                                .userId(userTeamInfoEntity.getUserId())
                                .teamId(userTeamInfoEntity.getTeamId())
                                .activityId(userTeamInfoEntity.getActivityId())
                                .targetCount(userTeamInfoEntity.getTargetCount())
                                .completeCount(userTeamInfoEntity.getCompleteCount())
                                .lockCount(userTeamInfoEntity.getLockCount())
                                .validStartTime(userTeamInfoEntity.getValidStartTime())
                                .validEndTime(userTeamInfoEntity.getValidEndTime())
                                .outerOrderId(userTeamInfoEntity.getOuterOrderId())
                                .build();
                        String timeCountDown = GoodsMarketResponseDTO.Team.differenceDateTime2Str(teamInfo.getValidStartTime(), teamInfo.getValidEndTime());
                        teamInfo.setValidTimeCountDown(timeCountDown);
                        return teamInfo;
                    })
                    .toList();

            // 统计拼团统计信息
            log.info("正在统计拼团信息, activityId: {}", activityId);
            TeamStatisticVO teamStatisticVO = indexGroupBuyMarketService.queryTeamStatistic(activityId);

            // 组装返回数据
            GoodsMarketResponseDTO responseDTO = GoodsMarketResponseDTO.builder()
                    .goods(
                            GoodsMarketResponseDTO.Goods.builder()
                                    .goodsId(trailResultEntity.getGoodsId())
                                    .originalPrice(trailResultEntity.getOriginalPrice())
                                    .discountPrice(trailResultEntity.getDeductionPrice())
                                    .payPrice(trailResultEntity.getPayPrice())
                                    .build()
                    )
                    .teamList(teamList)
                    .teamStatistic(
                            GoodsMarketResponseDTO.TeamStatistic.builder()
                                    .totalTeamCount(teamStatisticVO.getTotalTeamCount())
                                    .totalCompleteTeamCount(teamStatisticVO.getTotalCompleteTeamCount())
                                    .totalTeamUserCount(teamStatisticVO.getTotalTeamUserCount())
                                    .build()
                    )
                    .build();
            if (!trailResultEntity.getIsVisible()) {
                // 活动不可见则直接返回原价
                BigDecimal originalPrice = responseDTO.getGoods().getOriginalPrice();
                responseDTO.getGoods().setDiscountPrice(new BigDecimal("0.00"));
                responseDTO.getGoods().setPayPrice(originalPrice);
            }
            log.info("拼团配置查询完成!");
            return Result.success(responseDTO);
        } catch (Exception e) {
            log.error("查询拼团配置失败! userId: {}, goodsId: {}", goodsMarketRequestDTO.getUserId(), goodsMarketRequestDTO.getGoodsId(), e);
            return Result.error("查询拼团配置失败!");
        }
    }
}
