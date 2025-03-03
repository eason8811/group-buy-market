package xin.eason.domain.activity.service.discount.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import xin.eason.domain.activity.model.valobj.GroupBuyActivityDiscountVO;
import xin.eason.domain.activity.service.discount.AbstractDiscountCalculate;

import java.math.BigDecimal;

@Slf4j
@Service("MJ")
public class MJCalculateService extends AbstractDiscountCalculate {
    /**
     * 执行实际满减折扣计算
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
            String[] exprParts = marketExpr.split(",");
            BigDecimal expr1 = new BigDecimal(exprParts[0]);
            BigDecimal expr2 = new BigDecimal(exprParts[1]);
            if (originalPrice.compareTo(expr1) < 0)
                // originalPrice < expr1 直接原价返回
                return originalPrice;
            BigDecimal discountPrice = originalPrice.subtract(expr2);

            // 折后价格与 0.01 元相比, 如果不足 0.01 元按照 0.01 元计算
            if (discountPrice.compareTo(new BigDecimal("0.01")) <= 0)
                return new BigDecimal("0.01");
            return discountPrice;

        } catch (Exception e) {
            log.error("{} 折扣计算过程出错!", groupBuyDiscount.getMarketPlan().getDesc(), e);
            return originalPrice;
        }
    }
}
