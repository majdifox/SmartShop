package org.majdifoxx.smartshop.service;

import org.majdifoxx.smartshop.dto.request.OrderRequestDTO;
import org.majdifoxx.smartshop.dto.response.OrderResponseDTO;
import org.majdifoxx.smartshop.entity.Order;

import java.math.BigDecimal;
import java.util.List;

public interface OrderService {
    OrderResponseDTO createOrder(OrderRequestDTO request);
    OrderResponseDTO confirmOrder(Long orderId);
    OrderResponseDTO cancelOrder(Long orderId);
    OrderResponseDTO getOrderById(Long id);
    List<OrderResponseDTO> getAllOrders();
    List<OrderResponseDTO> getClientOrders(Long clientId);
    Order getOrderEntity(Long id);




}
