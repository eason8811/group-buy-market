package xin.eason.infrastructure.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import xin.eason.infrastructure.dao.po.GroupBuyDiscountPO;

/**
 * 拼团折扣表对应 Mapper
 */
@Mapper
public interface IGroupBuyDiscount extends BaseMapper<GroupBuyDiscountPO> {
}
