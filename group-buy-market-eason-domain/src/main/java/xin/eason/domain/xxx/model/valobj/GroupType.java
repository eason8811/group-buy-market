package xin.eason.domain.xxx.model.valobj;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * 拼团方式
 */
@Getter
public enum GroupType {
    AUTO(0, "自动成团"),
    GOAL(1, "达成目标成团");

    @EnumValue
    private final Integer code;
    @JsonValue
    private final String desc;

    GroupType(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
