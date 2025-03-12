package xin.eason.domain.activity.service;

import xin.eason.domain.activity.model.entity.MarketProductEntity;
import xin.eason.domain.activity.model.entity.TrailResultEntity;
import xin.eason.domain.activity.model.entity.UserTeamInfoEntity;
import xin.eason.domain.activity.model.valobj.TeamStatisticVO;

import java.util.List;

/**
 * 拼团首页活动领域服务接口
 */
public interface IIndexGroupBuyMarketService {

    /**
     * 进行首页优惠产品试算
     * @param marketProductEntity 营销产品实体类对象
     * @return 试算结果实体类对象
     */
    TrailResultEntity indexTrail(MarketProductEntity marketProductEntity);

    /**
     * 根据 activityId, userId, ownerCount, randomCount 查询指定用户参与的或者不是该用户参与的拼团队伍信息列表
     *
     * @param activityId  活动 ID
     * @param userId      用户 ID
     * @param ownerCount  需要查询的以该用户参与的拼团队伍数量
     * @param randomCount 需要查询的不以该用户参与的随机拼团队伍数量
     * @return 拼团队伍信息实体列表
     */
    List<UserTeamInfoEntity> queryUserTeamInfoList(Long activityId, String userId, Integer ownerCount, Integer randomCount);

    /**
     * 统计指定 activityId 活动内的拼团队伍数据
     * @param activityId 活动 ID
     * @return 拼团队伍数据值对象
     */
    TeamStatisticVO queryTeamStatistic(Long activityId);
}
