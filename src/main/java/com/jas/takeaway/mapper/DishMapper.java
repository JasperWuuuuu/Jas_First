package com.jas.takeaway.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jas.takeaway.entity.Dish;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DishMapper extends BaseMapper<Dish> {
}
