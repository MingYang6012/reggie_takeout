package com.atming.reggie.dto;

import com.atming.reggie.entity.Setmeal;
import com.atming.reggie.entity.SetmealDish;
import com.atming.reggie.entity.Setmeal;
import com.atming.reggie.entity.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
