package org.majdifoxx.smartshop.mapper;

import org.majdifoxx.smartshop.dto.response.OrderItemResponseDTO;
import org.majdifoxx.smartshop.dto.response.OrderResponseDTO;
import org.majdifoxx.smartshop.entity.Order;
import org.majdifoxx.smartshop.entity.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Mapping(source = "client.id", target = "clientId")
    @Mapping(source = "client.name", target = "clientName")
    @Mapping(source = "orderItems", target = "items")
    OrderResponseDTO toResponseDTO(Order order);

    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "product.name", target = "productName")
    OrderItemResponseDTO toOrderItemResponseDTO(OrderItem orderItem);

    List<OrderItemResponseDTO> toOrderItemResponseDTOList(List<OrderItem> orderItems);
}
