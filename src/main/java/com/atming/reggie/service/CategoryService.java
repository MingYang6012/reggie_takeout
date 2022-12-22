package com.atming.reggie.service;

import com.atming.reggie.entity.Category;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author karlo
* @description 针对表【category(菜品及套餐分类)】的数据库操作Service
* @createDate 2022-11-11 15:02:58
*/
public interface CategoryService extends IService<Category> {

    public void remove(Long id);

}
