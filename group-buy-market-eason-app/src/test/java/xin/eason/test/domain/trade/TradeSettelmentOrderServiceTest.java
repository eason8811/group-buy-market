package xin.eason.test.domain.trade;

import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import xin.eason.domain.trade.model.entity.OrderSettlementEntity;
import xin.eason.domain.trade.model.entity.OrderSettlementSuccessEntity;
import xin.eason.domain.trade.service.ITradeSettlementOrderService;

import java.time.LocalDateTime;

@SpringBootTest
@RunWith(SpringRunner.class)
@Slf4j
public class TradeSettelmentOrderServiceTest {

    @Autowired
    private ITradeSettlementOrderService tradeSettlementOrderService;

    @Test
    public void test_lockMarketPayOrder() {
        OrderSettlementEntity orderSettlementEntity = OrderSettlementEntity.builder()
                .source("s01")
                .channel("c01")
                .userId("Eason3")
                .outerOrderId("174669184617")
                .payTime(LocalDateTime.now())
                .build();

        OrderSettlementSuccessEntity orderSettlementSuccessEntity = tradeSettlementOrderService.settlementPayOrder(orderSettlementEntity);
        log.info("测试结果 res:{}", JSON.toJSONString(orderSettlementSuccessEntity));
    }

}
