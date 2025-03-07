package xin.eason.infrastructure.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import xin.eason.infrastructure.dao.po.GroupBuyOrderListPO;

/**
 * 拼团订单明细表对应 Mapper
 */
@Mapper
public interface IGroupBuyOrderList extends BaseMapper<GroupBuyOrderListPO> {
}
