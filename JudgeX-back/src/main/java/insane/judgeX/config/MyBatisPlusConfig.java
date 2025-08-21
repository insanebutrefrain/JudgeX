package insane.judgeX.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis Plus 配置类
 * 用于配置MyBatis-Plus相关功能，包括分页插件和Mapper接口扫描
 */
@Configuration  // 标识这是一个Spring配置类，会被Spring容器自动扫描和加载
@MapperScan("insane.judgeX.mapper")  // 自动扫描指定包路径下的所有Mapper接口，无需在每个Mapper上单独添加@Mapper注解
public class MyBatisPlusConfig {

    /**
     * 配置MyBatis-Plus拦截器
     * 主要用于添加各种插件功能，如分页、性能分析等
     *
     * @return MybatisPlusInterceptor 拦截器实例
     */
    @Bean  // 将该方法的返回值注册为Spring容器管理的Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        // 创建MyBatis-Plus拦截器实例
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();

        // 添加分页插件拦截器
        // PaginationInnerInterceptor用于处理分页查询逻辑
        // DbType.MYSQL指定数据库类型为MySQL，确保生成正确的分页SQL语法
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));

        // 返回配置好的拦截器
        return interceptor;
    }
}
