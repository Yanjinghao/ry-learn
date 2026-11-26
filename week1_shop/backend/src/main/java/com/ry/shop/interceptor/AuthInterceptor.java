package com.ry.shop.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ry.shop.common.RequiresPermission;
import com.ry.shop.common.Result;
import com.ry.shop.service.PermissionService;
import com.ry.shop.utils.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import java.util.List;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    private final JwtUtil jwtUtil;
    private final PermissionService permissionService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public AuthInterceptor(JwtUtil jwtUtil, PermissionService permissionService) {
        this.jwtUtil = jwtUtil;
        this.permissionService = permissionService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {
        // 静态资源等不是 HandlerMethod，直接放行
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }

        // ---------- 1) 登录校验：401 ----------
        String auth = request.getHeader("Authorization");
        if (auth == null || !auth.startsWith("Bearer ")) {
            writeJson(response, 401, "未登录或 token 缺失");
            return false;
        }

        // 加上附加参数userId
        Long userId;
        try {
            String token = auth.substring(7);
            userId = jwtUtil.getUserId(token);
            request.setAttribute("userId", userId);
        } catch (Exception e) {
            writeJson(response, 401, "token 无效或已过期");
            return false;
        }

        // ---------- 2) 权限字校验：403 ----------
        // 方法上的注解优先；没有再看类上的
        RequiresPermission methodAnno = handlerMethod.getMethodAnnotation(RequiresPermission.class);
        RequiresPermission classAnno = handlerMethod.getBeanType().getAnnotation(RequiresPermission.class);
        RequiresPermission need = methodAnno != null ? methodAnno : classAnno;

        // 没标注解 = 登录即可访问（例如部分查询可先不拦）
        if (need == null) {
            return true;
        }

        String required = need.value(); // 例如 "user:list"
        List<String> permissions = permissionService.listPermissions(userId);
        if (permissions == null || !permissions.contains(required)) {
            writeJson(response, 403, "无权限：" + required);
            return false;
        }

        return true;
    }

    private void writeJson(HttpServletResponse response, int code, String msg) throws Exception {
        response.setStatus(code); // HTTP 状态码：401 或 403
        response.setContentType("application/json;charset=UTF-8");
        Result<Void> result = Result.fail(code, msg);
        response.getWriter().write(objectMapper.writeValueAsString(result));
    }
}