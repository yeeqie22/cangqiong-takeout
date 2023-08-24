package com.sky.service;

import com.sky.dto.DishDTO;

/**
 *菜品管理
 */
public interface DishService {
    /**
     * 添加菜品及風味
     * @param dishDTO
     */
    void saveWithFlavors(DishDTO dishDTO);
}
