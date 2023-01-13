package com.atming.reggie.controller;

import com.atming.reggie.common.R;
import com.atming.reggie.entity.Category;
import com.atming.reggie.entity.Employee;
import com.atming.reggie.entity.Orders;
import com.atming.reggie.service.CategoryService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @CreateTime: 2022-11-11-15:04
 * @Author: Hello77
 * @toUser:
 * @note:
 */
@RestController
@Slf4j
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    CategoryService categoryService;


    @GetMapping("/page")
    public R<Page> getCategory(@RequestParam("page") Integer page,@RequestParam("pageSize") Integer pageSize){

        log.info("传入的页面页码是：{}，每页显示的条数是：{}",page,pageSize);

        //创建mybatisplus实现分页查询的对象,在构造器中传入需要查询的数据的当前页面的页码以及每一页显示的数据条数
        Page<Category> categoryPage = new Page<>(page,pageSize);

        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(Category::getCreateTime);

        categoryPage = categoryService.page(categoryPage,queryWrapper);

        if (categoryPage != null) {
            return R.success(categoryPage);
        }

        return R.error(null);
    }


    /**
     *  添加菜单分类以及套餐分类：
     *      其中：选择菜单分类的type为 1
     *            选择套餐分类的type为 2
     * @param category
     * @return
     */
    @PostMapping
    public R<String> insertCategory(@RequestBody Category category){
        log.info("传入的菜品的名字：{}",category.getName());

        boolean save = categoryService.save(category);

        if (save){
            return R.success("数据添加成功");
        }

        return R.error("数据添加失败");
    }

    /**
     *  用户修改菜品的名称以及排序
     * @param category
     * @return
     */
    @PutMapping
    public R<String> updateCategory(@RequestBody Category category){

        log.info("用户准备修改的菜品名称为：{}",category.getName());

        boolean update = categoryService.updateById(category);

        if (update){
            return R.success("修改成功");
        }
        return R.error("菜品修改失败");
    }


    /**
     * 根据id删除菜单的操作
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> delete(@RequestParam("ids") Long ids){
        log.info("需要删除的菜品的id为：{}",ids);
        //categoryService.removeById(ids);

        categoryService.remove(ids);

        return R.success("分类信息删除成功");

    }

    /**
     * 根据传入的分类类型来查找指定的分类数据
     * @param category
     * @return
     */
    @GetMapping("/list")
    public R<List<Category>> selectCategoryName(Category category){
        log.info("要查询的菜品类型是：{}",category.getType());

        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.eq(category.getType() != null,Category::getType,category.getType());

        queryWrapper.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);

        List<Category> categories = categoryService.list(queryWrapper);

        return R.success(categories);
    }

}
