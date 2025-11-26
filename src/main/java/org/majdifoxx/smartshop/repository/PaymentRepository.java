package org.majdifoxx.smartshop.repository;

import org.majdifoxx.smartshop.entity.Payment;
import org.majdifoxx.smartshop.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    List<Payment> findByOrderIdOrderByPaymentNumberAsc(Long orderId);
    List<Payment> findByStatus(PaymentStatus status);

}
