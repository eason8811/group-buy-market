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
     * 线程池 用于激活和管理多线程
     */
    private final ThreadPoolExecutor threadPoolExecutor;
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
        }
        // 计算出折后价格
        BigDecimal discountPrice = discountCalculateService.calculate(requestParam.getUserId(), dynamicContext.getSkuVO().getOriginalPrice(), groupBuyDiscount);
        dynamicContext.setDeductionPrice(discountPrice);
        log.info("折扣计算完成, 原始价格为: {}, 折后价格为: {}", dynamicContext.getSkuVO().getOriginalPrice(), discountPrice);

        return router(requestParam, dynamicContext);
    }

    /**
     * 多线程加载拼团活动信息, 活动折扣信息
     * <p>将需要的数据存入动态上下文 {@link DefaultActivityStrategyFactory.DynamicContext}</p>
     *
     * @param requestParam   入参
     * @param dynamicContext 动态上下文
     */
    @Override
    protected void multiThread(MarketProductEntity requestParam, DefaultActivityStrategyFactory.DynamicContext dynamicContext) throws Exception {
        // 同时运行两个线程, 确保接口的响应效率
        // 创建线程 通过 repository 仓储获取拼团活动信息和活动对应的折扣信息
        QueryGroupBuyActivityDiscountVO queryGroupBuyActivityDiscountVO = new QueryGroupBuyActivityDiscountVO(requestParam.getSource(), requestParam.getChannel(), requestParam.getUserId(), activityRepository);
        FutureTask<GroupBuyActivityDiscountVO> activityDiscountVOFutureTask = new FutureTask<>(queryGroupBuyActivityDiscountVO);
        threadPoolExecutor.execute(activityDiscountVOFutureTask);

        // 创建线程 通过 repository 仓储获取商品信息信息 (日后开发中可能是同步库的获取, 也可能是 HTTP 或 RPC 的访问)
        QuerySkuVO querySkuVO = new QuerySkuVO(requestParam.getGoodsId(), activityRepository);
        FutureTask<SkuVO> skuVOFutureTask = new FutureTask<>(querySkuVO);
        threadPoolExecutor.execute(skuVOFutureTask);

        // 将多线程获取到的所需信息存入动态上下文
        GroupBuyActivityDiscountVO activityDiscountVO = activityDiscountVOFutureTask.get(10, TimeUnit.SECONDS);
        if (activityDiscountVO == null) {
            // 若为 null 证明获取配置途中出现 无营销配置异常, 将异常存入动态上下文
            dynamicContext.setException(new NoMarketConfigException("来源: " + requestParam.getSource() + ", 渠道: " + requestParam.getChannel() + " 无营销配置"));
            return;
        }
        dynamicContext.setGroupBuyActivityDiscountVO(activityDiscountVO);
        dynamicContext.setSkuVO(skuVOFutureTask.get(10, TimeUnit.SECONDS));
        log.info("拼团商品优惠试算规则树 -> {}, userId: {}, 异步线程加载数据 [GroupBuyActivityDiscountVO, SkuVO] 完成", this.getClass().getSimpleName(), requestParam.getUserId());
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
