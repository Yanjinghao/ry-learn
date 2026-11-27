package com.ry.shop.aspect;

import com.ry.shop.common.Log;
import com.ry.shop.entity.OperLog;
import com.ry.shop.mapper.OperLogMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

@Aspect
@Component
public class OperLogAspect {

    private final OperLogMapper operLogMapper;

    public OperLogAspect(OperLogMapper operLogMapper) {
        this.operLogMapper = operLogMapper;
    }

    /** 只拦截带 @Log 的方法 */
    @Around("@annotation(com.ry.shop.common.Log)")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        long start = System.currentTimeMillis();
        OperLog log = new OperLog();
        log.setCreatedAt(LocalDateTime.now());
        log.setStatus(0); // 先假定成功

        // 1) 读注解标题
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        Method method = signature.getMethod();
        Log anno = method.getAnnotation(Log.class);
        if (anno != null) {
            log.setTitle(anno.value());
        }

        // 2) 从当前请求拿 URI / IP / userId（拦截器已 setAttribute）
        ServletRequestAttributes attrs =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs != null) {
            HttpServletRequest request = attrs.getRequest();
            log.setMethod(request.getMethod());
            log.setRequestUri(request.getRequestURI());
            log.setIp(request.getRemoteAddr());
            Object uid = request.getAttribute("userId");
            if (uid instanceof Long) {
                log.setUserId((Long) uid);
            }
        }

        try {
            Object result = pjp.proceed(); // 真正执行 Controller
            log.setCostMs((int) (System.currentTimeMillis() - start));
            saveQuietly(log);
            return result;
        } catch (Throwable ex) {
            log.setStatus(1);
            log.setErrorMsg(trim(ex.getMessage(), 500));
            log.setCostMs((int) (System.currentTimeMillis() - start));
            saveQuietly(log);
            throw ex; // 异常继续抛，交给 GlobalExceptionHandler
        }
    }

    /** 写库失败不影响主业务 */
    private void saveQuietly(OperLog log) {
        try {
            operLogMapper.insert(log);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String trim(String s, int max) {
        if (s == null) return null;
        return s.length() <= max ? s : s.substring(0, max);
    }
}