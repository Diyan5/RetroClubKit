package org.retroclubkit.payment.model;

import jakarta.persistence.*;
import lombok.*;
import org.retroclubkit.order.model.Order;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "payments")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @OneToOne
    @JoinColumn(name = "order_id", nullable = false) // Външен ключ за поръчката
    private Order order; // Връзка към поръчката, за която е извършено плащането

    @Column(nullable = false)
    private BigDecimal amount; // Сума на плащането

    @Column(nullable = false)
    private PaymentMethod paymentMethod; // Метод на плащане (например: "Credit Card", "Cash on Delivery")

    @Column(nullable = false)
    private boolean isPaid; // Статус на плащането (дали е успешно платено)

    @Column(nullable = false)
    private LocalDateTime paymentDate; // Дата на плащането

    @Column
    private String transactionId; // Идентификатор на транзакцията (ако е наличен)
}
