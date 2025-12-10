package org.majdifoxx.smartshop.service;

import lombok.RequiredArgsConstructor;
import org.majdifoxx.smartshop.dto.request.OrderItemRequestDTO;
import org.majdifoxx.smartshop.dto.request.OrderRequestDTO;
import org.majdifoxx.smartshop.dto.response.OrderResponseDTO;
import org.majdifoxx.smartshop.entity.Client;
import org.majdifoxx.smartshop.entity.Order;
import org.majdifoxx.smartshop.entity.OrderItem;
import org.majdifoxx.smartshop.entity.Product;
import org.majdifoxx.smartshop.enums.OrderStatus;
import org.majdifoxx.smartshop.exception.BusinessRuleException;
import org.majdifoxx.smartshop.exception.ResourceNotFoundException;
import org.majdifoxx.smartshop.mapper.OrderMapper;
import org.majdifoxx.smartshop.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final ClientService clientService;
    private final ProductService productService;
    private final LoyaltyService loyaltyService;
    private final PromoCodeService promoCodeService;

    private static final BigDecimal TVA_RATE = new BigDecimal("0.20");

    @Override
    public OrderResponseDTO createOrder(OrderRequestDTO request) {
        Client client = clientService.getClientEntity(request.getClientId());

        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new BusinessRuleException("Order must have at least one item");
        }

        Order order = Order.builder()
                .client(client)
                .promoCode(request.getPromoCode())
                .status(OrderStatus.PENDING)
                .build();

        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal subtotalHT = BigDecimal.ZERO;

        for (OrderItemRequestDTO itemRequest : request.getItems()) {
            Product product = productService.getProductEntity(itemRequest.getProductId());

            try {
                productService.validateStock(product, itemRequest.getQuantity());
            } catch (BusinessRuleException e) {
                order.setStatus(OrderStatus.REJECTED);
                orderRepository.save(order);
                throw new BusinessRuleException("Order rejected: " + e.getMessage());
            }

            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .product(product)
                    .quantity(itemRequest.getQuantity())
                    .unitPrice(product.getUnitPrice())
                    .build();

            BigDecimal lineTotal = product.getUnitPrice()
                    .multiply(new BigDecimal(itemRequest.getQuantity()))
                    .setScale(2, RoundingMode.HALF_UP);
            orderItem.setLineTotal(lineTotal);

            orderItems.add(orderItem);
            subtotalHT = subtotalHT.add(lineTotal);
        }

        order.setOrderItems(orderItems);
        order.setSubtotalHT(subtotalHT.setScale(2, RoundingMode.HALF_UP));

        BigDecimal loyaltyDiscount = loyaltyService.calculateDiscount(
                client.getTier(),
                order.getSubtotalHT()
        );
        order.setLoyaltyDiscount(loyaltyDiscount);

        BigDecimal promoDiscount = BigDecimal.ZERO;
        if (request.getPromoCode() != null && !request.getPromoCode().trim().isEmpty()) {
            String code = request.getPromoCode().trim();
            promoCodeService.validateForUse(code);
            promoDiscount = subtotalHT.multiply(new BigDecimal("0.05"))
                    .setScale(2, RoundingMode.HALF_UP);
        }
        order.setPromoDiscount(promoDiscount);

        BigDecimal totalDiscount = loyaltyDiscount.add(promoDiscount)
                .setScale(2, RoundingMode.HALF_UP);
        order.setTotalDiscount(totalDiscount);

        BigDecimal amountHT = subtotalHT.subtract(totalDiscount)
                .setScale(2, RoundingMode.HALF_UP);
        order.setAmountHT(amountHT);

        BigDecimal tvaAmount = amountHT.multiply(TVA_RATE)
                .setScale(2, RoundingMode.HALF_UP);
        order.setTvaAmount(tvaAmount);

        BigDecimal totalTTC = amountHT.add(tvaAmount)
                .setScale(2, RoundingMode.HALF_UP);
        order.setTotalTTC(totalTTC);
        order.setRemainingAmount(totalTTC);

        Order savedOrder = orderRepository.save(order);
        return orderMapper.toResponseDTO(savedOrder);
    }

    @Override
    public OrderResponseDTO confirmOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new BusinessRuleException("Only PENDING orders can be confirmed");
        }

        if (order.getRemainingAmount().compareTo(BigDecimal.ZERO) != 0) {
            throw new BusinessRuleException(
                    "Order must be fully paid before confirmation. Remaining: "
                            + order.getRemainingAmount() + " DH");
        }

        order.setStatus(OrderStatus.CONFIRMED);

        for (OrderItem item : order.getOrderItems()) {
            productService.decrementStock(item.getProduct(), item.getQuantity());
        }

        clientService.updateClientStatsAfterOrderConfirmation(
                order.getClient(),
                order.getTotalTTC()
        );

        if (order.getPromoCode() != null && !order.getPromoCode().trim().isEmpty()) {
            promoCodeService.markAsUsed(order.getPromoCode().trim(), order);
        }

        Order savedOrder = orderRepository.save(order);
        return orderMapper.toResponseDTO(savedOrder);
    }

    @Override
    public OrderResponseDTO cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new BusinessRuleException("Only PENDING orders can be canceled");
        }

        order.setStatus(OrderStatus.CANCELED);

        Order savedOrder = orderRepository.save(order);
        return orderMapper.toResponseDTO(savedOrder);
    }

    @Override
    public OrderResponseDTO getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
        return orderMapper.toResponseDTO(order);
    }

    @Override
    public List<OrderResponseDTO> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(orderMapper::toResponseDTO)
                .toList();
    }

    @Override
    public List<OrderResponseDTO> getClientOrders(Long clientId) {
        return orderRepository.findByClientIdOrderByCreatedAtDesc(clientId).stream()
                .map(orderMapper::toResponseDTO)
                .toList();
    }

    @Override
    public Order getOrderEntity(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
    }
}
