package xin.eason.infrastructure.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import xin.eason.infrastructure.dao.po.GroupBuyActivityPO;

@Mapper
public interface IGroupBuyActivity extends BaseMapper<GroupBuyActivityPO> {
}
