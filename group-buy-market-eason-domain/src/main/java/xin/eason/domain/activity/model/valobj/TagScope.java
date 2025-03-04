package xin.eason.domain.activity.model.valobj;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum TagScope {
    
    VISIBLE(1, "限制可见"),
    PARTICIPABLE(2, "限制参与"),
    VISABLE_PARTICIPABLE(3, "限制可见, 限制参与");

    @EnumValue
    private final Integer code;
    @JsonValue
    private final String desc;
    
    TagScope(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
