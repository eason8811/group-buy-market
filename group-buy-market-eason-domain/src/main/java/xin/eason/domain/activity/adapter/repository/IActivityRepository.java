package xin.eason.domain.activity.adapter.repository;

import xin.eason.domain.activity.model.entity.UserTeamInfoEntity;
import xin.eason.domain.activity.model.valobj.GroupBuyActivityDiscountVO;
import xin.eason.domain.activity.model.valobj.SkuVO;
import xin.eason.domain.activity.model.valobj.TeamStatisticVO;

import java.util.List;

/**
 * 活动 repository 仓储适配器接口
 */
public interface IActivityRepository {

    /**
     * 根据 <b>SC</b> 获取 {@link GroupBuyActivityDiscountVO} 拼团活动及其折扣类的对象
     * @param source 来源
     * @param channel 渠道
     * @param goodsId 拼团商品 ID
     * @return 拼团活动及其折扣类的对象
     */
    GroupBuyActivityDiscountVO queryGroupBuyActivityDiscountVO(String source, String channel, String goodsId);

    /**
     * 根据 ID 获取 {@link SkuVO} 商品信息值对象
     * @param goodsId 商品 ID
     * @return {@link SkuVO} 商品信息值对象
     */
    SkuVO querySkuVO(String goodsId);

    /**
     * 根据 tagId 和 userId 在 redis 的位图中判断用户是否在人群范围内
     * @param tagId 人群标签 ID
     * @param userId 用户 ID
     * @return 是否在人群标签内
     */
    Boolean queryUserInCrowd(String tagId, String userId);

    /**
     * 判断服务是否降级
     * @return 服务降级情况
     */
    boolean downGrade();

    /**
     * 判断服务对该用户是否切量
     * @param userId 用户 ID
     * @return 服务切量情况
     */
    boolean cutRange(String userId);

    /**
     * 查询用户参与的拼团队伍列表
     *
     * @param activityId 活动 ID
     * @param userId     用户 ID (需要查询用户参与的拼团队伍)
     * @param ownerCount 需要查询的队伍数量
     * @return 拼团队伍信息列表
     */
    List<UserTeamInfoEntity> queryUserOwnerTeamInfoList(Long activityId, String userId, Integer ownerCount);

    /**
     * 随机查询用户没有参与的拼团队伍列表
     * @param activityId 活动 ID
     * @param userId 用户 ID
     * @param randomCount 随机查询的数量
     * @return 拼团队伍信息列表
     */
    List<UserTeamInfoEntity> queryUserRamdomTeamInfoList(Long activityId, String userId, Integer randomCount);

    /**
     * 统计指定 activityId 活动内的拼团队伍数据
     * @param activityId 活动 ID
     * @return 拼团队伍数据值对象
     */
    TeamStatisticVO queryTeamStatistic(Long activityId);
}
