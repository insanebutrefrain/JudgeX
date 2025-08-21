package insane.judgeX.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.springframework.boot.jackson.JsonComponent;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

/**
 * Spring MVC Json 配置
 * 用于解决Long类型在JSON序列化时的精度丢失问题
 */
@JsonComponent  // Spring Boot注解，标识这是一个JSON组件，会自动注册到Jackson中
public class JsonConfig {

    /**
     * 添加 Long 转 json 精度丢失的配置
     * 解决JavaScript处理大整数时的精度丢失问题
     * @param builder Jackson对象映射器构建器
     * @return 配置好的ObjectMapper实例
     */
    @Bean  // Spring注解，将方法返回值注册为Spring Bean
    public ObjectMapper jacksonObjectMapper(Jackson2ObjectMapperBuilder builder) {
        // 使用构建器创建ObjectMapper实例，不创建XML映射器
        ObjectMapper objectMapper = builder.createXmlMapper(false).build();

        // 创建一个简单的Jackson模块，用于注册自定义序列化器
        SimpleModule module = new SimpleModule();

        // 为Long包装类型注册ToStringSerializer序列化器
        // 这会将Long类型序列化为字符串，避免JSON数字精度丢失
        module.addSerializer(Long.class, ToStringSerializer.instance);

        // 为long基本类型注册ToStringSerializer序列化器
        // 确保基本类型long也会被序列化为字符串
        module.addSerializer(Long.TYPE, ToStringSerializer.instance);

        // 将自定义模块注册到ObjectMapper中，使其生效
        objectMapper.registerModule(module);

        // 返回配置好的ObjectMapper
        return objectMapper;
    }
}
