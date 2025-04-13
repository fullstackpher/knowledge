package io.github.talelin.knowledge.controller.v1;

import io.github.talelin.knowledge.dto.order.OrderDTO;
import io.github.talelin.knowledge.model.OrderDO;
import io.github.talelin.knowledge.service.OrderService;
import io.github.talelin.knowledge.vo.CreatedVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping("/list")
    public List<OrderDO> getAllOrders() {
        return orderService.getAllOrders();
    }

    @PostMapping("/create")
    public CreatedVO createOrder(@RequestBody OrderDTO validator) {
        orderService.createOrder(validator);
        return new CreatedVO("创建订单成功");
    }
}
