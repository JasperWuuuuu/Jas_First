package com.jas.takeaway.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jas.takeaway.dto.SetmealDto;
import com.jas.takeaway.entity.Setmeal;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {
    //新增套餐，同时需要保存套餐和菜品关联关系
    public void saveWithDish(SetmealDto setmealDto);

    //刪除套餐，同時要傳輸套餐和菜品的關聯數據
    public void removeWithDish(List<Long> ids);
}
