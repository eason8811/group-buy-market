package xin.eason.types.exception;

/**
 * 参数非法异常类
 */
public class ParamInvalidException extends RuntimeException{
    /**
     * 参数非法异常类构造函数
     * @param message 错误信息
     */
    public ParamInvalidException(String message) {
        super(message);
    }
}
