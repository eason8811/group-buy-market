package xin.eason.test.type.singlemodel.node;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import xin.eason.test.type.singlemodel.factory.LogicChainFactory1;
import xin.eason.types.design.framework.link.singlemodel.AbstractLogicLinkChainNode;

@Component
@Slf4j
public class Node101 extends AbstractLogicLinkChainNode<String, String, LogicChainFactory1.DynamicContext> {
    /**
     * 处理责任链节点的逻辑
     *
     * @param requestParam   入参
     * @param dynamicContext 动态上下文
     * @return 出参
     */
    @Override
    public String apply(String requestParam, LogicChainFactory1.DynamicContext dynamicContext) {
        log.info("正处于节点 1");
        return routeNextNode(requestParam, dynamicContext);
    }
}
