package com.atming.reggie.config;

import com.atming.reggie.common.JacksonObjectMapper;
import com.atming.reggie.component.FrontLoginInterceptor;
import com.atming.reggie.component.MyHandlerInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * @CreateTime: 2022-11-07-21:05
 * @Author: Hello77
 * @toUser:
 * @note:
 */
@Slf4j
@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {

    //扩展消息转化类
    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        log.info("扩展消息转化器");
        //创建消息转换器对象
        MappingJackson2HttpMessageConverter messageConverter = new MappingJackson2HttpMessageConverter();

        //设置对象转换器，底层使用Jackson将Java对象转为json
        messageConverter.setObjectMapper(new JacksonObjectMapper());

        //将上面的消息转换器对象追加到mvc框架的转换器集合中,要将这个设置在前面,容器会先执行这个转化器
        converters.add(0,messageConverter);
    }

    //注册拦截器
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new MyHandlerInterceptor()).addPathPatterns("/**")  //addPathPatterns() 添加需要拦截的拦截路径
                .excludePathPatterns("/employee/login","/employee/logout","/backend/**","/front/**","/user/login","/user/sendMsg"); //excludePathPatterns()  表示那些请求不需要拦截,可以放行

    }
    //第二种：静态资源的映射路径方式
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/backend/**").addResourceLocations("classpath:/static/backend/");
        registry.addResourceHandler("/front/**").addResourceLocations("classpath:/static//front/");
    }
}
