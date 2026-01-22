package com.hanfu.interceptor;

import cn.hutool.core.util.StrUtil;
import com.hanfu.common.BusinessException;
import com.hanfu.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 认证拦截器
 */
@Component
@RequiredArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {

    private final JwtUtils jwtUtils;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 获取Token
        String token = request.getHeader("Authorization");
        
        if (StrUtil.isBlank(token)) {
            throw new BusinessException(401, "请先登录");
        }

        // 去除Bearer前缀
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        // 验证Token
        if (!jwtUtils.validateToken(token)) {
            throw new BusinessException(401, "登录已过期，请重新登录");
        }

        // 将用户信息存入请求属性
        request.setAttribute("userId", jwtUtils.getUserId(token));
        request.setAttribute("username", jwtUtils.getUsername(token));
        request.setAttribute("roleKey", jwtUtils.getRoleKey(token));

        return true;
    }
}
