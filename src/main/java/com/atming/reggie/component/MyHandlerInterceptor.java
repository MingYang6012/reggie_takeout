package com.atming.reggie.component;

import com.alibaba.fastjson.JSON;
import com.atming.reggie.common.BaseContext;
import com.atming.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @CreateTime: 2022-11-08-18:30
 * @Author: Hello77
 * @toUser:
 * @note:
 */
@Component
@Slf4j
public class MyHandlerInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.info("拦截的请求地址：{}",request.getRequestURI());

        Long employee = (Long) request.getSession().getAttribute("employee");
        Long user = (Long) request.getSession().getAttribute("user");
        BaseContext.setCurrentId(employee);
        BaseContext.setCurrentId(user);

        if (employee != null || user != null){
            log.info("用户以登入：用户id为：{}或{}",request.getSession().getAttribute("employee"),user);
            return true;
        }

        log.info("用户未登入");
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return false;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }
}
