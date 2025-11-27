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

    // PDF: "Taux de TVA: 20% par défaut (paramétrable)"
    private static final BigDecimal TVA_RATE = new BigDecimal("0.20");

    @Override
    public OrderResponseDTO createOrder(OrderRequestDTO request) {
        // Get client
        Client client = clientService.getClientEntity(request.getClientId());

        // PDF: "Une commande sans client ou sans article est refusée"
        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new BusinessRuleException("Order must have at least one item");
        }

        // Create order
        Order order = Order.builder()
                .client(client)
                .promoCode(request.getPromoCode())
                .status(OrderStatus.PENDING)
                .build();

        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal subtotalHT = BigDecimal.ZERO;

        // Process each order item
        for (OrderItemRequestDTO itemRequest : request.getItems()) {
            Product product = productService.getProductEntity(itemRequest.getProductId());

            // PDF: "Valider les prérequis: Disponibilité du stock"
            try {
                productService.validateStock(product, itemRequest.getQuantity());
            } catch (BusinessRuleException e) {
                // PDF: "REJECTED: refusée (stock insuffisant)"
                order.setStatus(OrderStatus.REJECTED);
                orderRepository.save(order);
                throw new BusinessRuleException("Order rejected: " + e.getMessage());
            }

            // Create order item
            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .product(product)
                    .quantity(itemRequest.getQuantity())
                    .unitPrice(product.getUnitPrice())
                    .build();

            // Calculate line total
            BigDecimal lineTotal = product.getUnitPrice()
                    .multiply(new BigDecimal(itemRequest.getQuantity()))
                    .setScale(2, RoundingMode.HALF_UP);
            orderItem.setLineTotal(lineTotal);

            orderItems.add(orderItem);
            subtotalHT = subtotalHT.add(lineTotal);
        }

        order.setOrderItems(orderItems);

        // PDF: "Sous-total HT: somme de (prix HT × quantité de produit)"
        order.setSubtotalHT(subtotalHT.setScale(2, RoundingMode.HALF_UP));

        // PDF: "Remise fidélité selon niveau client"
        BigDecimal loyaltyDiscount = loyaltyService.calculateDiscount(
                client.getTier(),
                order.getSubtotalHT()
        );
        order.setLoyaltyDiscount(loyaltyDiscount);

        // PDF: "Code promo PROMO-XXXX (+5% si valide)"
        BigDecimal promoDiscount = BigDecimal.ZERO;
        if (request.getPromoCode() != null &&
                request.getPromoCode().matches("^PROMO-[A-Z0-9]{4}$")) {
            promoDiscount = subtotalHT.multiply(new BigDecimal("0.05"))
                    .setScale(2, RoundingMode.HALF_UP);
        }
        order.setPromoDiscount(promoDiscount);

        // PDF: "Montant remise totale"
        BigDecimal totalDiscount = loyaltyDiscount.add(promoDiscount)
                .setScale(2, RoundingMode.HALF_UP);
        order.setTotalDiscount(totalDiscount);

        // PDF: "Montant HT après remise = Sous-total HT - Montant remise"
        BigDecimal amountHT = subtotalHT.subtract(totalDiscount)
                .setScale(2, RoundingMode.HALF_UP);
        order.setAmountHT(amountHT);

        // PDF: "TVA 20% = Montant HT après remise × 20%"
        // PDF: "Note: La TVA se calcule sur le montant APRÈS remise"
        BigDecimal tvaAmount = amountHT.multiply(TVA_RATE)
                .setScale(2, RoundingMode.HALF_UP);
        order.setTvaAmount(tvaAmount);

        // PDF: "Total TTC = Montant HT après remise + TVA"
        BigDecimal totalTTC = amountHT.add(tvaAmount)
                .setScale(2, RoundingMode.HALF_UP);
        order.setTotalTTC(totalTTC);

        // Initialize remaining amount
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

        // PDF: "Une commande ne peut être validée que si totalement payée"
        if (order.getRemainingAmount().compareTo(BigDecimal.ZERO) != 0) {
            throw new BusinessRuleException(
                    "Order must be fully paid before confirmation. Remaining: "
                            + order.getRemainingAmount() + " DH");
        }

        // PDF: "CONFIRMED: commande validée par ADMIN (après paiement complet)"
        order.setStatus(OrderStatus.CONFIRMED);

        // PDF: "Décrémenter le stock produits"
        for (OrderItem item : order.getOrderItems()) {
            productService.decrementStock(item.getProduct(), item.getQuantity());
        }

        // PDF: "Actualiser statistiques client (totalOrders, totalSpent)"
        // PDF: "Recalculer niveau fidélité"
        clientService.updateClientStatsAfterOrderConfirmation(
                order.getClient(),
                order.getTotalTTC()
        );

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

        // PDF: "CANCELED: annulée manuellement par ADMIN"
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
        // PDF: "Consulter l'historique des commandes"
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
