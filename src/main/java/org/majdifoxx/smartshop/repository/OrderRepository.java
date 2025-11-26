package org.majdifoxx.smartshop.repository;

import org.majdifoxx.smartshop.entity.Order;
import org.majdifoxx.smartshop.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByClientIdOrderByCreatedAtDesc(Long clientId);
    List<Order> findByStatus(OrderStatus status);
}
