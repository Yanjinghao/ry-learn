package com.ry.shop.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ry.shop.common.Result;
import com.ry.shop.utils.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public AuthInterceptor(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {
        // 1. 取 Authorization
        String auth = request.getHeader("Authorization");
        if (auth == null || !auth.startsWith("Bearer ")) {
            write401(response, "未登录或 token 缺失");
            return false; // 拦住，不进 Controller
        }

        // 2. 去掉 "Bearer " 前缀，剩下才是 token
        String token = auth.substring(7);

        // 3. 解析；假 token / 过期会抛异常
        try {
            Long userId = jwtUtil.getUserId(token);
            // 后面接口若要用当前用户，可以从 request 取
            request.setAttribute("userId", userId);
            return true; // 放行
        } catch (Exception e) {
            write401(response, "token 无效或已过期");
            return false;
        }
    }

    private void write401(HttpServletResponse response, String msg) throws Exception {
        response.setStatus(401);
        response.setContentType("application/json;charset=UTF-8");
        Result<Void> result = Result.fail(401, msg);
        response.getWriter().write(objectMapper.writeValueAsString(result));
    }
}