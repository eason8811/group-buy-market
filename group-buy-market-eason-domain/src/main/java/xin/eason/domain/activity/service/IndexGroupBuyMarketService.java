package xin.eason.domain.activity.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import xin.eason.domain.activity.adapter.repository.IActivityRepository;
import xin.eason.domain.activity.model.entity.MarketProductEntity;
import xin.eason.domain.activity.model.entity.TrailResultEntity;
import xin.eason.domain.activity.model.entity.UserTeamInfoEntity;
import xin.eason.domain.activity.model.valobj.TeamStatisticVO;
import xin.eason.domain.activity.service.trail.factory.DefaultActivityStrategyFactory;
import xin.eason.types.design.framework.tree.StrategyHandler;
import xin.eason.types.exception.ParamInvalidException;

import java.util.ArrayList;
import java.util.List;

/**
 * 拼团首页活动领域服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class IndexGroupBuyMarketService implements IIndexGroupBuyMarketService {

    /**
     * @see DefaultActivityStrategyFactory
     */
    private final DefaultActivityStrategyFactory defaultActivityStrategyFactory;
    /**
     * activit 领域适配器的仓储服务
     */
    private final IActivityRepository activityRepository;

    /**
     * <p>进行首页优惠产品试算</p>
     * <p>出现异常则返回空的 {@link TrailResultEntity} 对象</p>
     *
     * @param marketProductEntity 营销产品实体类对象
     * @return 试算结果实体类对象
     */
    @Override
    public TrailResultEntity indexTrail(MarketProductEntity marketProductEntity) {
        StrategyHandler<MarketProductEntity, DefaultActivityStrategyFactory.DynamicContext, TrailResultEntity> strategyHandler = defaultActivityStrategyFactory.createStrategyHandler();
        try {
            return strategyHandler.apply(marketProductEntity, new DefaultActivityStrategyFactory.DynamicContext());
        } catch (ParamInvalidException e) {
            log.error("{}", e.getMessage(), e);
            return new TrailResultEntity();
        } catch (Exception e) {
            log.error("优惠试算过程错误! ", e);
            return new TrailResultEntity();
        }
    }

    /**
     * 根据 activityId, userId, ownerCount, randomCount 查询指定用户参与的或者不是该用户参与的拼团队伍信息列表
     *
     * @param activityId  活动 ID
     * @param userId      用户 ID
     * @param ownerCount  需要查询的以该用户参与的拼团队伍数量
     * @param randomCount 需要查询的不以该用户参与的随机拼团队伍数量
     * @return 拼团队伍信息实体列表
     */
    @Override
    public List<UserTeamInfoEntity> queryUserTeamInfoList(Long activityId, String userId, Integer ownerCount, Integer randomCount) {
        // 创建用于存储队伍信息的列表
        List<UserTeamInfoEntity> unionList = new ArrayList<>();

        // 查询本用户参加的拼团队伍
        if (ownerCount != 0) {
            List<UserTeamInfoEntity> userTeamInfoEntityList = activityRepository.queryUserOwnerTeamInfoList(activityId, userId, ownerCount);
            unionList.addAll(userTeamInfoEntityList);
        }

        // 查询非本用户参加的拼团队伍
        if (randomCount != 0) {
            List<UserTeamInfoEntity> userTeamInfoEntityList = activityRepository.queryUserRamdomTeamInfoList(activityId, userId, randomCount);
            unionList.addAll(userTeamInfoEntityList);
        }

        return unionList;
    }

    /**
     * 统计指定 activityId 活动内的拼团队伍数据
     *
     * @param activityId 活动 ID
     * @return 拼团队伍数据值对象
     */
    @Override
    public TeamStatisticVO queryTeamStatistic(Long activityId) {
        return activityRepository.queryTeamStatistic(activityId);
    }
}
