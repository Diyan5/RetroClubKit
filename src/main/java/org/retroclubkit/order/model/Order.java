package org.retroclubkit.order.model;

import jakarta.persistence.*;
import lombok.*;
import org.retroclubkit.orderItem.model.OrderItem;
import org.retroclubkit.payment.model.Payment;
import org.retroclubkit.user.model.User;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "orders")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // Връзка към потребителя, който е направил поръчката

    @Column(nullable = false)
    private String fullName; // Име на получателя

    @Column(nullable = false)
    private String phoneNumber; // Телефон за доставка

    @Column(nullable = false)
    private BigDecimal totalPrice; // Общата цена на поръчката

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status; // Статус на поръчката (например: Pending, Completed)

    @Column(nullable = false)
    private LocalDateTime createdAt; // Дата на създаване на поръчката

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<OrderItem> items; // Продукти в поръчката с количество

    @Column(nullable = false)
    private String deliveryAddress; // Адрес на доставка (например: София, бул. Витоша 15)

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL)
    private Payment payment;

}
