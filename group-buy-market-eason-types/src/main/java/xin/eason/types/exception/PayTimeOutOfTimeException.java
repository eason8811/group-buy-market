package xin.eason.types.exception;

/**
 * 支付时间不在合法时间内异常
 */
public class PayTimeOutOfTimeException extends RuntimeException{
    /**
     * 支付时间不在合法时间内异常类构造函数
     * @param message 错误信息
     */
    public PayTimeOutOfTimeException(String message) {
        super(message);
    }
}
