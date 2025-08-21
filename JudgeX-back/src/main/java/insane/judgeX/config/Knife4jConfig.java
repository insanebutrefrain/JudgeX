package insane.judgeX.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Knife4j 接口文档配置
 * https://doc.xiaominfo.com/knife4j/documentation/get_start.html
 */
@Configuration  // Spring配置类
@EnableSwagger2 // 启用Swagger2
@Profile({"dev", "test"}) // 仅在开发和测试环境生效
public class Knife4jConfig {

    @Bean
    public Docket defaultApi2() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(new ApiInfoBuilder()     // API文档信息
                        .title("接口文档")          // 标题
                        .description("JudgeX-back") // 描述
                        .version("1.0")            // 版本
                        .build())
                .select()
                // 指定 Controller 扫描包路径
                .apis(RequestHandlerSelectors.basePackage("insane.judgeX.controller")) // 扫描控制器包
                .paths(PathSelectors.any())       // 包含所有路径
                .build();
    }
}
