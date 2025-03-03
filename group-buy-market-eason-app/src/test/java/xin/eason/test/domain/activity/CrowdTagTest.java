package xin.eason.test.domain.activity;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.redisson.api.RBitSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import xin.eason.domain.tags.service.ITagsService;
import xin.eason.infrastructure.redis.IRedisService;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class CrowdTagTest {

    @Autowired
    private ITagsService tagsService;
    @Autowired
    private IRedisService redisService;

    @Test
    public void test_tag_job() {
        tagsService.execTagBatchJob("RQ_KJHKL98UU78H66554GFDV", "10001");
    }

    @Test
    public void test_get_tag_bitmap() {
        RBitSet bitSet = redisService.getBitSet("RQ_KJHKL98UU78H66554GFDV");
        // 是否存在
        log.info("xiaofuge 存在，预期结果为 true，测试结果:{}", bitSet.get(redisService.getIndexFromUserId("xiaofuge")));
        log.info("gudebai 不存在，预期结果为 false，测试结果:{}", bitSet.get(redisService.getIndexFromUserId("gudebai")));
    }
}
