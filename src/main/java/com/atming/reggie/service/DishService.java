package com.atming.reggie.service;

import com.atming.reggie.dto.DishDto;
import com.atming.reggie.entity.Dish;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author karlo
* @description 针对表【dish(菜品管理)】的数据库操作Service
* @createDate 2022-11-11 16:51:54
*/
public interface DishService extends IService<Dish> {

    /**
     * 保存菜品信息
     * @param dishDto
     */
    public void saveDishAndFlavor(DishDto dishDto);

    /**
     * 修改菜品信息
     * @param dishDto
     */
    public int updateDishAndFlavor(DishDto dishDto);

}
