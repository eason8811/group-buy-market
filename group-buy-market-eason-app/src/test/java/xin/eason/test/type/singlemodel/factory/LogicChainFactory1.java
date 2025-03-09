package xin.eason.test.type.singlemodel.factory;

import lombok.*;
import org.springframework.stereotype.Component;
import xin.eason.test.type.singlemodel.node.Node101;
import xin.eason.test.type.singlemodel.node.Node102;
import xin.eason.test.type.singlemodel.node.Node103;
import xin.eason.types.design.framework.link.singlemodel.ILogicLinkChainNode;

@Component
@RequiredArgsConstructor
public class LogicChainFactory1 {

    public ILogicLinkChainNode<String, String, DynamicContext> creatLogicChain1() {
        ILogicLinkChainNode<String, String, DynamicContext> node101 = new Node101();
        ILogicLinkChainNode<String, String, DynamicContext> node102 = new Node102();
        node101.appendNext(node102);
        return node101;
    }

    public ILogicLinkChainNode<String, String, DynamicContext> creatLogicChain2() {
        ILogicLinkChainNode<String, String, DynamicContext> node101 = new Node101();
        ILogicLinkChainNode<String, String, DynamicContext> node103 = new Node103();
        node101.appendNext(node103);
        return node101;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DynamicContext {
        private String name;
    }


}
