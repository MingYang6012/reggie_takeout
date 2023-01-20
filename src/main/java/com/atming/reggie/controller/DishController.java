package com.atming.reggie.controller;

import com.atming.reggie.common.R;
import com.atming.reggie.dto.DishDto;
import com.atming.reggie.entity.Category;
import com.atming.reggie.entity.Dish;
import com.atming.reggie.entity.DishFlavor;
import com.atming.reggie.service.CategoryService;
import com.atming.reggie.service.DishFlavorService;
import com.atming.reggie.service.DishService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.awt.im.InputMethodHighlight;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @CreateTime: 2022-11-12-19:18
 * @Author: Hello77
 * @toUser:
 * @note:
 */
@RestController
@Slf4j
@RequestMapping("/dish")
public class DishController {


    @Autowired
    private DishService dishService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private DishFlavorService dishFlavorService;

    //注入RedisTemplate的对象实现数据缓存
    @Autowired
    private RedisTemplate<Object,Object> redisTemplate;

    /**
     * 需要解决的问题：
     * 当使用普通的分页查询从dish表中查询数据的时候，只能查询到category_id的数据,而页面需要category_name的数据来展示在浏览器中，
     * 所以这个时候,在页面的菜品分类的栏中,就无法显示数据
     * <p>
     * 解决办法：
     * 根据查询到的 category_id 到 category 表中根据id来查询响应的菜品的名称,同时封装到 DishDto类的对象中,返回给浏览器
     *
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> selectPage(@RequestParam("page") Integer page,
                              @RequestParam("pageSize") Integer pageSize,
                              @RequestParam(value = "name", defaultValue = "") String name) {
        log.info("传入的page= {}，pageSize= {},name= {}", page, pageSize, name);

        //创建分页的对象
        Page<Dish> dishPage = new Page<>(page, pageSize);
        //数据库中并没有DishDto表,所以不能将Page<DishDto> 当做对象放到数据库中进行查询
        Page<DishDto> dishDtoPage = new Page<>(page, pageSize);

        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        //根据用户输入的名称查询,并且根据菜品的价格排序升序,价格相同,根据修改时间从高到低降序
        queryWrapper.like(name != null, Dish::getName, name).orderByAsc(Dish::getPrice).orderByDesc(Dish::getUpdateTime);

        dishPage = dishService.page(dishPage, queryWrapper);

        //使用BeanUtils工具类下面的copyProperties方法,将查询到的dishPage对象中的数据复制到dishDtoPage对象中,但是 records属性不复制
        //records : 封装了查询到的数据库中真正的数据,但是此时的数据中并没有categoryname数据，所以不能复制
        BeanUtils.copyProperties(dishPage, dishDtoPage, "records");

        //获取到在分页对象中封装的数据集合
        List<Dish> records = dishPage.getRecords();

        //Lambda表达式,遍历集合,集合中每个数据对象为item,item为具体的某一个数据
        //在方法体重,将item(Dish对象)都copy到每一个dishDto对象中,并且查询到每一个category_id对应的category_name封装到dishDto对象中，并且返回
        List<DishDto> list = records.stream().map((item) -> {

            DishDto dishDto = new DishDto();

            //将每一个dish对象的值都copy到dishdao对象中
            BeanUtils.copyProperties(item, dishDto);

            //获取到数据的category_id
            Long categoryId = item.getCategoryId();

            //根据category_id去category表中找到每一个id对应的对象
            Category category = categoryService.getById(categoryId);

            //获取categoryname
            String categoryName = category.getName();

            //设置dishDto对象中categoryName属性的值
            dishDto.setCategoryName(categoryName);

            //这个时候的dishDto对象中即封装了每一个dish对象的值,还封装了categoryName属性的值
            return dishDto;
        }).collect(Collectors.toList());

        //将保存dishDto对象的集合放入dishDtoPage的分页对象中
        dishDtoPage.setRecords(list);

        //返回这个分页对象
        return R.success(dishDtoPage);
    }

    /**
     * 完成菜品保存的操作
     *
     * @param dishDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto) {

        log.info("当前传入的菜品的名称:{},以及当前菜品分类的id：{},以及当前dishflavor的name:{}", dishDto.getName(), dishDto.getCategoryId(), dishDto.getFlavors());


        dishService.saveDishAndFlavor(dishDto);
        //在添加数据后,要删除redis的缓存,保证数据的一致性
        String key = dishDto.getCategoryId() + "_1" ;
        redisTemplate.delete(key); //在用户自行插入操作前,向将redis中的和这个数据有关的缓存清空
        return R.success("数据存储成功");
    }


    /**
     * 这个方法是数据回显, 要将传入的dishid对象dish表以及dishflavor表中相同的dishid对应的数据返回
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<DishDto> getDishAndFlavor(@PathVariable Long id) {
        log.info("要回显数据的dishid是：{}", id);
        //找出dish表中指定的dish_id对应的数据
        Dish dishData = dishService.getById(id);

        //找出dishflavor表中指定的dish_id对应的数据
        LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        //设置dish_id为指定的查找条件
        lambdaQueryWrapper.eq(DishFlavor::getDishId, id);
        //指定的dish_id对应的dishflavor对象会有多个，返回一个集合
        List<DishFlavor> flavors = dishFlavorService.list(lambdaQueryWrapper);


        DishDto dishDto = new DishDto();
        //将查找到的dish对象复制到dishDto对象中
        BeanUtils.copyProperties(dishData, dishDto);
        //在将flavors对象设置进dishDto中
        dishDto.setFlavors(flavors);

        //将dishDto对象返回
        return R.success(dishDto);
    }

    /**
     * 接受到用户要修改的菜品数据,同时对dish表以及dishflavor表中的数据进行修改
     *
     * @param dishDto
     * @return
     */
    @PutMapping
    public R<String> updateDishAndFlavor(@RequestBody DishDto dishDto) {
        log.info("获取到要修改的菜品的名称:{}", dishDto.getName());
        int count = dishService.updateDishAndFlavor(dishDto);
        //在添加数据后,要删除redis的缓存,保证数据的一致性
        String key = dishDto.getCategoryId() + "_1" ;
        redisTemplate.delete(key); //在用户自行插入操作前,向将redis中的和这个数据有关的缓存清空

        if (count != 0) {
            return R.success("数据修改成功");
        }

        return R.error("数据修改失败");
    }

    /**
     * 根据用户传入的菜品id来删除菜品.同时删除这个菜品的口味，可以完成批量删除菜品功能
     *
     * @param list 传入的要删除菜品的集合
     * @return
     */
    @DeleteMapping
    public R<String> deleteDishAndFlavor(@RequestParam("ids") List<Long> list) {
        log.info("要批量删除的菜品的id是:{}",list);

        //批量删除菜品的功能
        for (Long ids :
                list) {
            //根据dishid要删除两个表中的所有数据
            dishService.removeById(ids);

            LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(DishFlavor::getDishId,ids);

            dishFlavorService.remove(queryWrapper);
        }


        //根据菜品id进行删除菜品,这个是实现单个菜品的删除
        /*dishService.removeById(list);

        //在根据菜品id来删除dishflavor中的数据
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId, list);
        dishFlavorService.remove(queryWrapper);*/

        return R.success("删除菜品成功");
    }

    /**
     * 即可以完成对单个菜品的停售以及起售,可以完成批量的对数据的停售以及起售
     *
     * @param status
     * @param list
     * @return
     */
    @PostMapping("/status/{status}")
    public R<String> updateStatus(@PathVariable int status, @RequestParam("ids") List<Long> list) {
        log.info("获取到要修改状态的菜品id是：{},以及修改到什么状态:{}", list, status);

        //下面是通用操作
        for (long ids :
                list) {
            LambdaUpdateWrapper<Dish> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.set(Dish::getStatus, status).eq(Dish::getId, ids);

            dishService.update(updateWrapper);
        }

        return R.success("数据更新成功");


        //下面是对单个菜品的操作
        /*LambdaUpdateWrapper<Dish> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(Dish::getStatus,status);
        updateWrapper.eq(Dish::getId,list);
        boolean update = dishService.update(updateWrapper);
        if (update){
            return R.success("状态修改成功");
        }
        return R.error("状态为修改成功");
    }*/
    }

    /**
     * 根据用户传入的菜品id来查询对应的菜品信息,包括菜品口味信息
     * @param
     * @return
     */
    @GetMapping("/list")
    //public R<List<DishDto>> getDishList(@RequestParam("categoryId") Long categoryId){
    public R<List<DishDto>> getDishList(Dish dish){

        log.info("要查询的category的id是：{}",dish.getCategoryId());

        List<DishDto> dishDtos = null;

        String key = dish.getCategoryId() + "_" + dish.getStatus();

        //每次在用户查询菜品的时候,都要先从redis缓存中查询有没有数据
        //如果redis缓存中有这个key的数据,就从缓存中取出这个数据然后返回
        dishDtos = (List<DishDto>) redisTemplate.opsForValue().get(key);//从redis缓存中获取数据,指定key的数据

        if (dishDtos != null){
            return R.success(dishDtos); //如果不为空,就返回
        }



        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Dish::getCategoryId,dish.getCategoryId());
        queryWrapper.eq(Dish::getStatus,1);

        List<Dish> list = dishService.list(queryWrapper);

        dishDtos = list.stream().map((item) -> {

            DishDto dishDto = new DishDto();

            //将每一个dish对象的值都copy到dishdao对象中
            BeanUtils.copyProperties(item, dishDto);

            LambdaQueryWrapper<DishFlavor> dishFlavorLambdaQueryWrapper = new LambdaQueryWrapper<>();
            dishFlavorLambdaQueryWrapper.eq(dish.getCategoryId() != null,DishFlavor::getDishId,item.getId());

            List<DishFlavor> dishFlavors = dishFlavorService.list(dishFlavorLambdaQueryWrapper);

            dishDto.setFlavors(dishFlavors);

            //这个时候的dishDto对象中即封装了每一个dish对象的值,还封装了categoryName属性的值
            return dishDto;
        }).collect(Collectors.toList());

        //如果redis缓存中没有这个数据,就从数据库中查询数据然后返回,然后将查询到的数据放入redis中进行缓存
        redisTemplate.opsForValue().set(key,dishDtos,30*60, TimeUnit.MINUTES); //设置过期时间

        return R.success(dishDtos);

    }




    /*@GetMapping("/list")
    public R<List<Dish>> getDishList(@RequestParam("categoryId") Long categoryId){

        log.info("要查询的category的id是：{}",categoryId);

        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Dish::getCategoryId,categoryId);

        List<Dish> list = dishService.list(queryWrapper);

        return R.success(list);

    }*/

}
