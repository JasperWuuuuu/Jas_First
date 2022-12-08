package com.jas.takeaway.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jas.takeaway.common.CustomException;
import com.jas.takeaway.entity.Category;
import com.jas.takeaway.entity.Dish;
import com.jas.takeaway.entity.Setmeal;
import com.jas.takeaway.mapper.CategoryMapper;
import com.jas.takeaway.service.CategoryService;
import com.jas.takeaway.service.DishService;
import com.jas.takeaway.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
    @Autowired
    private DishService dishService;
    @Autowired
    private SetmealService setmealService;

    /**
     * 根據id刪除分類，刪除之前需要判斷
     * @param id
     */
    @Override
    public void remove(Long id) {
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.eq(Dish::getCategoryId,id);
        int count1 = dishService.count(dishLambdaQueryWrapper);
        //1 查詢當前分類是否關聯了菜品，若關聯則拋出業務異常
        if(count1>0){
            throw new CustomException("當前分類下關聯了菜品，不能刪除");
        }
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId,id);
        int count2 = setmealService.count(setmealLambdaQueryWrapper);
        //2 查詢當前分類是否關聯了套餐，若關聯則拋出業務異常
        if(count2>0){
            throw new CustomException("當前分類下關聯了套餐，不能刪除");
        }
        //3 正常刪除分類
        super.removeById(id);
    }
}
