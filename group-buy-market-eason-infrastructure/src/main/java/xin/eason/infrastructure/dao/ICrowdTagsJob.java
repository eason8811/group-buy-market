package xin.eason.infrastructure.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import xin.eason.infrastructure.dao.po.CrowdTagsJobPO;

@Mapper
public interface ICrowdTagsJob extends BaseMapper<CrowdTagsJobPO> {
}
