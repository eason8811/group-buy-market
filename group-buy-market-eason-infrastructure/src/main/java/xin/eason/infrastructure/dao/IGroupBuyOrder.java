package xin.eason.infrastructure.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import xin.eason.infrastructure.dao.po.GroupBuyOrderPO;

/**
 * 拼团订单表对应 Mapper
 */
@Mapper
public interface IGroupBuyOrder extends BaseMapper<GroupBuyOrderPO> {
}
