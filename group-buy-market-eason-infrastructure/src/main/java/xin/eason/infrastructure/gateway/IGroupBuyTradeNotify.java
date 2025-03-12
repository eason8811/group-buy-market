package xin.eason.infrastructure.gateway;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Url;

/**
 * 拼团平台请求外部接口的 接口
 */
public interface IGroupBuyTradeNotify {

    /**
     * 向回调 URL 发送回调信息
     * @param notifyUrl 回调 URL
     * @param notifyJSONParam 回调的 JSON 参数
     * @return 回调的响应信息
     */
    @POST
    Call<String> notify(@Url String notifyUrl, @Body RequestBody notifyJSONParam);
}
