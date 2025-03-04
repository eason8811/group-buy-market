package xin.eason.domain.activity.service.trail.node;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import xin.eason.domain.activity.adapter.repository.IActivityRepository;
import xin.eason.domain.activity.model.entity.MarketProductEntity;
import xin.eason.domain.activity.model.entity.TrailResultEntity;
import xin.eason.domain.activity.model.valobj.GroupBuyActivityDiscountVO;
import xin.eason.domain.activity.model.valobj.SkuVO;
import xin.eason.domain.activity.service.trail.AbstractGroupBuyMarketSupport;
import xin.eason.domain.activity.service.trail.factory.DefaultActivityStrategyFactory;
import xin.eason.domain.activity.service.trail.thread.QueryGroupBuyActivityDiscountVO;
import xin.eason.domain.activity.service.trail.thread.QuerySkuVO;
import xin.eason.types.design.framework.tree.StrategyHandler;
import xin.eason.types.exception.NoMarketConfigException;

import java.util.concurrent.FutureTask;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 规则树人群标签处理节点
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TagNode extends AbstractGroupBuyMarketSupport {
    /**
     * 线程池 用于激活和管理多线程
     */
    private final ThreadPoolExecutor threadPoolExecutor;
    /**
     * 处理错误的节点
     */
    private final ErrorNode errorNode;
    /**
     * 规则树收尾节点
     */
    private final EndNode endNode;
    /**
     * 规则树营销服务节点
     */
    private final MarketNode marketNode;
    /**
     * 活动 repository 仓储适配器接口
     */
    private final IActivityRepository activityRepository;

    /**
     * 处理当前节点具体逻辑
     *
     * @param requestParam   入参
     * @param dynamicContext 动态上下文
     * @return 出参
     */
    @Override
    protected TrailResultEntity doApply(MarketProductEntity requestParam, DefaultActivityStrategyFactory.DynamicContext dynamicContext) throws Exception {
        log.info("拼团商品优惠试算规则树 -> {}, userId: {}, requestParam: {}", this.getClass().getSimpleName(), requestParam.getUserId(), requestParam);
        GroupBuyActivityDiscountVO activityDiscountVO = dynamicContext.getGroupBuyActivityDiscountVO();
        String tagId = activityDiscountVO.getTagId();
        boolean visible = activityDiscountVO.isVisible();
        boolean participable = activityDiscountVO.isParticipable();

        if (tagId == null) {
            dynamicContext.setIsVisible(true);
            dynamicContext.setIsParticipable(true);
            return router(requestParam, dynamicContext);
        }

        Boolean userInCrowd = activityRepository.queryUserInCrowd(tagId, requestParam.getUserId());

        dynamicContext.setIsVisible(userInCrowd || visible);
        dynamicContext.setIsParticipable((userInCrowd || participable) && dynamicContext.getIsVisible());
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
        QueryGroupBuyActivityDiscountVO queryGroupBuyActivityDiscountVO = new QueryGroupBuyActivityDiscountVO(requestParam.getSource(), requestParam.getChannel(), requestParam.getGoodsId(), activityRepository);
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
        if (!dynamicContext.getIsVisible() || !dynamicContext.getIsParticipable()){
            // 不可见或不可参与则直接流转向收尾节点
            return endNode;
        }
        return marketNode;
    }
}
