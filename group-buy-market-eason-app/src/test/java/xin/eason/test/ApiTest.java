package xin.eason.test;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import xin.eason.infrastructure.dao.IGroupBuyActivity;
import xin.eason.infrastructure.dao.IGroupBuyDiscount;
import xin.eason.infrastructure.dao.po.GroupBuyActivityPO;
import xin.eason.infrastructure.dao.po.GroupBuyDiscountPO;

import java.util.List;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class ApiTest {

    @Autowired
    private IGroupBuyActivity groupBuyActivity;
    @Autowired
    private IGroupBuyDiscount groupBuyDiscount;

    @Test
    public void testDatabase() {
        LambdaQueryWrapper<GroupBuyActivityPO> groupBuyActivityWrapper = new LambdaQueryWrapper<>();
        List<GroupBuyActivityPO> activityList = groupBuyActivity.selectList(groupBuyActivityWrapper);
        log.info("{}", activityList);

        LambdaQueryWrapper<GroupBuyDiscountPO> groupBuyDiscountWrapper = new LambdaQueryWrapper<>();
        List<GroupBuyDiscountPO> discountList = groupBuyDiscount.selectList(groupBuyDiscountWrapper);
        log.info("{}", discountList);

    }

}
