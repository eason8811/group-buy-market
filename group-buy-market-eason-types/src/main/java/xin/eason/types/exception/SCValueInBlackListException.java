package xin.eason.types.exception;

/**
 * SC 处于黑名单内异常
 */
public class SCValueInBlackListException extends RuntimeException{
    /**
     * SC 处于黑名单内异常类构造函数
     * @param message 错误信息
     */
    public SCValueInBlackListException(String message) {
        super(message);
    }
}
