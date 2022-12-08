package com.jas.takeaway.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jas.takeaway.common.R;
import com.jas.takeaway.entity.Category;
import com.jas.takeaway.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 分類管理
 */
@RestController
@RequestMapping("/category")
@Slf4j
public class CategoryController  {
    @Autowired
    private CategoryService categoryService;

    /**
     * 新增分類
     * @param category
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody Category category){
        log.info("category:{}",category);
        categoryService.save(category);
        return R.success("新增分類成功");
    }

    /**
     * 分頁查詢
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize){
        //分頁構造
        Page<Category> pageInfo = new Page<>(page,pageSize);
        //條件構造器
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        //添加排序條件，以sort屬性進行排序
        queryWrapper.orderByAsc(Category::getSort);
        //進行分頁查詢
        categoryService.page(pageInfo,queryWrapper);
        return R.success(pageInfo);
    }

    /**
     * 根據id刪除分類
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> delete(@RequestParam Long ids){
        log.info("刪除分類，id為:{}",ids);
        categoryService.remove(ids);
        return R.success("刪除分類成功");
    }

    /**
     * 根據id修改分類信息
     * @param category
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody Category category){
        log.info("修改分類信息：{}",category);
        categoryService.updateById(category);
        return R.success("修改分類信息成功");
    }

    /**
     * 根據條件查詢分類數據
     * @param category
     * @return
     */
    @GetMapping("/list")
    public R<List<Category>> list(Category category){
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(category.getType()!=null
                ,Category::getType,category.getType());
        //優先使用sort排序，相同情況就使用updateTime
        queryWrapper.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);
        List<Category> list = categoryService.list(queryWrapper);
        return R.success(list);
    }
}
