package com.atming.reggie.controller;

import com.atming.reggie.common.BaseContext;
import com.atming.reggie.common.R;
import com.atming.reggie.entity.ShoppingCart;
import com.atming.reggie.service.ShoppingCartService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

/**
 * @CreateTime: 2022-12-11-17:29
 * @Author: Hello77
 * @toUser:
 * @note:
 */
@Slf4j
@RestController
@RequestMapping("/shoppingCart")
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    /**
     * 添加购物车的操作
     * @return
     */
    @RequestMapping("/add")
    public R<ShoppingCart> add(@RequestBody //标记用来接收json格式的数据
                                   ShoppingCart shoppingCart){
        log.info("接收到的菜品或套餐名称为：" +shoppingCart.getName() );

        //获取到当前用户的userId，并且将当前userId设置到shoppingCart对象中
        Long currentId = BaseContext.getCurrentId();
        shoppingCart.setUserId(currentId);

        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        //设置查询条件,设置要查询的为当前用户下的购物车信息
        queryWrapper.eq(ShoppingCart::getUserId,currentId);

        //判断要加入到购物车的是菜品信息还是套餐信息
        //判断当前userId的对象中是否有要添加的菜品或者套餐
        Long dishId = shoppingCart.getDishId();
        if (dishId != null){
            //要添加的是菜品的信息
            queryWrapper.eq(ShoppingCart::getDishId,dishId);
        }else {
            //要添加的是套餐的信息
            queryWrapper.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        }

        //数据库中有对应的数据,则在数据库中不添加一条新的数据,number+1
        ShoppingCart shoppingCartServiceOne = shoppingCartService.getOne(queryWrapper);
        if (shoppingCartServiceOne != null){
            Integer number = shoppingCartServiceOne.getNumber();
            number += 1;
            shoppingCartServiceOne.setNumber(number);
            shoppingCartService.updateById(shoppingCartServiceOne);
        }else {
            //数据库中没有数据，就在数据库中新添加一条数据
            shoppingCart.setNumber(1);
            /*Date date = new Date();
            Timestamp timestamp = new Timestamp(date.getTime());
            shoppingCart.setCreateTime(timestamp.toLocalDateTime());*/
            shoppingCartService.save(shoppingCart);
            shoppingCartServiceOne = shoppingCart;
        }
        //返回shopingcart的信息
        return R.success(shoppingCartServiceOne);
    }

    /**
     * 购物车查询数据
     * @return
     */
    @GetMapping("/list")
    public R<List<ShoppingCart>> listData(){
        //根据用户id来查询当前用户的购物车数据
        Long currentId = BaseContext.getCurrentId();

        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,currentId);

        List<ShoppingCart> list = shoppingCartService.list(queryWrapper);

        return R.success(list);
    }

    /**
     * 删除购物车中所有数据
     * @return
     */
    @DeleteMapping("/clean")
    public R<String> clean(){

        Long currentId = BaseContext.getCurrentId();

        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,currentId);

        shoppingCartService.remove(queryWrapper);

        return R.success("购物车删除成功");
    }




























}
