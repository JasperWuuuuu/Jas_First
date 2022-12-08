package com.jas.takeaway.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jas.takeaway.common.R;
import com.jas.takeaway.entity.Employee;
import com.jas.takeaway.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.tomcat.jni.Local;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;

    /**
     * 員工登陸
     * @param employee
     * @return
     */
    @PostMapping("/login")
    public R<Employee> login(@RequestBody Employee employee, HttpServletRequest request){
        //1 將頁面提交的password進行MD5加密處理
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        //2 根據頁面提交的用戶名username查詢數據庫
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername,employee.getUsername());
        Employee emp = employeeService.getOne(queryWrapper); //因為數據庫中username字段是unique唯一的

        //3 如果沒有查詢到則返回登陸失敗
        if(emp == null){
            return R.error("登陸失敗");
        }

        //4 密碼比對，若密碼錯誤返回登陸失敗
        if(!emp.getPassword().equals(password)){
            return R.error("登陸失敗");
        }

        //5 查看員工狀態，若員工禁用則返回已禁用的結果
        if(emp.getStatus() == 0){
            return R.error("帳戶已禁用");
        }
        
        //6 登陸成功，將員工id保存到session中並返回登陸成功
        request.getSession().setAttribute("employee",emp.getId());
        return R.success(emp);
    }

    /**
     * 員工退出
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request){
        //清理session中保存的當前員工id
        request.getSession().removeAttribute("employee");
        return R.success("退出成功");
    }

    /**
     * 新增員工
     * @param employee
     * @return
     */
    @PostMapping
    public R<String> save(HttpServletRequest request,@RequestBody Employee employee){
        log.info("新增員工信息：{}",employee.toString());
        //設置初始密碼123456，但是需要md5加密
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));

        //employee.setCreateTime(LocalDateTime.now());
        //employee.setUpdateTime(LocalDateTime.now());

        //獲取當前登陸用戶id來作為創建人信息
        //Long empId = (Long)request.getSession().getAttribute("employee");
        //employee.setCreateUser(empId);
        //employee.setUpdateUser(empId);

        employeeService.save(employee);

        return R.success("成功新增員工");
    }

    /**
     * 員工信息分頁查詢
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){
        log.info("page={},pageSize={},name={}",page,pageSize,name);
        //構造分頁構造器
        Page pageInfo = new Page(page,pageSize);
        //構造條件構造器
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        //添加過濾條件
        queryWrapper.like(StringUtils.isNotEmpty(name),Employee::getName,name);
        //添加排序條件
        queryWrapper.orderByDesc(Employee::getUpdateTime);
        //執行查詢
        employeeService.page(pageInfo,queryWrapper);
        return R.success(pageInfo);
    }

    /**
     * 根據員工id去更新員工信息
     * @param employee
     * @return
     */
    @PutMapping
    public R<String> update(HttpServletRequest request,@RequestBody Employee employee){
        log.info(employee.toString());

        long id = Thread.currentThread().getId();
        log.info("線程id為：{}",id);
//        Long empId = (Long)request.getSession().getAttribute("employee");
//        employee.setUpdateTime(LocalDateTime.now());
//        employee.setUpdateUser(empId);
        employeeService.updateById(employee);
        return R.success("員工信息修改成功");
    }

    /**
     * 根據id查詢員工信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    //@PathVariable 路徑變量，說明id是在整個請求路徑當中
    public R<Employee> getById(@PathVariable Long id){
        //根據員工id查詢信息
        Employee employee = employeeService.getById(id);
        if(employee != null){
            return R.success(employee);
        }
        return R.error("沒有查詢到對應員工信息");
    }
}
