package com.jas.takeaway.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jas.takeaway.entity.OrderDetail;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderDetailMapper extends BaseMapper<OrderDetail> {
}
