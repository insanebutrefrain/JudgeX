package insane.judgeX.interceptor;

import insane.judgeX.common.ErrorCode;
import insane.judgeX.exception.BusinessException;
import insane.judgeX.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
@Slf4j
public class JwtInterceptor implements HandlerInterceptor {
    String authorization = "authorization";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        log.info("[jwt拦截器]:请求{}", request.getRequestURI());
        if (HttpMethod.OPTIONS.toString().equals(request.getMethod())) {
            return true;
        }
        String token = request.getHeader(authorization);
        if (token == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        // 将用户信息存入request
        Claims claims = JwtUtils.parseToken(token);
        if (!claims.containsKey("userId")) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        request.setAttribute("userId", claims.get("userId"));
        log.info("[jwt拦截器]:放行{}", request.getRequestURI());
        return true;
    }
}
