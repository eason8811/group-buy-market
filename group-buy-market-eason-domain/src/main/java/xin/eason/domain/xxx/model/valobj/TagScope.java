package xin.eason.domain.xxx.model.valobj;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum TagScope {
    
    VISIBLE(1, "可见限制"),
    PARTICIPABLE(2, "参与限制");

    @EnumValue
    private final Integer code;
    @JsonValue
    private final String desc;
    
    TagScope(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
