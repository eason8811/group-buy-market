package xin.eason.types.exception;

/**
 * 服务降级异常类
 */
public class ServiceDownGradeException extends RuntimeException{
    /**
     * 服务降级异常构造函数
     * @param message 错误信息
     */
    public ServiceDownGradeException(String message) {
        super(message);
    }
}
