package com.jas.takeaway.dto;


import com.jas.takeaway.entity.Setmeal;
import com.jas.takeaway.entity.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
