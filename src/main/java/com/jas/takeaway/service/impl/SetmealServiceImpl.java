package com.jas.takeaway.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jas.takeaway.common.CustomException;
import com.jas.takeaway.dto.SetmealDto;
import com.jas.takeaway.entity.Setmeal;
import com.jas.takeaway.entity.SetmealDish;
import com.jas.takeaway.mapper.SetmealMapper;
import com.jas.takeaway.service.SetmealDishService;
import com.jas.takeaway.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {

    @Autowired
    private SetmealDishService setmealDishService;

    //新增套餐，同时需要保存套餐和菜品关联关系
    @Override
    @Transactional
    public void saveWithDish(SetmealDto setmealDto) {
        //保存套餐基本信息，操作setmeal表 -- insert操作
        this.save(setmealDto);
        //獲取關聯關係的集合，但是要注意dishId是有值的，但是setmealId是沒有值的
        //所以保存setmealDishes之前要遍歷它给null賦值(因為只有前端傳過來的值才是有的)
        //原因是關聯套餐信息都沒有插入，雪花算法的setmealId肯定就沒有生成
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes.stream().map((item) -> {
            item.setSetmealId(setmealDto.getId()); //setmealDto已經存入表了，所以有值
            return item;
        }).collect(Collectors.toList());
        //保存套餐和菜品关联信息，操作setmeal_dish表 -- insert操作
        //注意要先增加SetmealDishService
        setmealDishService.saveBatch(setmealDishes);

    }

    //刪除套餐，同時要傳輸套餐和菜品的關聯數據
    @Override
    @Transactional
    public void removeWithDish(List<Long> ids) {
        // select count(*) from setmeal where id in (1,2,3) and status = 1
        //查詢套餐狀態（在售/停售），判斷是否可以刪除
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Setmeal::getId,ids);
        queryWrapper.eq(Setmeal::getStatus,1);
        int count = this.count(queryWrapper);

        //若不能刪除則拋出業務異常
        if(count > 0){
            throw new CustomException("套餐正在售賣中,无法删除");
        }

        //若可以刪除，先刪除套餐表中的數據 -- setmeal，然後再刪除關係表中的數據 -- setmeal_dish
        this.removeByIds(ids);

        //delete from setmeal_dish where setmeal_id in (1,2,3)
        LambdaQueryWrapper<SetmealDish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(SetmealDish::getSetmealId,ids);
        setmealDishService.remove(lambdaQueryWrapper);
    }
}
