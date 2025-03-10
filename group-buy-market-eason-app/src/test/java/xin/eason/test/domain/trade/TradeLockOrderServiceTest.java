package xin.eason.test.domain.trade;

import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import xin.eason.api.IMarketTradeController;
import xin.eason.api.dto.LockMarketPayOrderRequestDTO;
import xin.eason.api.dto.LockMarketPayOrderResponseDTO;
import xin.eason.api.response.Result;

@SpringBootTest
@RunWith(SpringRunner.class)
@Slf4j
public class TradeLockOrderServiceTest {

    @Autowired
    private IMarketTradeController marketTradeController;

    @Test
    public void test_lockMarketPayOrder() {
        LockMarketPayOrderRequestDTO lockMarketPayOrderRequestDTO = new LockMarketPayOrderRequestDTO();
        lockMarketPayOrderRequestDTO.setUserId("Eason1");
        lockMarketPayOrderRequestDTO.setTeamId(null);
        lockMarketPayOrderRequestDTO.setActivityId(100123L);
        lockMarketPayOrderRequestDTO.setGoodsId("9890001");
        lockMarketPayOrderRequestDTO.setSource("s01");
        lockMarketPayOrderRequestDTO.setChannel("c01");
        lockMarketPayOrderRequestDTO.setOuterOrderId(RandomStringUtils.randomNumeric(12));
//        lockMarketPayOrderRequestDTO.setOuterOrderId("551640666540");
        Result<LockMarketPayOrderResponseDTO> lockMarketPayOrderResponseDTOResponse = marketTradeController.lockMarketPayOrder(lockMarketPayOrderRequestDTO);
        log.info("测试结果 req:{} res:{}", JSON.toJSONString(lockMarketPayOrderRequestDTO), JSON.toJSONString(lockMarketPayOrderResponseDTOResponse));
    }

    @Test
    public void test_lockMarketPayOrder_teamId_not_null() {
        LockMarketPayOrderRequestDTO lockMarketPayOrderRequestDTO = new LockMarketPayOrderRequestDTO();
        lockMarketPayOrderRequestDTO.setUserId("Eason3");
        lockMarketPayOrderRequestDTO.setTeamId("05450613");
        lockMarketPayOrderRequestDTO.setActivityId(100123L);
        lockMarketPayOrderRequestDTO.setGoodsId("9890001");
        lockMarketPayOrderRequestDTO.setSource("s01");
        lockMarketPayOrderRequestDTO.setChannel("c01");
        lockMarketPayOrderRequestDTO.setOuterOrderId(RandomStringUtils.randomNumeric(12));
//        lockMarketPayOrderRequestDTO.setOuterOrderId("551640666540");
        Result<LockMarketPayOrderResponseDTO> lockMarketPayOrderResponseDTOResponse = marketTradeController.lockMarketPayOrder(lockMarketPayOrderRequestDTO);
        log.info("测试结果 req:{} res:{}", JSON.toJSONString(lockMarketPayOrderRequestDTO), JSON.toJSONString(lockMarketPayOrderResponseDTOResponse));
    }

}
