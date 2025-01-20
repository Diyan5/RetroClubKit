package org.retroclubkit.order.model;

import jakarta.persistence.*;
import lombok.*;
import org.retroclubkit.tshirt.model.Tshirt;
import org.retroclubkit.user.model.User;

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
    private double totalPrice; // Общата цена на поръчката

    @Column(nullable = false)
    private String status; // Статус на поръчката (например: Pending, Completed)

    @Column(nullable = false)
    private LocalDateTime createdAt; // Дата на създаване на поръчката

    @OneToMany(mappedBy = "order", fetch = FetchType.EAGER)
    private List<Tshirt> items; // Продукти в поръчката (OrderItems)

    @Column
    private LocalDateTime updatedAt; // Дата на последната актуализация на поръчката (по избор)

    @Column(nullable = false)
    private String deliveryAddress; // Адрес на доставка (например: София, бул. Витоша 15)

}
