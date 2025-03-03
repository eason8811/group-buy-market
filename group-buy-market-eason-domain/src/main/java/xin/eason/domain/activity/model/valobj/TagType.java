package xin.eason.domain.activity.model.valobj;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum TagType {
    TAKE_PART_COUNT("参与量"),
    BUY_AMOUNT("购买量");

    @JsonValue
    @EnumValue
    private final String type;

    TagType(String type) {
        this.type = type;
    }
}
