package xin.eason.types.exception;

/**
 * 参与次数溢出异常类
 */
public class JoinLimitOverException extends RuntimeException{
    /**
     * 参与次数溢出异常构造函数
     * @param message 错误信息
     */
    public JoinLimitOverException(String message) {
        super(message);
    }
}
