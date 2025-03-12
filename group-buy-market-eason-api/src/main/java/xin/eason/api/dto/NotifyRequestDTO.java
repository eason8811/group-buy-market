package xin.eason.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotifyRequestDTO {
    /** 组队ID */
    private String teamId;
    /** 外部单号 */
    private List<String> outerOrderId;

}