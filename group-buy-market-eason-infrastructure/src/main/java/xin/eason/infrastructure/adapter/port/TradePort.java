package xin.eason.infrastructure.adapter.port;

import lombok.RequiredArgsConstructor;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import org.redisson.api.RLock;
import org.springframework.stereotype.Component;
import retrofit2.Response;
import xin.eason.domain.trade.adapter.port.ITradePort;
import xin.eason.domain.trade.model.entity.NotifyTaskEntity;
import xin.eason.infrastructure.gateway.IGroupBuyTradeNotify;
import xin.eason.infrastructure.redis.RedissonService;

import java.util.concurrent.TimeUnit;

/**
 * Trade 领域接口实现类
 */
@Component
@RequiredArgsConstructor
public class TradePort implements ITradePort {
    /**
     * redisson 服务
     */
    private final RedissonService redissonService;
    /**
     * 拼团交易回调接口
     */
    private final IGroupBuyTradeNotify groupBuyTradeNotify;

    /**
     * 根据 notifyTaskEntity 执行回调
     *
     * @param notifyTaskEntity 回调任务实体
     * @return 回调的响应
     */
    @Override
    public String groupBuyNotify(NotifyTaskEntity notifyTaskEntity) {
        // 使用 redissonService 获取可重入锁
        RLock lock = redissonService.getLock(notifyTaskEntity.lockKey());
        try {
            // 尝试锁
            if (lock.tryLock(3, 0, TimeUnit.SECONDS)) {
                // 锁定成功
                try {
                    String notifyUrl = notifyTaskEntity.getNotifyUrl();
                    if (notifyUrl.isBlank() || notifyUrl.contains("暂无"))
                        return "success";

                    MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                    RequestBody jsonParam = RequestBody.create(JSON, notifyTaskEntity.getParameterJson());
                    Response<String> response = groupBuyTradeNotify.notify(notifyUrl, jsonParam).execute();
                    return response.body();

                } finally {
                    if (lock.isLocked() && lock.isHeldByCurrentThread())
                        // 如果锁被上锁, 并且是被当前线程上锁的, 就解锁
                        lock.unlock();
                }
            }
            // 锁定不成功, 直接返回 null
            return null;
        } catch (Exception e) {
            // 出错, 中断线程
            Thread.currentThread().interrupt();
            return null;
        }
    }
}
