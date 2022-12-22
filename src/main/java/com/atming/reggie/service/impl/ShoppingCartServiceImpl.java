package com.atming.reggie.service.impl;

import com.atming.reggie.entity.ShoppingCart;
import com.atming.reggie.mapper.ShoppingCartMapper;
import com.atming.reggie.service.ShoppingCartService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService {

}
