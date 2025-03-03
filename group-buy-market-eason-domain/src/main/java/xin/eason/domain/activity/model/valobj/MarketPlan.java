package xin.eason.domain.activity.model.valobj;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum MarketPlan {
    ZJ("ZJ", "直减"),
    MJ("MJ", "满减"),
    ZK("ZK", "折扣"),
    N("N", "N元购");

    @EnumValue
    private final String code;
    @JsonValue
    private final String desc;

    MarketPlan(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
