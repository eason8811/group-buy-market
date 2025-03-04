package xin.eason.types.exception;

/**
 * 无营销配置异常
 */
public class NoMarketConfigException extends RuntimeException{
    /**
     * 无营销配置异常构造函数
     * @param message 异常消息
     */
    public NoMarketConfigException(String message) {
        super(message);
    }
}
