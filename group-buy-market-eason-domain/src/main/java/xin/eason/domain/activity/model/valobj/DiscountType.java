package xin.eason.domain.activity.model.valobj;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum DiscountType {
    BASE(0, "base"),
    TAG(1, "tag");

    @EnumValue
    private final Integer code;
    @JsonValue
    private final String desc;

    DiscountType(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
