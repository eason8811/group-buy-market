package xin.eason.domain.trade.adapter.port;

import xin.eason.domain.trade.model.entity.NotifyTaskEntity;

/**
 * Trade 领域接口
 */
public interface ITradePort {
    /**
     * 根据 notifyTaskEntity 执行回调
     * @param notifyTaskEntity 回调任务实体
     * @return 回调的响应
     */
    String groupBuyNotify(NotifyTaskEntity notifyTaskEntity);
}
