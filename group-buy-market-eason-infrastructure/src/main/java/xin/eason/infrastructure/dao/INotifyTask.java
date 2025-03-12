package xin.eason.infrastructure.dao;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import xin.eason.infrastructure.dao.po.NotifyTaskPO;

@Mapper
public interface INotifyTask extends BaseMapper<NotifyTaskPO> {
    /**
     * 将回调明细状态修改为 成功 并将回调次数 +1
     * @param wrapper 条件过滤器
     * @return 受修改行数
     */
    int updateNotifyStatusSuccess(@Param("ew") LambdaQueryWrapper<NotifyTaskPO> wrapper);

    /**
     * 将回调明细状态修改为 失败 并将回调次数 +1
     * @param wrapper 条件过滤器
     * @return 受修改行数
     */
    int updateNotifyStatusError(@Param("ew") LambdaQueryWrapper<NotifyTaskPO> wrapper);

    /**
     * 将回调明细状态修改为 重试 并将回调次数 +1
     * @param wrapper 条件过滤器
     * @return 受修改行数
     */
    int updateNotifyStatusRetry(@Param("ew") LambdaQueryWrapper<NotifyTaskPO> wrapper);
}
