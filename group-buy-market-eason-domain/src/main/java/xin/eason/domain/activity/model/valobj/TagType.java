package xin.eason.domain.activity.model.valobj;

import lombok.Getter;

@Getter
public enum TagType {
    TAKE_PART_COUNT("参与量"),
    BUY_AMOUNT("购买量");

    private String type;

    TagType(String type) {
        this.type = type;
    }
}
