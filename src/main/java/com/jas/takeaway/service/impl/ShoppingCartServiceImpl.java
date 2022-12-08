package com.jas.takeaway.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jas.takeaway.entity.ShoppingCart;
import com.jas.takeaway.mapper.ShoppingCartMapper;
import com.jas.takeaway.service.ShoppingCartService;
import org.springframework.stereotype.Service;

@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService {
}
