package com.atming.reggie.service.impl;

import com.atming.reggie.entity.OrderDetail;
import com.atming.reggie.mapper.OrderDetailMapper;
import com.atming.reggie.service.OrderDetailService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail> implements OrderDetailService {

}