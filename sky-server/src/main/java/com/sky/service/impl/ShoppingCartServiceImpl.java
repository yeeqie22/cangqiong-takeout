package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class ShoppingCartServiceImpl implements ShoppingCartService {
    @Autowired
    ShoppingCartMapper shoppingCartMapper;
    @Autowired
    DishMapper dishMapper;
    @Autowired
    SetmealMapper setmealMapper;
    /**
     * 添加购物车
     * @param shoppingCartDTO
     */
    public void addShoppingCart(ShoppingCartDTO shoppingCartDTO) {
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO,shoppingCart);
        Long currentId = BaseContext.getCurrentId();


        //判断当前加入购物车中的商品是否已经存在
        List<ShoppingCart> shoppingCarts = shoppingCartMapper.list(shoppingCartDTO);
        if(shoppingCarts != null && shoppingCarts.size() > 0){
            //如果存在，只需数量加一
            shoppingCarts.get(0).setNumber(shoppingCarts.get(0).getNumber() + 1);
            shoppingCartMapper.updateNumberById(shoppingCarts.get(0));
        }else{
            //如果不存在，需要插入一条购物车数据
            if(shoppingCartDTO.getDishId() != null){
                Dish dish = dishMapper.getById(shoppingCartDTO.getDishId());
                shoppingCart.setAmount(dish.getPrice());
                shoppingCart.setName(dish.getName());
                shoppingCart.setImage(dish.getImage());
            }else{
                Setmeal setmeal = setmealMapper.getById(shoppingCartDTO.getSetmealId());
                shoppingCart.setAmount(setmeal.getPrice());
                shoppingCart.setName(setmeal.getName());
                shoppingCart.setImage(setmeal.getImage());
            }
            shoppingCart.setUserId(currentId);
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartMapper.insert(shoppingCart);
        }

    }
}
