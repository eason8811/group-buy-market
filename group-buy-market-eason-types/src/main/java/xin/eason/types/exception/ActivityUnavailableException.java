package xin.eason.types.exception;

/**
 * 活动不可用异常类
 */
public class ActivityUnavailableException extends RuntimeException{
    /**
     * 活动不可用异常构造函数
     * @param message 错误信息
     */
    public ActivityUnavailableException(String message) {
        super(message);
    }
}
