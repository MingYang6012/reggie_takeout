package com.atming.reggie.controller;

import com.atming.reggie.common.R;
import com.atming.reggie.entity.Orders;
import com.atming.reggie.service.OrderService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @CreateTime: 2022-12-13-15:39
 * @Author: Hello77
 * @toUser:
 * @note:
 */
@Slf4j
@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders){
        log.info("输入的数据是：{}",orders);
        orderService.submit(orders);
        return R.success("订单下单成功");
    }

    @GetMapping("/page")
    public R<Page> pageToBack(@RequestParam Integer page,@RequestParam Integer pageSize){
        log.info("收到的pageSize的大小为：{}",pageSize);
        Page<Orders> orderPage =  new Page<Orders>(page,pageSize);

        orderPage = orderService.page(orderPage);

        if (orderPage != null){
            return R.success(orderPage);
        }

        return R.error("为查到相关数据");
    }

    //修改订单的目标状态
    @PutMapping
    public R<String> modifyStatus(@RequestBody Orders orders){


        boolean updateById = orderService.updateById(orders);

        if (updateById){
            return R.success("订单修改成功");
        }else {
            return R.error("订单修改失败");
        }

    }

    /**
     * 进行分页查询,将查询到的数据存入其中
     * @return
     */
    @GetMapping("/userPage")
    public R<Page> getOrder(@RequestParam("page") int page,@RequestParam("pageSize") int pageSize){
        log.info("传入的page为 {}",page);
        Page<Orders> ordersPage = new Page<>(page, pageSize);

        LambdaQueryWrapper<Orders> ordersLambdaQueryWrapper = new LambdaQueryWrapper<>();

        ordersPage = orderService.page(ordersPage, ordersLambdaQueryWrapper);

        if (ordersPage != null){
            return R.success(ordersPage);
        }

        return R.error("未查到数据");
    }

}
