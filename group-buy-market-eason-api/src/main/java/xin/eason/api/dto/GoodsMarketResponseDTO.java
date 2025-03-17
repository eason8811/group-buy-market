package xin.eason.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 拼团首页展示响应数据传输类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoodsMarketResponseDTO {
    /**
     * 活动 ID
     */
    private Long activityId;
    /**
     * 商品信息
     */
    private Goods goods;
    /**
     * 队伍信息列表
     */
    private List<Team> teamList;
    /**
     * 拼团统计
     */
    private TeamStatistic teamStatistic;

    /**
     * 商品信息类
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Goods {
        /**
         * 商品 ID
         */
        private String goodsId;
        /**
         * 商品原价
         */
        private BigDecimal originalPrice;
        /**
         * 商品折扣金额
         */
        private BigDecimal discountPrice;
        /**
         * 商品支付价格 = 商品原价 - 商品折扣金额
         */
        private BigDecimal payPrice;
    }

    /**
     * 队伍信息类
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Team {
        /**
         * 拼团队伍对账的 用户ID
         */
        private String userId;
        /**
         * 拼团队伍 ID
         */
        private String teamId;
        /**
         * 拼团队伍所参加的活动 ID
         */
        private Long activityId;
        /**
         * 拼团目标人数
         */
        private Integer targetCount;
        /**
         * 拼团完成人数
         */
        private Integer completeCount;
        /**
         * 拼团锁单人数
         */
        private Integer lockCount;
        /**
         * 拼团队伍开始拼团时间 (有效开始时间)
         */
        private LocalDateTime validStartTime;
        /**
         * 拼团队伍拼团结束时间 (有效结束时间 = 有效开始时间 + 活动有效时长)
         */
        private LocalDateTime validEndTime;
        /**
         * 倒计时 (字符串)
         */
        private String validTimeCountDown;
        /**
         * 外部订单 ID
         */
        private String outerOrderId;

        /**
         * 将有效时间转换为字符串
         * @param validStartTime 有效开始时间
         * @param validEndTime 有效结束时间
         * @return 字符串形式的倒计时
         */
        public static String differenceDateTime2Str(LocalDateTime validStartTime, LocalDateTime validEndTime) {
            if (validStartTime == null || validEndTime == null) {
                return "无效的时间";
            }

            long diffInMilliseconds = (validEndTime.toEpochSecond(OffsetDateTime.now().getOffset()) - LocalDateTime.now().toEpochSecond(OffsetDateTime.now().getOffset())) * 1000;

            if (diffInMilliseconds < 0) {
                return "已结束";
            }

            long seconds = TimeUnit.MILLISECONDS.toSeconds(diffInMilliseconds) % 60;
            long minutes = TimeUnit.MILLISECONDS.toMinutes(diffInMilliseconds) % 60;
            long hours = TimeUnit.MILLISECONDS.toHours(diffInMilliseconds) % 24;
            long days = TimeUnit.MILLISECONDS.toDays(diffInMilliseconds);

            return String.format("%02d:%02d:%02d", hours, minutes, seconds);
        }
    }

    /**
     * 拼团统计类
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TeamStatistic {
        /**
         * 开团队伍总数
         */
        private Long totalTeamCount;
        /**
         * 完成拼团的队伍总数
         */
        private Long totalCompleteTeamCount;
        /**
         * 参与拼团的用户总数
         */
        private Long totalTeamUserCount;
    }
}
