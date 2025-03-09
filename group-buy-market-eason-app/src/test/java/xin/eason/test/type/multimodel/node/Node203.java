package xin.eason.test.type.multimodel.node;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import xin.eason.test.type.multimodel.factory.LogicLinkChainFactory;
import xin.eason.types.design.framework.link.multimodel.handler.ILogicChainNodeHandler;

@Slf4j
@Component
public class Node203 implements ILogicChainNodeHandler<String, String, LogicLinkChainFactory.DynamicContext> {
    /**
     * 处理节点内的逻辑
     *
     * @param requestParam   入参
     * @param dynamicContext 动态上下文
     * @return 出参
     */
    @Override
    public String apply(String requestParam, LogicLinkChainFactory.DynamicContext dynamicContext) {
        log.info("正处于节点 3");
        return "节点 3 结束";
    }
}
