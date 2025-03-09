package xin.eason.test.type;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import xin.eason.test.type.singlemodel.factory.LogicChainFactory1;
import xin.eason.types.design.framework.link.singlemodel.ILogicLinkChainNode;

@SpringBootTest
@Slf4j
@RunWith(SpringRunner.class)
public class SingleModelTest {

    @Autowired
    private LogicChainFactory1 logicChainFactory1;

    @Test
    public void testSingleModelLogicChain() {
        ILogicLinkChainNode<String, String, LogicChainFactory1.DynamicContext> chain1 = logicChainFactory1.creatLogicChain1();
        String result1 = chain1.apply("", new LogicChainFactory1.DynamicContext());
        log.info(result1);

        ILogicLinkChainNode<String, String, LogicChainFactory1.DynamicContext> chain2 = logicChainFactory1.creatLogicChain2();
        String result2 = chain2.apply("", new LogicChainFactory1.DynamicContext());
        log.info(result2);
        log.info(chain1.apply("", new LogicChainFactory1.DynamicContext()));
    }
}
