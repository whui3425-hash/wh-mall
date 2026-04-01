package com.gupaoedu.vip.mall.goods.controller;

import com.gupaoedu.mall.util.RespResult;
import com.gupaoedu.vip.mall.goods.model.Category;
import com.gupaoedu.vip.mall.goods.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/category")
@CrossOrigin
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /****
     * Query child categories by parent ID
     */
    @GetMapping(value = "/parent/{id}")
    public RespResult<List<Category>> findByParentId(@PathVariable("id")Integer id){
        return RespResult.ok(categoryService.findByParentId(id));
    }

    /****
     * Query category info by ID
     */
    @GetMapping(value = "/{id}")
    public RespResult<Category> one(@PathVariable(value = "id")Integer id){
        Category category = categoryService.getById(id);
        return RespResult.ok(category);
    }
}
