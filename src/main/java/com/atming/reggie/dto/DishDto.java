package com.atming.reggie.dto;

import com.atming.reggie.entity.Dish;
import com.atming.reggie.entity.DishFlavor;
import com.atming.reggie.entity.Dish;
import com.atming.reggie.entity.DishFlavor;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

/**
 *  dto : Data Transfer object 数据传输对象
 */
@Data
public class DishDto extends Dish {

    private List<DishFlavor> flavors = new ArrayList<>();

    private String categoryName;

    private Integer copies;
}
