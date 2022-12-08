package com.jas.takeaway.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jas.takeaway.entity.Employee;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {
}
