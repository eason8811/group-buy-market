package xin.eason.domain.trade.model.valobj;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * 拼团订单明细状态
 */
@Getter
public enum OrderListStatus {
    INIT_LOCK(0, "初始锁定"),
    PAY_COMPLETE(1, "消费完成"),
    CLOSE(2, "超时关单");


    @EnumValue
    private final Integer code;
    @JsonValue
    private final String desc;

    OrderListStatus(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
