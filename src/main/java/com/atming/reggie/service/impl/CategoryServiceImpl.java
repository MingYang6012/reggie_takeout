package com.atming.reggie.service.impl;

import com.atming.reggie.component.CustomException;
import com.atming.reggie.entity.Dish;
import com.atming.reggie.entity.Setmeal;
import com.atming.reggie.service.DishService;
import com.atming.reggie.service.SetmealService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atming.reggie.entity.Category;
import com.atming.reggie.service.CategoryService;
import com.atming.reggie.mapper.CategoryMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
* @author karlo
* @description 针对表【category(菜品及套餐分类)】的数据库操作Service实现
* @createDate 2022-11-11 15:02:58
*/
@Service
@Slf4j
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category>
    implements CategoryService{

    @Autowired
    DishService dishService;

    @Autowired
    SetmealService setmealService;

    /**
     * 实现一个自己的逻辑方法来执行对分类id的删除
     * @param id
     * @description :在删除一个分类前,要判断这个分类下面是否关联了菜品分类和套餐分类,如果有,则抛出一个异常,表示当前分类不能删除
     * @Caution : 抛出的异常要到全局异常处理器中进行捕获
     */
    @Override
    public void remove(Long id) {
        log.info("当前传入要删除分类的id是：{}",id);

        //找菜品是否有分类
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        //以id为条件在dish表中找是否有categoryid和这个值相同的
        dishLambdaQueryWrapper.eq(Dish::getCategoryId, id);

        int count1 = dishService.count(dishLambdaQueryWrapper);
        //判断在数据库中找出的这个categoryid是否有菜品,大于1表示有,则抛出异常
        if (count1 > 0){
            throw new CustomException("当前分类中存在菜品,不能删除");
        }

        //找套餐是否有分类
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();

        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId,id);

        int count2 = setmealService.count(setmealLambdaQueryWrapper);
        //如果当前count>0 表示当前分类中有套餐分类的存在,不能删除
        if (count2 > 0){
            //抛出异常
            throw new CustomException("当前分类中存在套餐管理,不能删除");
        }

        super.removeById(id);
    }
}




