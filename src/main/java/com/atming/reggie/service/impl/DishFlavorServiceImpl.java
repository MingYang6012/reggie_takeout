package com.atming.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atming.reggie.entity.DishFlavor;
import com.atming.reggie.service.DishFlavorService;
import com.atming.reggie.mapper.DishFlavorMapper;
import org.springframework.stereotype.Service;

/**
* @author karlo
* @description 针对表【dish_flavor(菜品口味关系表)】的数据库操作Service实现
* @createDate 2022-11-12 19:15:19
*/
@Service
public class DishFlavorServiceImpl extends ServiceImpl<DishFlavorMapper, DishFlavor>
    implements DishFlavorService{

}




