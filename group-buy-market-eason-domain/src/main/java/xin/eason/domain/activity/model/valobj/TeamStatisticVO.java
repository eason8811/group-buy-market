package xin.eason.domain.activity.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 统计一个活动内的拼团队伍数据
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeamStatisticVO {
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
