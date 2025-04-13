package io.github.talelin.knowledge.service.impl;

import io.github.talelin.knowledge.common.util.BeanCopyUtil;
import io.github.talelin.knowledge.dto.order.OrderDTO;
import io.github.talelin.knowledge.mapper.OrderMapper;
import io.github.talelin.knowledge.model.OrderDO;
import io.github.talelin.knowledge.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Override
    public List<OrderDO> getAllOrders() {
        return orderMapper.getAllOrders();
    }

    @Override
    public Boolean createOrder(OrderDTO validator) {
        OrderDO orderDO = BeanCopyUtil.copyProperties(validator, OrderDO::new);
        return orderMapper.insertOrder(orderDO) > 0;
    }
}
