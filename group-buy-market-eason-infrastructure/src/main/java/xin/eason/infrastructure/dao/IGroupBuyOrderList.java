package xin.eason.infrastructure.dao;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import xin.eason.infrastructure.dao.po.GroupBuyOrderListPO;

import java.util.List;

/**
 * 拼团订单明细表对应 Mapper
 */
@Mapper
public interface IGroupBuyOrderList extends BaseMapper<GroupBuyOrderListPO> {
    /**
     * 根据 activityId 查询所有参加这个活动的用户数量 (去重)
     * @param totalTeamUserCountWrapper where语句生成器
     * @return 用户数量
     */
    Long queryTotalTeamUserCount(@Param("ew") LambdaQueryWrapper<GroupBuyOrderListPO> totalTeamUserCountWrapper);

    /**
     * 根据 activityId, userId, count 随机查询该用户没参与的队伍
     * @param activityId 活动 ID
     * @param userId 用户 ID
     * @param count 查询数量
     * @return 订单明细列表
     */
    List<GroupBuyOrderListPO> queryUserRamdomTeamInfoList(Long activityId, String userId, int count);
}
