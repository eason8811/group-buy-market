package xin.eason.infrastructure.dao;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import xin.eason.infrastructure.dao.po.GroupBuyOrderPO;

/**
 * 拼团订单表对应 Mapper
 */
@Mapper
public interface IGroupBuyOrder extends BaseMapper<GroupBuyOrderPO> {
    /**
     * 根据 teamId 修改拼团订单的 complete_count, 使其增加 1
     * @param orderUpdateWrapper 外部构造的 wrapper 条件
     */
    int updateOrderCompleteCountByTeamId(@Param("ew") LambdaUpdateWrapper<GroupBuyOrderPO> orderUpdateWrapper);
}
