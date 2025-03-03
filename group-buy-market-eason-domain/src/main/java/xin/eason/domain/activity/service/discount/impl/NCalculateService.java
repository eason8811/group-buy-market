package xin.eason.domain.activity.service.discount.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import xin.eason.domain.activity.model.valobj.GroupBuyActivityDiscountVO;
import xin.eason.domain.activity.service.discount.AbstractDiscountCalculate;

import java.math.BigDecimal;

@Slf4j
@Service("N")
public class NCalculateService extends AbstractDiscountCalculate {
    /**
     * 执行实际 N元购 折扣计算
     *
     * @param originalPrice    商品原始价格
     * @param groupBuyDiscount 拼团活动折扣值对象
     * @return 折后价格
     */
    @Override
    public BigDecimal doCalculate(BigDecimal originalPrice, GroupBuyActivityDiscountVO.GroupBuyDiscount groupBuyDiscount) {
        log.info("正在执行 {} 折扣计算, 折扣名称: {}, 人群标签模式: {}", groupBuyDiscount.getMarketPlan().getDesc(), groupBuyDiscount.getDiscountName(), groupBuyDiscount.getDiscountType());
        String marketExpr = groupBuyDiscount.getMarketExpr();
        try {
            // 直接返回 N 元价格
            return new BigDecimal(marketExpr);
        } catch (Exception e) {
            log.error("{} 折扣计算过程出错!", groupBuyDiscount.getMarketPlan().getDesc(), e);
            return originalPrice;
        }
    }
}
