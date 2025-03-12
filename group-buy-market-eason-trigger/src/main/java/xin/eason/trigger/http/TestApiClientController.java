package xin.eason.trigger.http;

import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import xin.eason.api.dto.NotifyRequestDTO;

@Slf4j
@RestController
@CrossOrigin("*")
@RequestMapping("/api/v1/test")
public class TestApiClientController {

    /**
     * 模拟回调案例
     *
     * @param notifyRequestDTO 通知回调参数
     * @return success 成功，error 失败
     */
    @PostMapping("/group_buy_notify")
    public String groupBuyNotify(@RequestBody NotifyRequestDTO notifyRequestDTO) {
        log.info("模拟测试第三方服务接收拼团回调 response: {}", JSON.toJSONString(notifyRequestDTO));
        return "success";
    }

}