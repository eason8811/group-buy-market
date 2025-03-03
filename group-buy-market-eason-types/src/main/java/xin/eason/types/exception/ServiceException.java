package xin.eason.types.exception;

public class ServiceException extends RuntimeException{
    /**
     * 服务异常类构造函数
     * @param message 错误信息
     */
    public ServiceException(String message) {
        super(message);
    }
}
