package xin.eason.test.domain.trade;

import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import xin.eason.domain.trade.model.aggregate.GroupBuyOrderAggregate;
import xin.eason.domain.trade.model.entity.OrderSettlementSuccessEntity;
import xin.eason.domain.trade.model.entity.PayOrderActivityEntity;
import xin.eason.domain.trade.model.entity.PayOrderDiscountEntity;
import xin.eason.domain.trade.model.entity.PayOrderTeamEntity;
import xin.eason.domain.trade.model.valobj.GroupBuyProgressVO;
import xin.eason.domain.trade.model.valobj.OrderStatus;
import xin.eason.domain.trade.service.ITradeSettlementOrderService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@SpringBootTest
@RunWith(SpringRunner.class)
@Slf4j
public class TradeSettelmentOrderServiceTest {

    @Autowired
    private ITradeSettlementOrderService tradeSettlementOrderService;

    @Test
    public void test_lockMarketPayOrder() {
        PayOrderTeamEntity teamEntity = PayOrderTeamEntity.builder()
                .teamId("05450613")
                .orderStatus(OrderStatus.GROUPING)
                .teamProgress(
                        GroupBuyProgressVO.builder()
                                .targetCount(3)
                                .completeCount(2)
                                .lockCount(3)
                                .build()
                )
                .build();

        PayOrderActivityEntity activityEntity = PayOrderActivityEntity.builder()
                .activityId(100123L)
                .activityName("")
                .targetCount(3)
                .startTime(LocalDateTime.parse("2024-12-07 10:19:40", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .endTime(LocalDateTime.parse("2025-12-07 10:19:40", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .build();

        PayOrderDiscountEntity discountEntity = PayOrderDiscountEntity.builder()
                .source("s01")
                .channel("c01")
                .build();

        GroupBuyOrderAggregate groupBuyOrderAggregate = GroupBuyOrderAggregate.builder()
                .userId("Eason3")
                .outerOrderId("862242869743")
                .payOrderEntity(null)
                .payOrderTeamEntity(teamEntity)
                .payOrderDiscountEntity(discountEntity)
                .payOrderActivityEntity(activityEntity)
                .build();

        OrderSettlementSuccessEntity orderSettlementSuccessEntity = tradeSettlementOrderService.settlementPayOrder(groupBuyOrderAggregate);
        log.info("测试结果 res:{}", JSON.toJSONString(orderSettlementSuccessEntity));
    }

}
