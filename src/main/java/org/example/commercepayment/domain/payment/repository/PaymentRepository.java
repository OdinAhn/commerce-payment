package org.example.commercepayment.domain.payment.repository;

import org.example.commercepayment.domain.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByOrder_Id(Long orderId);
    List<Payment> findAllByOrder_IdIn(List<Long> orderIds);
}

