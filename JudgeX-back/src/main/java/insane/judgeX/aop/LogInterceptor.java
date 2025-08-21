package insane.judgeX.aop;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;

/**
 * 请求响应日志 AOP
 * 用于拦截Controller层的所有请求，记录请求和响应日志，并统计执行时间
 **/
@Aspect  // 标识这是一个切面类，用于实现面向切面编程
@Component  // 注册为Spring容器管理的组件
@Slf4j  // Lombok注解，自动生成日志记录器log变量
public class LogInterceptor {

    /**
     * 执行拦截
     * 环绕通知，拦截insane.judgeX.controller包下所有类的所有方法
     *
     * @param point 连接点，代表被拦截的方法
     * @return 目标方法的执行结果
     * @throws Throwable 可能抛出的异常
     */
    @Around("execution(* insane.judgeX.controller.*.*(..))")
    public Object doInterceptor(ProceedingJoinPoint point) throws Throwable {
        // 计时 - 使用Spring的StopWatch工具统计方法执行时间
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        // 获取请求路径 - 从当前线程获取HTTP请求上下文
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        // 获取当前的HTTP请求对象
        HttpServletRequest httpServletRequest = ((ServletRequestAttributes) requestAttributes).getRequest();

        // 生成请求唯一 id - 为每次请求生成唯一标识，便于追踪
        String requestId = UUID.randomUUID().toString();
        // 获取请求的URI路径
        String url = httpServletRequest.getRequestURI();

        // 获取请求参数 - 获取被拦截方法的参数值
        Object[] args = point.getArgs();
        // 将参数数组转换为字符串，参数之间用逗号和空格分隔
        String reqParam = "[" + StringUtils.join(args, ", ") + "]";

        // 输出请求日志 - 记录请求开始信息，包括请求ID、路径、客户端IP和参数
        log.info("request start，id: {}, path: {}, ip: {}, params: {}", requestId, url,
                httpServletRequest.getRemoteHost(), reqParam);

        // 执行原方法 - 调用被拦截的目标方法
        Object result = point.proceed();

        // 输出响应日志 - 记录请求结束信息和执行耗时
        stopWatch.stop();  // 停止计时
        long totalTimeMillis = stopWatch.getTotalTimeMillis();  // 获取总执行时间(毫秒)
        log.info("request end, id: {}, cost: {}ms", requestId, totalTimeMillis);

        return result;  // 返回目标方法的执行结果
    }
}

