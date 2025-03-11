package xin.eason.test.type.multimodel.factory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import xin.eason.test.type.multimodel.node.Node201;
import xin.eason.test.type.multimodel.node.Node202;
import xin.eason.test.type.multimodel.node.Node203;
import xin.eason.types.design.framework.link.multimodel.LinkListConstructor;
import xin.eason.types.design.framework.link.multimodel.chain.BusinessLinkList;

@Configuration
public class LogicLinkChainFactory {

    @Bean("demo1")
    public BusinessLinkList<String, String, DynamicContext> createDemo1Chain(Node201 node201, Node202 node202) {
        LinkListConstructor<String, String, DynamicContext> linkListConstructor = new LinkListConstructor<>(node201, node202);
        return linkListConstructor.getLogicLinkChain();
    }

    @Bean("demo2")
    public BusinessLinkList<String, String, DynamicContext> createDemo2Chain(Node201 node201, Node203 node203) {
        LinkListConstructor<String, String, DynamicContext> linkListConstructor = new LinkListConstructor<>(node201, node203);
        return linkListConstructor.getLogicLinkChain();
    }

    /**
     * 动态上下文
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DynamicContext {
        private String string;
    }
}
