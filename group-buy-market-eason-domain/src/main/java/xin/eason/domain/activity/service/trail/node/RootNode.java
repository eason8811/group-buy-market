package xin.eason.domain.activity.service.trail.node;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import xin.eason.domain.activity.model.entity.MarketProductEntity;
import xin.eason.domain.activity.model.entity.TrailResultEntity;
import xin.eason.domain.activity.service.trail.AbstractGroupBuyMarketSupport;
import xin.eason.domain.activity.service.trail.factory.DefaultActivityStrategyFactory;
import xin.eason.types.design.framework.tree.StrategyHandler;
import xin.eason.types.exception.ParamInvalidException;

/**
 * <p>规则树起点节点</p>
 * <p>主要做参数的合法性校验</p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RootNode extends AbstractGroupBuyMarketSupport {

    /**
     * 开关节点, 起点节点的下一节点
     */
    private final SwitchNode switchNode;
    /**
     * 处理错误的节点
     */
    private final ErrorNode errorNode;

    /**
     * 处理根节点逻辑, 校验<b>营销产品实体类对象</b>中的
     * <p><b>userId, goodsId, source, channel</b> 属性是否为空</p>
     * <p>如果为空, 则抛出错误</p>
     *
     * @param requestParam   入参
     * @param dynamicContext 动态上下文
     * @return 出参
     */
    @Override
    protected TrailResultEntity doApply(MarketProductEntity requestParam, DefaultActivityStrategyFactory.DynamicContext dynamicContext) throws Exception {
        log.info("拼团商品优惠试算规则树 -> {}, userId: {}, requestParam: {}", this.getClass().getSimpleName(), requestParam.getUserId(), requestParam);
        if (StringUtils.isBlank(requestParam.getUserId()) || StringUtils.isBlank(requestParam.getGoodsId()) || StringUtils.isBlank(requestParam.getSource()) || StringUtils.isBlank(requestParam.getChannel())) {
            // 将异常存入动态上下文中
            dynamicContext.setException(new ParamInvalidException("MarketProductEntity 营销产品类对象参数不合法!"));
            return router(requestParam, dynamicContext);
        }
        return router(requestParam, dynamicContext);
    }

    /**
     * 获取下一节点的策略处理器 {@link StrategyHandler}
     *
     * @param requestParam   入参
     * @param dynamicContext 动态上下文
     * @return 下一节点的 {@link SwitchNode} 节点对象
     * @throws Exception 抛出所有错误
     */
    @Override
    public StrategyHandler<MarketProductEntity, DefaultActivityStrategyFactory.DynamicContext, TrailResultEntity> get(MarketProductEntity requestParam, DefaultActivityStrategyFactory.DynamicContext dynamicContext) throws Exception {
        if (dynamicContext.getException() != null)
            return errorNode;
        return switchNode;
    }
}
