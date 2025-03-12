package xin.eason.api.dto;

import lombok.Data;

/**
 * 拼团首页展示信息请求数据传输类
 */
@Data
public class GoodsMarketRequestDTO {

    /**
     * 用户 ID
     */
    private String userId;
    /**
     * 来源
     */
    private String source;
    /**
     * 渠道
     */
    private String channel;
    /**
     * 商品 ID
     */
    private String goodsId;

}