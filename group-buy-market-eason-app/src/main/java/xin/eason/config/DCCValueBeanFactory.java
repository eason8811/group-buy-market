package xin.eason.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBucket;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import xin.eason.types.annotations.DCCValue;
import xin.eason.types.exception.DynamicConfControlException;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * 动态配置管理工厂, 用于初始化和动态更改配置
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class DCCValueBeanFactory implements BeanPostProcessor {
    /**
     * 基础配置路径
     */
    private static final String BASE_CONFIG_PATH = "group_buy_market_dcc_";
    /**
     * 发布 - 订阅模式标题
     */
    private static final String LISTEN_TOPIC = "group_buy_market_dcc";
    /**
     * redisson 客户端
     */
    private final RedissonClient redissonClient;
    /**
     * 用于存放属性包含了 {@link DCCValue} 注解的
     */
    private final Map<String, Object> dccObjMap = new HashMap<>();

    /**
     * 为 redis 话题添加消息监听器
     * @return redis topic 的 bean 对象
     */
    @Bean("dccTopicListener")
    public RTopic redisTopicListener() {
        // 获取 topic
        RTopic topic = redissonClient.getTopic(LISTEN_TOPIC);
        // 添加话题监听器
        topic.addListener(String.class, ((channel, msg) -> {
            String[] configControlParams = msg.split(",");
            String attribute = configControlParams[0];
            String value = configControlParams[1];
            String key = BASE_CONFIG_PATH + attribute;

            // 在 redis 中根据 key 获取 bucket, 若 bucket 不存在则直接返回
            RBucket<Object> bucket = redissonClient.getBucket(key);
            if (!bucket.isExists())
                return;
            // 设置新的配置
            bucket.set(value);

            // 更改配置对象中属性的值
            Object targetObj = dccObjMap.get(key);
            if (targetObj == null)  // 若不存在这个键值对则直接返回
                return;
            Class<?> objClass = targetObj.getClass();
            if (AopUtils.isAopProxy(targetObj)) {
                objClass = AopUtils.getTargetClass(targetObj);
            }

            Field field = null;
            Field[] fields = objClass.getDeclaredFields();
            for (Field f : fields) {
                if (f.getName().toLowerCase().equals(attribute)){
                    field = f;
                    break;
                }
            }
            if (field == null)
                return;

            // 更改对应 attribute 的值
            try {
                field.setAccessible(true);
                if (field.getType() == Boolean.class)
                    field.set(targetObj, Boolean.valueOf(value));
                else if (field.getType() == Integer.class)
                    field.set(targetObj, Integer.valueOf(value));
                else
                    field.set(targetObj, value);
                field.setAccessible(false);

                log.info("DCC 节点监听，动态设置值 {} {}", key, value);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }));
        return topic;
    }


    /**
     * 每一个 bean 对象创建完成后调用的方法, 用于检测属性中添加了 {@link DCCValue} 注解的 bean 对象并设置默认值
     *
     * @param bean     bean 对象
     * @param beanName bean 对象的名称
     * @return bean 对象
     * @throws BeansException 抛出bean异常
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        // 获取 bean 对象的目标类, 和目标对象
        Class<?> objClass = bean.getClass();
        Object targetObj = bean;
        // 判断 bean 对象是否是 AOP 代理对象, 如果是, 则获取其目标对象
        if (AopUtils.isAopProxy(bean)){
            objClass = AopUtils.getTargetClass(bean);
            targetObj = AopProxyUtils.getSingletonTarget(bean);
        }

        // 获取对象的目标类属性列表
        Field[] fields = objClass.getDeclaredFields();
        for (Field field : fields) {
            // 遍历每个属性, 判断其是否含有 @DCCValue 注解
            if (!field.isAnnotationPresent(DCCValue.class))
                // 没有 @DCCValue 注解则跳过
                continue;
            // 取出注解中的 value 值, 分割冒号, 若没有按照格式设置默认值, 则抛出错误
            String dccValue = field.getAnnotation(DCCValue.class).value();
            if (dccValue == null) {
                throw new DynamicConfControlException(field.getName() + "字段添加了 @DCCValue 注解, 但是没有进行默认配置! 请进行默认配置!");
            }
            String[] configControlParams = dccValue.split(":");
            String key = BASE_CONFIG_PATH + configControlParams[0];
            String value = configControlParams.length == 2 ? configControlParams[1] : null;
            if (value == null)
                throw new DynamicConfControlException(field.getName() + "字段 @DCCValue 注解配置错误! " + key + " 不应该为空!");

            String setValue = value;

            try {
                // 从 redis 中获取值
                RBucket<String> bucket = redissonClient.getBucket(key);
                if (!bucket.isExists())
                    bucket.set(value);
                else {
                    setValue = bucket.get();
                }
                field.setAccessible(true);
                if (field.getType() == Boolean.class)
                    field.set(targetObj, Boolean.valueOf(setValue));
                else if (field.getType() == Integer.class)
                    field.set(targetObj, Integer.valueOf(setValue));
                else
                    field.set(targetObj, setValue);
                field.setAccessible(false);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            dccObjMap.put(key, targetObj);
        }


        return bean;
    }
}
