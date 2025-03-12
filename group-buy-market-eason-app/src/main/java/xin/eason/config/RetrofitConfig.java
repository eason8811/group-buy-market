package xin.eason.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import xin.eason.infrastructure.gateway.IGroupBuyTradeNotify;

/**
 * Retrofit 配置类
 */
@Configuration
public class RetrofitConfig {

    @Bean
    public Retrofit retrofit() {
        return new Retrofit.Builder().baseUrl("https://localhost")
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(JacksonConverterFactory.create(
                new ObjectMapper().setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
        )).build();
    }

    @Bean
    public IGroupBuyTradeNotify iGroupBuyTradeNotify(Retrofit retrofit) {
        return retrofit.create(IGroupBuyTradeNotify.class);
    }
}
