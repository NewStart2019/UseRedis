package com.example.demo.config;

import com.example.demo.common.web.AjaxResult;
import com.example.demo.exception.RedisLimitException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理类
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandle {

    @ExceptionHandler(RedisLimitException.class)
    public AjaxResult<Void> businessException(RedisLimitException e) {
        if (StringUtils.hasText(e.getMessage())) {
            return AjaxResult.error(e.getMessage());
        }
        return AjaxResult.error(e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public AjaxResult<Void> handleException(Exception e) {
        log.error(e.getMessage(), e);
        return AjaxResult.error(e.getMessage());
    }

}
