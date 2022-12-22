package com.atming.reggie.component;


import com.alibaba.fastjson.JSON;
import com.atming.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @CreateTime: 2022-11-08-20:01
 * @Author: Hello77
 * @toUser:
 * @note:  注册过滤器来进行登入校验
 */
//@WebFilter(filterName = "loginCheckFilter",urlPatterns = "/*")
@Slf4j
public class LoginCheckFilter implements Filter {

    //路径匹配器，支持通配符
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        //将ServletRequest和ServletResponse向下转型为HttpServletRequest
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;

        //1.获取本次请求的URI
        String requestURI = httpServletRequest.getRequestURI();

        //定义不需要处理的请求路径
        String[] urlCompare = new String[]{
                "/employee/login","/employee/logout","/backend/**","/front/**"
        };

        //2.判断本次请求时候需要处理
        boolean check = check(urlCompare, requestURI);

        //3.如果不需要处理，则直接放行
        if (check){
            //这个是过滤器放行的方法
            chain.doFilter(request,response);
            return;
        }

        //4.判断登入状态，如果已登入，则直接放行
        Object employee = httpServletRequest.getSession().getAttribute("employee");

        if (employee != null){
            //这个是过滤器放行的方法
            chain.doFilter(request,response);
            return;
        }

        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return;


    }


    /**
     *  路径匹配的方法
     * @param urlCompare
     * @param requestURI
     * @return
     */
    public boolean check(String[] urlCompare,String requestURI){
        for (String url : urlCompare) {
            boolean match = PATH_MATCHER.match(url, requestURI);
            if (match){
                return true;
            }
        }
        return false;
    }

}
