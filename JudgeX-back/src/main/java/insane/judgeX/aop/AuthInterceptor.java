package insane.judgeX.aop;

import insane.judgeX.annotation.AuthCheck;
import insane.judgeX.common.ErrorCode;
import insane.judgeX.exception.BusinessException;
import insane.judgeX.model.entity.User;
import insane.judgeX.model.enums.UserRoleEnum;
import insane.judgeX.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 权限校验 AOP
 *
 */
@Aspect  // 标识这是一个切面类，用于实现面向切面编程
@Component  // 注册为Spring容器管理的组件
public class AuthInterceptor {

    @Resource  // 注入UserService，用于获取当前登录用户信息
    private UserService userService;

    /**
     * 执行拦截
     *
     * @param joinPoint 连接点，代表被拦截的方法
     * @param authCheck 权限检查注解，包含权限要求信息
     * @return 目标方法的执行结果
     * @throws Throwable 可能抛出的异常
     */
    @Around("@annotation(authCheck)")  // 环绕通知，拦截所有带有@AuthCheck注解的方法
    public Object doInterceptor(ProceedingJoinPoint joinPoint, AuthCheck authCheck) throws Throwable {
        // 获取注解中要求的最低权限角色
        String mustRole = authCheck.mustRole();

        // 从当前线程获取HTTP请求上下文
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        // 获取当前的HTTP请求对象
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();

        // 获取当前登录的用户信息
        User loginUser = userService.getLoginUser(request);

        // 必须有该权限才通过验证
        if (StringUtils.isNotBlank(mustRole)) {
            // 将字符串角色转换为枚举类型
            UserRoleEnum mustUserRoleEnum = UserRoleEnum.getEnumByValue(mustRole);

            // 如果角色枚举不存在，抛出无权限异常
            if (mustUserRoleEnum == null) {
                throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
            }

            // 获取当前用户的角色
            String userRole = loginUser.getUserRole();

            // 如果要求的角色是封禁状态，直接拒绝访问
            if (UserRoleEnum.BAN.equals(mustUserRoleEnum)) {
                throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
            }

            // 如果要求管理员权限，则必须完全匹配用户角色
            if (UserRoleEnum.ADMIN.equals(mustUserRoleEnum)) {
                if (!mustRole.equals(userRole)) {
                    throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
                }
            }
        }

        // 通过权限校验，继续执行目标方法
        return joinPoint.proceed();
    }
}
