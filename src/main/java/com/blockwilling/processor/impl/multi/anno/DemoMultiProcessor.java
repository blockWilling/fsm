package com.blockwilling.processor.impl.multi.anno;


import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * 状态机引擎的处理器注解标识
 * Created by blockWilling  on 2022/8/11.
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Component
public @interface DemoMultiProcessor {
    String[] currentState();
    String event();
    String[] bizCode();
    String[] scene();
    String[] operatorCode();
    String[] category();
}
