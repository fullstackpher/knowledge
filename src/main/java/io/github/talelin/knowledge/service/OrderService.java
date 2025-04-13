package io.github.talelin.knowledge.service;

import io.github.talelin.knowledge.dto.order.OrderDTO;
import io.github.talelin.knowledge.model.OrderDO;

import java.util.List;

public interface OrderService {

    List<OrderDO> getAllOrders();

    Boolean createOrder(OrderDTO validator);
}
