package xin.eason.domain.trade.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import xin.eason.domain.trade.model.valobj.GroupBuyProgressVO;
import xin.eason.domain.trade.model.valobj.OrderStatus;

/**
 * 拼团队伍实体
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PayOrderTeamEntity {
    /**
     * 拼团队伍 ID
     */
    private String teamId;
    /**
     * 队伍拼团状态
     */
    private OrderStatus orderStatus;
    /**
     * 拼团进度
     */
    private GroupBuyProgressVO teamProgress;
}
