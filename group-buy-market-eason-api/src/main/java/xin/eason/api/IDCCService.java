package xin.eason.api;


import xin.eason.api.response.Result;

/**
 * 动态配置管理 API 接口
 */
public interface IDCCService {

    Result<Boolean> updateConfig(String key, String value);

}
