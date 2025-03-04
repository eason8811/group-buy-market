package xin.eason.test.domain.activity;

import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import xin.eason.domain.activity.model.entity.MarketProductEntity;
import xin.eason.domain.activity.model.entity.TrailResultEntity;
import xin.eason.domain.activity.service.IIndexGroupBuyMarketService;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class IIndexGroupBuyMarketServiceTest {

    @Autowired
    private IIndexGroupBuyMarketService indexGroupBuyMarketService;

    @Test
    public void testIndexMarketTrial() throws Exception {
        long start = System.currentTimeMillis();
        MarketProductEntity marketProductEntity = new MarketProductEntity();
        marketProductEntity.setUserId("xiaofuge");
        marketProductEntity.setSource("s01");
        marketProductEntity.setChannel("c02");
        marketProductEntity.setGoodsId("9890002");

        TrailResultEntity trialBalanceEntity = indexGroupBuyMarketService.indexTrail(marketProductEntity);
        log.info("请求参数:{}", JSON.toJSONString(marketProductEntity));
        log.info("返回结果:{}", JSON.toJSONString(trialBalanceEntity));
        long end = System.currentTimeMillis();
        log.info("所需时间: {}ms", end - start);

        start = System.currentTimeMillis();
        marketProductEntity = new MarketProductEntity();
        marketProductEntity.setUserId("xiaofuge");
        marketProductEntity.setSource("s01");
        marketProductEntity.setChannel("c01");
        marketProductEntity.setGoodsId("9890001");

        trialBalanceEntity = indexGroupBuyMarketService.indexTrail(marketProductEntity);
        log.info("请求参数:{}", JSON.toJSONString(marketProductEntity));
        log.info("返回结果:{}", JSON.toJSONString(trialBalanceEntity));
        end = System.currentTimeMillis();
        log.info("所需时间: {}ms", end - start);
    }

    @Test
    public void testIndexMarketTrialEmpty() throws Exception {
        long start = System.currentTimeMillis();
        MarketProductEntity marketProductEntity = new MarketProductEntity();

        TrailResultEntity trialBalanceEntity = indexGroupBuyMarketService.indexTrail(marketProductEntity);
        log.info("请求参数:{}", JSON.toJSONString(marketProductEntity));
        log.info("返回结果:{}", JSON.toJSONString(trialBalanceEntity));
        long end = System.currentTimeMillis();
        log.info("所需时间: {}ms", end - start);
    }

}