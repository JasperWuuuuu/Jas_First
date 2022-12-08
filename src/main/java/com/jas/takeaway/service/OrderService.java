package com.jas.takeaway.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jas.takeaway.entity.Orders;

public interface OrderService extends IService<Orders> {
    //用戶下單
    public void submit(Orders orders);
}
