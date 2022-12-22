package com.atming.reggie.service;

import com.atming.reggie.dto.SetmealDto;
import com.atming.reggie.entity.Setmeal;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author karlo
* @description 针对表【setmeal(套餐)】的数据库操作Service
* @createDate 2022-11-11 16:52:02
*/
public interface SetmealService extends IService<Setmeal> {

    /**
     * 修改套餐的数据
     * @param setmealDto
     */
    public void updateData(SetmealDto setmealDto);

    /**
     * 删除指定的id的谈套餐信息
     * @param ids
     */
    public void deleteSetmealWithDish(List<Long> ids);

}
