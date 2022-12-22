package com.atming.reggie.service.impl;

import com.atming.reggie.component.CustomException;
import com.atming.reggie.dto.SetmealDto;
import com.atming.reggie.entity.SetmealDish;
import com.atming.reggie.service.SetmealDishService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atming.reggie.entity.Setmeal;
import com.atming.reggie.service.SetmealService;
import com.atming.reggie.mapper.SetmealMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
* @author karlo
* @description 针对表【setmeal(套餐)】的数据库操作Service实现
* @createDate 2022-11-11 16:52:02
*/
@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal>
    implements SetmealService{

    @Autowired
    SetmealDishService setmealDishService;

    @Override
    public void updateData(SetmealDto setmealDto) {
        //修改普通的套餐数据,直接去setmeal表中去修改

        this.updateById(setmealDto);

        //取出setmealDishes的对象集合
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();

        //获取setmealId
        String setmealId = String.valueOf(setmealDto.getId());

        for (SetmealDish setmealdish :
                setmealDishes) {
            setmealdish.setSetmealId(setmealId);

            //获取到要添加的dishid
            String dishId = setmealdish.getDishId();

            //查找这个数据中时候有这个dishid的数据
            LambdaQueryWrapper<SetmealDish> selectQueryWrapper = new LambdaQueryWrapper<>();
            selectQueryWrapper.eq(SetmealDish::getDishId,dishId);
            int count = setmealDishService.count(selectQueryWrapper);

            //判断数据库中是否有该dishid的值,有值就执行修改,无值执行添加
            if (count != 0) {
                LambdaUpdateWrapper<SetmealDish> setmealDishLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
                setmealDishLambdaUpdateWrapper.eq(SetmealDish::getDishId, setmealdish.getDishId());

                setmealDishService.update(setmealdish, setmealDishLambdaUpdateWrapper);
            }else {
                setmealDishService.save(setmealdish);
            }
        }


        /*//根据这个套餐id来获取之前存入的菜品有那些
        LambdaQueryWrapper<SetmealDish> setmealDishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealDishLambdaQueryWrapper.eq(SetmealDish::getSetmealId,setmealId);
        List<SetmealDish> list = setmealDishService.list(setmealDishLambdaQueryWrapper);*/

        /*for (SetmealDish dish :
                list) {
            //获取到原来表中每一个菜品的id
            String dishId = dish.getDishId();

            for (SetmealDish dishNow :
                    setmealDishes) {
                //新的每一个菜品的id
                String dishIdNow = dishNow.getDishId();
            }

        }*/

        //修改setmealdish表中的数据,要根据传进来的dish_id进行修改

        //若dish_id在表中没有,则要在表中加入这个菜品,

        //若表中有这个菜品,则是修改这个菜品的信息


        /*//获取setmealId
        String setmealId = String.valueOf(setmealDto.getId());

        //但是在封装setmealdishes数据的时候,并没有将setmealId传入,我们要为每一个setmealdishes对象添加中setmealid

        //修改setmealdishes数据
        for (SetmealDish setmealDish :
                setmealDishes) {
            setmealDish.setSetmealId(setmealId);

            LambdaUpdateWrapper<SetmealDish> dishLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
            dishLambdaUpdateWrapper.eq(SetmealDish::getSetmealId,setmealId);

            //修改套餐中菜品的数据,修改setmealdishes表中的数据,要根据setmealId来修改
            setmealDishService.update(setmealDish,dishLambdaUpdateWrapper);
        }

        *//*setmealDishes.stream().map((item) ->{
            item.setSetmealId(setmealId);

            LambdaUpdateWrapper<SetmealDish> dishLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
            dishLambdaUpdateWrapper.eq(SetmealDish::getSetmealId,setmealId);
            //修改套餐中菜品的数据,修改setmealdishes表中的数据,要根据setmealId来修改
            setmealDishService.update(item,dishLambdaUpdateWrapper);

            return
        });*/
    }

    @Override
    public void deleteSetmealWithDish(List<Long> ids) {

        //判断这个套餐是否是起售状态,若当前为起售状态,则不能删除,抛出一个异常
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        //判断数据库中是否有id和传入的id相同的并且他的状态为1的
        setmealLambdaQueryWrapper.in(Setmeal::getId,ids).eq(Setmeal::getStatus,1);
        //SELECT COUNT( * ) FROM setmeal WHERE (id IN (?) AND status = ?)
        int count = this.count(setmealLambdaQueryWrapper);

        if (count > 0){
            throw new CustomException("该套餐为起售状态,不能删除");
        }

        //若为禁售状态,则可以删除,先删除setmeal表中的相关套餐信息,
        this.removeByIds(ids);

        //在删除setmealdish表中的信息,以setmealid为删除条件
        LambdaQueryWrapper<SetmealDish> setmealDishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealDishLambdaQueryWrapper.in(SetmealDish::getSetmealId,ids);
        setmealDishService.remove(setmealDishLambdaQueryWrapper);

    }
}




