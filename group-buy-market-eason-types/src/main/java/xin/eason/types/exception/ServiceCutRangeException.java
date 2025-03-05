package xin.eason.types.exception;

/**
 * 服务切量异常类
 */
public class ServiceCutRangeException extends RuntimeException{
    /**
     * 服务切量异常构造函数
     * @param message 错误信息
     */
    public ServiceCutRangeException(String message) {
        super(message);
    }
}
