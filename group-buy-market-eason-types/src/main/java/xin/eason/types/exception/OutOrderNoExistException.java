package xin.eason.types.exception;

/**
 * 外部订单编号不存在异常
 */
public class OutOrderNoExistException extends RuntimeException{
    /**
     * 外部订单编号不存在异常构造函数
     * @param message 错误信息
     */
    public OutOrderNoExistException(String message) {
        super(message);
    }
}
