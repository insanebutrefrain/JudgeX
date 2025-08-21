package insane.judgeX.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 全局跨域配置
 *
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // 覆盖所有请求路径，对整个应用生效
        registry.addMapping("/**")
                // 允许发送 Cookie 和其他凭证信息
                .allowCredentials(true)
                // 放行所有域名的请求（使用 allowedOriginPatterns 而非 allowedOrigins
                // 是因为当 allowCredentials 为 true 时，不能直接使用 "*"）
                .allowedOriginPatterns("*")
                // 允许的 HTTP 方法列表
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                // 允许所有的请求头字段
                .allowedHeaders("*")
                // 暴露给客户端的响应头字段
                .exposedHeaders("*");
    }
}
