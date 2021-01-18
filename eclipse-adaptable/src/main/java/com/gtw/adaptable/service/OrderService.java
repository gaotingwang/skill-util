package com.gtw.adaptable.service;

import com.gtw.adaptable.business.Ump;
import com.gtw.adaptable.domain.Order;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    public void orderUmp() {
        Order order = new Order("general",1);
        // 通过adapter可以动态
        Ump ump = (Ump)order.getAdapter(Ump.class);
        float promotion = 0f;
        if(ump != null) {
            ump.setOrderPromation(order);
            promotion = ump.getOrderPromation(order);
        }
        System.out.println("promotion is : " + promotion);
    }
}
