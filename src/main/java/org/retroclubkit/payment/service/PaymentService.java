package org.retroclubkit.payment.service;

import org.retroclubkit.order.model.Order;
import org.retroclubkit.payment.model.Payment;
import org.retroclubkit.payment.model.PaymentMethod;
import org.retroclubkit.payment.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;

    @Autowired
    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    public Payment processPayment(Order order, PaymentMethod paymentMethod) {
        boolean isPaid = paymentMethod != PaymentMethod.CASH_ON_DELIVERY;

        String transactionId = (paymentMethod == PaymentMethod.CASH_ON_DELIVERY)
                ? "N/A"
                : UUID.randomUUID().toString();

        Payment payment = Payment.builder()
                .order(order)
                .amount(order.getTotalPrice())
                .paymentMethod(paymentMethod)
                .isPaid(isPaid)
                .paymentDate(LocalDateTime.now())
                .transactionId(transactionId)
                .build();

        return paymentRepository.save(payment);
    }
}
