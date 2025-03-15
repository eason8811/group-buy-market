package xin.eason.trigger.job;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import xin.eason.domain.trade.service.ITradeSettlementOrderService;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 检查队伍是否在有效时间内, 如果队伍不在有效时间内且拼团未成功, 则将其状态修改为失败
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CheckTeamInValidTime {
    /**
     * trade 领域订单结算服务
     */
    private final ITradeSettlementOrderService settlementOrderService;

    /**
     * 每隔 30 秒检查是否有队伍的有效时间已经不在当前时间内, 如有, 且其状态不为成功, 则修改为失败
     */
    @Scheduled(cron = "0 * * * * ? ")
    public void setInvalidTeamToFailed() {
        try {
            LocalDateTime currentTime = LocalDateTime.now();
            log.info("定时任务 (每 30 秒钟) 当前时间为: {} 检测是否有过期且状态不为 成功 的队伍...", currentTime);
            List<String> teamIdList = settlementOrderService.setInvalidTeamToFailed(currentTime);
            if (!teamIdList.isEmpty())
                log.info("定时任务完成! 已将 {} 中的队伍状态更改为 失败!", teamIdList);
            else
                log.info("暂无需要将状态修改为 失败 的队伍!");

        } catch (Exception e) {
            log.error("检测队伍是否过期 定时任务出现异常! 异常信息: {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
}
