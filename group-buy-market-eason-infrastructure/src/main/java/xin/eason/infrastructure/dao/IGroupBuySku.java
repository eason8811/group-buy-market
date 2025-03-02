package xin.eason.infrastructure.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import xin.eason.infrastructure.dao.po.SkuPO;

/**
 * 拼团商品表对应 Mapper
 */
@Mapper
public interface IGroupBuySku extends BaseMapper<SkuPO> {
}
