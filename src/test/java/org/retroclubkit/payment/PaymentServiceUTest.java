package org.retroclubkit.payment;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.retroclubkit.order.model.Order;
import org.retroclubkit.payment.model.Payment;
import org.retroclubkit.payment.model.PaymentMethod;
import org.retroclubkit.payment.repository.PaymentRepository;
import org.retroclubkit.payment.service.PaymentService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private PaymentService paymentService;

    private Order order;

    @BeforeEach
    void setUp() {
        order = new Order();
        order.setId(UUID.randomUUID());
        order.setTotalPrice(BigDecimal.valueOf(100.0));
    }

    @Test
    void processPayment_ShouldSavePayment_WhenPaymentMethodIsNotCashOnDelivery() {
        PaymentMethod paymentMethod = PaymentMethod.CREDIT_CARD;
        Payment mockPayment = Payment.builder()
                .order(order)
                .amount(order.getTotalPrice())
                .paymentMethod(paymentMethod)
                .isPaid(true)
                .paymentDate(LocalDateTime.now())
                .transactionId(UUID.randomUUID().toString())
                .build();

        when(paymentRepository.save(any(Payment.class))).thenReturn(mockPayment);

        Payment result = paymentService.processPayment(order, paymentMethod);

        assertNotNull(result);
        assertEquals(order.getTotalPrice(), result.getAmount());
        assertEquals(paymentMethod, result.getPaymentMethod());
        assertTrue(result.isPaid());
        assertNotNull(result.getTransactionId());
        verify(paymentRepository, times(1)).save(any(Payment.class));
    }

    @Test
    void processPayment_ShouldSavePaymentWithNAPaymentId_WhenCashOnDelivery() {
        PaymentMethod paymentMethod = PaymentMethod.CASH_ON_DELIVERY;
        Payment mockPayment = Payment.builder()
                .order(order)
                .amount(order.getTotalPrice())
                .paymentMethod(paymentMethod)
                .isPaid(false)
                .paymentDate(LocalDateTime.now())
                .transactionId("N/A")
                .build();

        when(paymentRepository.save(any(Payment.class))).thenReturn(mockPayment);

        Payment result = paymentService.processPayment(order, paymentMethod);

        assertNotNull(result);
        assertEquals(order.getTotalPrice(), result.getAmount());
        assertEquals(paymentMethod, result.getPaymentMethod());
        assertFalse(result.isPaid());
        assertEquals("N/A", result.getTransactionId());
        verify(paymentRepository, times(1)).save(any(Payment.class));
    }
}

