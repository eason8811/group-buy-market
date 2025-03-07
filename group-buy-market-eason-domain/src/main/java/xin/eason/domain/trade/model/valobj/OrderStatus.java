package xin.eason.domain.trade.model.valobj;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * 拼团订单状态
 */
@Getter
public enum OrderStatus {
    GROUPING(0, "拼单中"),
    COMPLETE(1, "已完成"),
    FAIL(2, "失败");

    @EnumValue
    private final Integer code;
    @JsonValue
    private final String desc;

    OrderStatus(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
