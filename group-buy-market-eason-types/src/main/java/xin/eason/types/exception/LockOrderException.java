package xin.eason.types.exception;

/**
 * 锁定拼团订单异常类
 */
public class LockOrderException extends RuntimeException{
    /**
     * 锁定拼团订单异常构造函数
     * @param message 错误信息
     */
    public LockOrderException(String message) {
        super(message);
    }
}
