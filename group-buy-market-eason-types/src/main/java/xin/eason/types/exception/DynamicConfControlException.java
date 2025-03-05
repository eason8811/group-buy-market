package xin.eason.types.exception;

/**
 * 动态配置异常类
 */
public class DynamicConfControlException extends RuntimeException{
    /**
     * 动态配置异常构造函数
     * @param message 异常信息
     */
    public DynamicConfControlException(String message) {
        super(message);
    }
}
