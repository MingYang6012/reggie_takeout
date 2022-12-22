package com.atming.reggie.controller;

import com.atming.reggie.common.R;
import com.atming.reggie.entity.Orders;
import com.atming.reggie.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

}
