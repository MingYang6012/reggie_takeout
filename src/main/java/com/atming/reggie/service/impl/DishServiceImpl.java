package com.atming.reggie.service.impl;

import com.atming.reggie.dto.DishDto;
import com.atming.reggie.entity.DishFlavor;
import com.atming.reggie.service.DishFlavorService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atming.reggie.entity.Dish;
import com.atming.reggie.service.DishService;
import com.atming.reggie.mapper.DishMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
* @author karlo
* @description 针对表【dish(菜品管理)】的数据库操作Service实现
* @createDate 2022-11-11 16:51:54
*/
@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish>
    implements DishService{

    @Autowired
    private DishFlavorService dishFlavorService;

    /**
     * 这个service方法可以将dish数据保存到dish表中同时可以将dishflavor数据保存到dishflavor表中
     * 涉及到多表的连接,在使用上数据库事务
     */
    @Transactional
    @Override
    public void saveDishAndFlavor(DishDto dishDto) {
        //将菜品的基本信息保存到菜品表dish中
        this.save(dishDto);

        //获取菜品的id
        Long id = dishDto.getId();

        //获取dishFlavor的数据,但是此时dishflavoer对象中dishId属性并没有赋值,这个时候就需要我们对这个对象进行处理
        List<DishFlavor> flavors = dishDto.getFlavors();

        //处理DishFlavor对象
        flavors = flavors.stream().map((item) ->{
            //对DishFlavor每一个对象都附上dishid的值
            item.setDishId(id);
            //返回DishFlavor的对象
            return item;
        }).collect(Collectors.toList());

        //保存dishflavor数据到dishflavor表中
        dishFlavorService.saveBatch(flavors);
    }

    @Override
    public int updateDishAndFlavor(DishDto dishDto) {

        //对dish表中的数据进行修改

        super.updateById(dishDto);

        //获取dish_id
        Long id = dishDto.getId();

        //获取每一个要修改的dish_flavor的对象
        List<DishFlavor> flavors = dishDto.getFlavors();

        for (DishFlavor dishFlavor :
                flavors) {
            if (dishFlavor.getDishId() == null) {
                //若等于null,说明是新添加的数据,这个时候需要使用添加的方法
                //将dishid设置进dishflavor对象中
                dishFlavor.setDishId(id);
                //将dishflavor对象存入数据库中
                dishFlavorService.save(dishFlavor);
            }else {
                //否则是之前就存过但是只是修改,这个时候根据dishid进行修改就可
                LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(DishFlavor::getDishId,id);
                dishFlavorService.update(dishFlavor,queryWrapper);
            }
            }

       /* flavors = flavors.stream().map((item) ->{
            //获取每一个dishflavor的对象,将dish_id的值赋值给dishfalvor对象
            item.setDishId(id);

            //返回赋值后的dishflavor的对象
            return item;
        }).collect(Collectors.toList());*/

        /*LambdaQueryWrapper<DishFlavor> dishFlavorLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishFlavorLambdaQueryWrapper.eq(DishFlavor::getDishId,id);

        //对dishflavor表中的数据进行修改，根据dish_id来修改dishflavor对象的值
        boolean b = dishFlavorService.updateBatchById(flavors);
*/
        /*if (b) {

            return 1;
        }*/
        return 1;
    }
}




