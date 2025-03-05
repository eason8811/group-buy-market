package xin.eason.trigger.http;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RTopic;
import org.springframework.web.bind.annotation.*;
import xin.eason.api.IDCCService;
import xin.eason.api.response.Result;


/**
 * 动态配置管理触发器
 */
@Slf4j
@RestController()
@CrossOrigin("*")
@RequestMapping("/api/v1/gbm/dcc")
@RequiredArgsConstructor
public class DCCController implements IDCCService {

    private final RTopic dccTopic;

    /**
     * <p>动态更改配置<p/>
     * <p><b><a href="http://127.0.0.1:8080/api/v1/gbm/dcc/update_config?key=downgrade&value=1">key: downGrade, value: 1</a></b><p/>
     * <p><b><a href="http://127.0.0.1:8080/api/v1/gbm/dcc/update_config?key=cutrange&value=0">key: cutRange, value: 0</a></b><p/>
     */
    @RequestMapping(value = "/update_config", method = RequestMethod.GET)
    @Override
    public Result<Boolean> updateConfig(@RequestParam String key, @RequestParam String value) {
        try {
            log.info("DCC 动态配置值变更 key:{} value:{}", key, value);
            dccTopic.publish(key + "," + value);
            return Result.success(true);
        } catch (Exception e) {
            log.error("DCC 动态配置值变更失败 key:{} value:{}", key, value, e);
            return Result.success(true);
        }
    }

}
