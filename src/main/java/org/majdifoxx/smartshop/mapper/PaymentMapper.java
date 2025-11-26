package org.majdifoxx.smartshop.mapper;

import org.majdifoxx.smartshop.dto.response.PaymentResponseDTO;
import org.majdifoxx.smartshop.entity.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PaymentMapper {

    @Mapping(source = "order.id", target = "orderId")
    PaymentResponseDTO toResponseDTO(Payment payment);

    List<PaymentResponseDTO> toResponseDTOList(List<Payment> payments);
}
