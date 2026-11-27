package com.ry.shop.common;

import java.lang.annotation.*;

/**
 * 标在 Controller 方法上：AOP 自动记一条 oper_log
 * 例：@Log("创建订单")
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Log {
    /** 操作标题 */
    String value();
}