package xin.eason.infrastructure.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import xin.eason.infrastructure.dao.po.CrowdTagsPO;

@Mapper
public interface ICrowdTags extends BaseMapper<CrowdTagsPO> {
}
