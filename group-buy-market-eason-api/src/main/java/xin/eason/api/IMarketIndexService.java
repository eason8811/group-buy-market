package xin.eason.api;

import xin.eason.api.dto.GoodsMarketRequestDTO;
import xin.eason.api.dto.GoodsMarketResponseDTO;
import xin.eason.api.response.Result;

/**
 * 拼团首页 API 用于提供商品折扣信息, 拼团队伍信息, 拼团队伍和人数统计等 UI 显示信息
 */
public interface IMarketIndexService {
    /**
     * 查询拼团营销配置
     * @param goodsMarketRequestDTO 拼团首页展示信息请求数据传输类对象
     * @return 拼团首页展示响应数据传输类对象
     */
    Result<GoodsMarketResponseDTO> queryGroupBuyMarketConfig(GoodsMarketRequestDTO goodsMarketRequestDTO);
}
