package com.jas.takeaway.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jas.takeaway.dto.DishDto;
import com.jas.takeaway.entity.Dish;

public interface DishService extends IService<Dish> {

    //新增菜品，同時插入菜品對應口味的數據，要操作dish和dish_flavor兩張表
    //因為頁面提交的JSON數據都封裝到DishDto中
    public void saveWithFlavor(DishDto dishDto);

    //根據id來查詢菜品信息和對應的口味信息
    public DishDto getByIdWithFlavor(Long id);

    //更新菜品信息，同時更新對應的口味信息
    public void updateWithFlavor(DishDto dishDto);
}
