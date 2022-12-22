package com.atming.reggie.component;

import com.atming.reggie.common.BaseContext;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.Date;

/**
 * @CreateTime: 2022-11-10-21:34
 * @Author: Hello77
 * @toUser:
 * @note: mybatisplus提供的可以完成自动填充功能的类，需要实现MetaObjectHandler接口,实现两个方法insertFill和updateFill
 *
 *
 *      insertFill：执行添加功能的时候,会调用这个方法对需要自动填充的字段进行自动填充
 *
 *      updateFill：执行更新功能的时候,会调用这个方法对需要自动填充的字段进行自动填充
 */
@Slf4j
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
        log.info("调用这个方法是：{}",metaObject.toString());

        Date date = new Date();
        Timestamp timestamp = new Timestamp(date.getTime());

        //对数据库进行自动填充
        metaObject.setValue("createTime",timestamp);
        metaObject.setValue("updateTime",timestamp);
        metaObject.setValue("createUser", BaseContext.getCurrentId());
        metaObject.setValue("updateUser",BaseContext.getCurrentId());

    }

    @Override
    public void updateFill(MetaObject metaObject) {
        log.info("调用这个方法是：{}",metaObject.toString());

        Date date = new Date();
        Timestamp timestamp = new Timestamp(date.getTime());

        metaObject.setValue("updateTime",timestamp);
        metaObject.setValue("updateUser",BaseContext.getCurrentId());
    }
}
