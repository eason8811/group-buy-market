package xin.eason.types.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 动态配置管理注解
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface DCCValue {
    /**
     * 设置配置值 格式: isSwitch:1
     * @return 配置值
     */
    String value();
}
