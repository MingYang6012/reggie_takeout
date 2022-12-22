package com.atming.reggie.component;

import com.atming.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * @CreateTime: 2022-11-09-19:08
 * @Author: Hello77
 * @toUser:
 * @note:  全局异常处理器
 */
//表明那些Controller层出现异常需要处理
@ControllerAdvice(value = "com.atming.reggie.controller")
@ResponseBody
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 表明出现那些异常可以别这个异常处理类处理
     * @return
     */
    @ExceptionHandler(CustomException.class)
    public R<String> exceptionHandler(CustomException exception){
        log.info("出现异常的原因：{}" ,exception.getMessage());

        return R.error(exception.getMessage());

    }

    /**
     * 表明出现那些异常可以别这个异常处理类处理
     * @return
     */
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public R<String> exceptionHandler(SQLIntegrityConstraintViolationException exception){
        log.info("出现异常的原因：{}" ,exception.getMessage());

        //判断要处理的请求是否为“Duplicate entry”，若是这个异常,则抽取这个异常的原因,返回给页面
        if (exception.getMessage().contains("Duplicate entry")){
            String[] s = exception.getMessage().split(" ");
            String reason = s[2] + "用户名重复";
            return R.error(reason);
        }
        return R.error("未知异常");

    }
}
