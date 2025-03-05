package xin.eason.infrastructure.dcc;

import org.springframework.stereotype.Service;
import xin.eason.types.annotations.DCCValue;

/**
 * 动态配置管理服务
 */
@Service
public class DCCService {
    /**
     * 服务降级开关
     */
    @DCCValue("downgrade:true")
    private Boolean downGrade;
    /**
     * 服务切量开关
     */
    @DCCValue("cutrange:100")
    private Integer cutRange;

    /**
     * 判断服务是否降级
     * @return 服务降级情况
     */
    public Boolean isDownGrade() {
        return downGrade;
    }

    /**
     * 根据 userId 判断是否需要切量
     * @param userId 用户 ID
     * @return 用户切量情况
     */
    public Boolean isCutRange(String userId) {
        // 取用户 ID 的 hashCode 的最后两位
        int userIdHash = Math.abs(userId.hashCode());
        int lastTwoDigits = userIdHash % 100;
        // 如果在切量范围内, 返回 true 否则 false
        return lastTwoDigits <= cutRange;
    }
}
