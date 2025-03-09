package xin.eason.test.type;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import xin.eason.test.type.multimodel.factory.LogicLinkChainFactory;
import xin.eason.types.design.framework.link.multimodel.chain.BusinessLinkChain;

@SpringBootTest
@RunWith(SpringRunner.class)
@Slf4j
public class MultiModelTest {

    @Autowired
    @Qualifier("demo1")
    private BusinessLinkChain<String, String, LogicLinkChainFactory.DynamicContext> businessLinkChain1;
    @Autowired
    @Qualifier("demo2")
    private BusinessLinkChain<String, String, LogicLinkChainFactory.DynamicContext> businessLinkChain2;

    @Test
    public void testDemo() {
        String result1 = businessLinkChain1.apply("", new LogicLinkChainFactory.DynamicContext());
        log.info(result1);

        String result2 = businessLinkChain2.apply("", new LogicLinkChainFactory.DynamicContext());
        log.info(result2);

        String result3 = businessLinkChain1.apply("", new LogicLinkChainFactory.DynamicContext());
        log.info(result3);
    }
}
