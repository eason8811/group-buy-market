package xin.eason.api.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result<T> implements Serializable {
    private String msg;

    private Integer code;

    private T data;

    public static <T> Result<T> success(){
        Result<T> result = new Result<>();
        result.setMsg("success");
        result.setCode(1);
        return result;
    }

    public static <T> Result<T> success(T data){
        Result<T> result = new Result<>();
        result.setMsg("success");
        result.setCode(1);
        result.setData(data);
        return result;
    }

    public static <T> Result<T> error(String msg){
        Result<T> result = new Result<>();
        result.setMsg(msg);
        result.setCode(0);
        return result;
    }
}
