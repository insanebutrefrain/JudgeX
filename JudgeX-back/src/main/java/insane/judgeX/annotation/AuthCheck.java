package insane.judgeX.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 权限校验注解
 * 用于方法级别的权限控制，验证当前用户是否具有执行该方法的权限
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AuthCheck {

    /**
     * 指定必须拥有的角色权限
     * 默认为空字符串，表示不需要特定角色
     * 使用示例：@AuthCheck(mustRole = "admin") 表示只有管理员角色可以访问
     *
     * @return 需要的角色名称
     */
    String mustRole() default "";
}

