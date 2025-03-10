package xin.eason.types.exception;

/**
 * 表更新记录数量为 0 异常
 */
public class UpdateAmountZeroException extends RuntimeException{
    /**
     * 表更新记录数量为 0 异常构造函数
     * @param message 错误信息
     */
    public UpdateAmountZeroException(String message) {
        super(message);
    }
}
