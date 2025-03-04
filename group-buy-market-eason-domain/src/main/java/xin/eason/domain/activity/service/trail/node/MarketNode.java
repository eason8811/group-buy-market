package xin.eason.domain.activity.service.trail.node;

import com.alibaba.fastjson.JSON;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import xin.eason.domain.activity.adapter.repository.IActivityRepository;
import xin.eason.domain.activity.model.entity.MarketProductEntity;
import xin.eason.domain.activity.model.entity.TrailResultEntity;
import xin.eason.domain.activity.model.valobj.GroupBuyActivityDiscountVO;
import xin.eason.domain.activity.model.valobj.SkuVO;
import xin.eason.domain.activity.service.discount.IDiscountCalculateService;
import xin.eason.domain.activity.service.trail.AbstractGroupBuyMarketSupport;
import xin.eason.domain.activity.service.trail.factory.DefaultActivityStrategyFactory;
import xin.eason.domain.activity.service.trail.thread.QueryGroupBuyActivityDiscountVO;
import xin.eason.domain.activity.service.trail.thread.QuerySkuVO;
import xin.eason.types.design.framework.tree.StrategyHandler;
import xin.eason.types.exception.NoMarketConfigException;
import xin.eason.types.exception.ServiceException;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * <p>规则树营销服务节点</p>
 * <p>主要做拼团商品的优惠试算</p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MarketNode extends AbstractGroupBuyMarketSupport {
    /**
     * 规则树收尾节点
     */
    private final EndNode endNode;
    /**
     * 处理错误的节点
     */
    private final ErrorNode errorNode;
    /**
     * 活动 repository 仓储适配器接口
     */
    private final IActivityRepository activityRepository;
    /**
     * 折扣计算服务的 Map 注入, 可以通过 键(Bean名) 获取对应的接口实现类注入
     */
    private final Map<String, IDiscountCalculateService> discountCalculateServiceMap;

    /**
     * 处理当前节点具体逻辑
     *
     * @param requestParam   入参
     * @param dynamicContext 动态上下文
     * @return 出参
     * @throws Exception 抛出所有错误
     */
    @Override
    public TrailResultEntity doApply(MarketProductEntity requestParam, DefaultActivityStrategyFactory.DynamicContext dynamicContext) throws Exception {
        log.info("拼团商品优惠试算规则树 -> {}, userId: {}, requestParam: {}", this.getClass().getSimpleName(), requestParam.getUserId(), requestParam);

        // 若动态上下文中有异常, 则直接进行路由
        if (dynamicContext.getException() != null)
            return router(requestParam, dynamicContext);

        // 获取 marketPlan 营销计划对应的实现类对象
        GroupBuyActivityDiscountVO groupBuyActivityDiscountVO = dynamicContext.getGroupBuyActivityDiscountVO();
        GroupBuyActivityDiscountVO.GroupBuyDiscount groupBuyDiscount = groupBuyActivityDiscountVO.getGroupBuyDiscount();
        IDiscountCalculateService discountCalculateService = discountCalculateServiceMap.get(groupBuyDiscount.getMarketPlan().getCode());

        if (discountCalculateService == null) {
            // 将不存在服务的异常, 存入动态上下文中
            dynamicContext.setException(new ServiceException("不存在" + groupBuyDiscount.getMarketPlan().getDesc() + "类型的服务! 支持的类型为: " + JSON.toJSONString(discountCalculateServiceMap.keySet())));
            return router(requestParam, dynamicContext);
        }
        // 计算出折后价格
        BigDecimal discountPrice = discountCalculateService.calculate(requestParam.getUserId(), dynamicContext.getSkuVO().getOriginalPrice(), groupBuyDiscount);
        dynamicContext.setDeductionPrice(discountPrice);
        log.info("折扣计算完成, 原始价格为: {}, 折后价格为: {}", dynamicContext.getSkuVO().getOriginalPrice(), discountPrice);

        return router(requestParam, dynamicContext);
    }

    /**
     * 获取下一节点的策略处理器 {@link StrategyHandler}
     *
     * @param requestParam   入参
     * @param dynamicContext 动态上下文
     * @return 下一节点的 {@link EndNode} 节点对象
     * @throws Exception 抛出所有错误
     */
    @Override
    public StrategyHandler<MarketProductEntity, DefaultActivityStrategyFactory.DynamicContext, TrailResultEntity> get(MarketProductEntity requestParam, DefaultActivityStrategyFactory.DynamicContext dynamicContext) throws Exception {
        if (dynamicContext.getException() != null)
            return errorNode;
        return endNode;
    }
}
