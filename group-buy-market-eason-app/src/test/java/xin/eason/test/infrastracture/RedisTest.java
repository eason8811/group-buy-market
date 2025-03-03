package xin.eason.test.infrastracture;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import xin.eason.domain.activity.model.valobj.GroupBuyActivityDiscountVO;
import xin.eason.domain.activity.model.valobj.MarketPlan;

@Slf4j
@SpringBootTest
public class RedisTest {


    @Test
    public void testRedis() {


    }
}
