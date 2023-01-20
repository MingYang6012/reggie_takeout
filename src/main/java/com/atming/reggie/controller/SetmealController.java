package com.atming.reggie.controller;

import com.atming.reggie.common.R;
import com.atming.reggie.dto.SetmealDto;
import com.atming.reggie.entity.Category;
import com.atming.reggie.entity.Setmeal;
import com.atming.reggie.entity.SetmealDish;
import com.atming.reggie.service.CategoryService;
import com.atming.reggie.service.SetmealDishService;
import com.atming.reggie.service.SetmealService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @CreateTime: 2022-11-13-19:54
 * @Author: Hello77
 * @toUser:
 * @note:
 */
@RestController
@Slf4j
@RequestMapping("/setmeal")
public class SetmealController {

    @Autowired
    private SetmealService setmealService;

    @Autowired
    private SetmealDishService setmealDishService;

    @Autowired
    private CategoryService categoryService;

    /**
     * 新建套餐,给套餐加入基本信息,以及菜品信息
     * @param setmealDto
     * @return
     */
    @PostMapping
    @CacheEvict(value = "sermealCache",allEntries = true)
    public R<String> saveSetmealAndDish(@RequestBody SetmealDto setmealDto){
        log.info("获取的当前的套餐的信息");

        //将setmeal数据的基本信息储存到setmeal表中
        setmealService.save(setmealDto);

        //要将套餐中的菜品信息储存到setmealdish表中,但是在数据中setmealdishes集合中并没有封装setmeal_id的数据
        //获取到setmeal的id
        Long id = setmealDto.getId();

        //获取到封装在Dto对象中的setmealdishes对象
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();

        //因为setmealDishes对象中没有封装setmealid数据,这个时候要遍历每一个数据,在将每一个数据都设置setmealdishes数据
        setmealDishes = setmealDishes.stream().map((item) ->{
            String setmealId = String.valueOf(id);
            item.setSetmealId(setmealId);

            return item;

        }).collect(Collectors.toList());

        setmealDishService.saveBatch(setmealDishes);

        return R.success("数据储存成功");
    }

    /**
     *  分页查询套餐数据
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> getSetmealPage(@RequestParam("page") Integer page,
                                  @RequestParam("pageSize") Integer pageSize,
                                  @RequestParam(value = "name",defaultValue = "") String name){

        log.info("要查询的页码为：{},要查询的页面大小为:{},要的套餐名称为:{}",page,pageSize,name);

        //根据页码以及页面显示条数来创建Page对象
        Page<Setmeal> setmealPage = new Page<>(page,pageSize);
        Page<SetmealDto> setmealDtoPage = new Page<>(page, pageSize);
        

        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<Setmeal>();
        setmealLambdaQueryWrapper.like(name != null,Setmeal::getName,name);

        //执行查询操作,但是这个时候的查询操作中,并没有查询到categoryname属性的属性值,
        setmealPage = setmealService.page(setmealPage, setmealLambdaQueryWrapper);

        //将Setmeal数据通过分页查询到的赋值到Page<setmealDto>对象对，除了records属性中的值
        BeanUtils.copyProperties(setmealPage,setmealDtoPage,"records");

        //获取到setmealPage中records的值
        List<Setmeal> records = setmealPage.getRecords();
        
        //将每一个Setmeal对象遍历出来
        List<SetmealDto> setmealDtos = records.stream().map((item) -> {
            //穿件一个setmealDto对象
            SetmealDto setmealDto = new SetmealDto();
            
            //将Setmeal对象中的数据赋值到setmealDto对象中
            BeanUtils.copyProperties(item,setmealDto);
            
            //获取category_id
            Long categoryId = item.getCategoryId();

            //根据category_id去category表中查找指定的category对象
            Category category = categoryService.getById(categoryId);

            //获取到category_name的值
            String categoryName = category.getName();

            //将categoryName的值赋值到setmealDto对象中
            setmealDto.setCategoryName(categoryName);

            //返回这个对象
            return setmealDto;

        }).collect(Collectors.toList());

        //将加上了categoryname的records集合重新添加到页面对象中,
        setmealDtoPage.setRecords(setmealDtos);

        //要根据查询到的categoryid来在category表中,对categoryName属性进行查找,然后将属性值赋值到SetmealDto对象中；

        return R.success(setmealDtoPage);

    }

    /**
     * 将传入的指定的套餐套餐修改状态
     * @param status
     * @param ids
     * @return
     */
    @PostMapping("/status/{status}")
    public R<String> updateStatus(@PathVariable Integer status,
                                  @RequestParam("ids") List<Long> ids){
        log.info("要将状态修改为：{},要修改状态的套餐id为:{}",status,ids);



        for (Long id :
                ids) {
            LambdaUpdateWrapper<Setmeal> update = new LambdaUpdateWrapper<>();
            update.set(status != null,Setmeal::getStatus,status).eq(Setmeal::getId,id);

            //调用service方法来修改状态
           setmealService.update(update);
        }

        return R.success("套餐状态修改成功");
    }

    /**
     * 点击修改的时候,完成数据回显的功能
     *  同时要返回setmeanl的数据以及setmealdishes的数据,所以要使用到setmealDto对象,用来封装来个类的数据
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<SetmealDto> getSetmealAndDishes(@PathVariable Long id){

        log.info("要展示数据的套餐为:{}",id);

        //从setmeal表中根据id字段找出对应的数据

        Setmeal setmeal = setmealService.getById(id);

        //从setmealdishes表中根据setmealId找出对应的数据
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId,id);

        List<SetmealDish> list = setmealDishService.list(queryWrapper);

        //将两个数据封装到setmealDto对象中
        SetmealDto setmealDto = new SetmealDto();

        BeanUtils.copyProperties(setmeal,setmealDto);

        setmealDto.setSetmealDishes(list);

        //放回setmealDto对象
        return R.success(setmealDto);
    }

    /**
     * 修改套餐信息
     * @param setmealDto
     * @return
     */
    @PutMapping
    @CacheEvict(value = "sermealCache",allEntries = true)
    //在用户调用这个函数对数据库进行修改后,SpringCache框架会将用户修改的数据缓存在redis数据库中删除
    public R<String> updateData(@RequestBody SetmealDto setmealDto){
        log.info("要修改的套餐的基本信息是:{}",setmealDto.getName());
        setmealService.updateData(setmealDto);
        return R.success("套餐修改成功");
    }

    /**
     * 完成删除的操作,可以单个删除,也可以批量删除
     * @param ids
     * @return
     */
    @DeleteMapping
    @CacheEvict(value = "sermealCache",allEntries = true)
    public R<String> deleteSetmealWithDish(@RequestParam("ids") List<Long> ids){

        log.info("要删除的套餐的id是：{}",ids);

        setmealService.deleteSetmealWithDish(ids);

        return R.success("删除成功");
    }

    /**
     *  根据categoryid以及status信息来查询套餐的信息
     * @param setmeal
     * @return
     */
    @GetMapping("/list")
    //value属性,相当于redis键值对中的key,用来映射
    //key属性相当于redis中value数据类型为hash的数据理性,这里面存储的是value中的key的值,而value中的value才是要存储的值
    @Cacheable(value = "sermealCache",key = "#setmeal.categoryId + '_' + #setmeal.status")
    //使用注解的方式来进行数据缓存,用户在调用这个函数的时候,先会在redis数据库中进行查找数据key为sermealCache的数据是否存在,找到了这个key还会找
    //#setmeal.categoryId + '_' + #setmeal.status这个拼接的数据,如果redis中没有这个数据的缓存,就会执行接下来的函数调用数据库,然后将查找到的值返回给客户端
    //并且SpringCache框架会接收到SpringBoot返回的数据,并且存储到redis数据库中进行缓存
    public R<List<Setmeal>> selectSetmeal(Setmeal setmeal){
        log.info("要查询的套餐信息是:{}",setmeal);
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(setmeal.getCategoryId() != null,Setmeal::getCategoryId,setmeal.getCategoryId())
                        .eq(setmeal.getStatus() != null,Setmeal::getStatus,setmeal.getStatus());

        List<Setmeal> setmealData = setmealService.list(queryWrapper);

        return R.success(setmealData);
    }

}
