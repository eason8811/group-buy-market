package xin.eason.domain.xxx.model.valobj;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum ActivityStatus {
    CREATE(0, "创建"),
    VALIDATE(1, "生效"),
    EXPIRED(2, "过期"),
    USELESS(3, "废弃");

    @EnumValue
    private final Integer code;
    @JsonValue
    private final String desc;

    ActivityStatus(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
