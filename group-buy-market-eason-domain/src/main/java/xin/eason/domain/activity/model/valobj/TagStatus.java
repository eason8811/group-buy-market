package xin.eason.domain.activity.model.valobj;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum TagStatus {
    INIT(0, "初始"),
    PLAN(1, "计划"),
    RESET(2, "重置"),
    COMPLETE(3, "完成");

    @EnumValue
    private final Integer code;
    @JsonValue
    private final String desc;

    TagStatus(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
