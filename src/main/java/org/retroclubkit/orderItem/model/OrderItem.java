package org.retroclubkit.orderItem.model;

import jakarta.persistence.*;
import lombok.*;
import org.retroclubkit.order.model.Order;
import org.retroclubkit.tshirt.model.Tshirt;

import java.util.UUID;

@Entity
@Table(name = "order_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order; // Връзка към поръчката

    @ManyToOne
    @JoinColumn(name = "tshirt_id", nullable = false)
    private Tshirt tshirt; // Връзка към тениската

    @Column(nullable = false)
    private int quantity; // Количество на поръчаните тениски

    @Column(nullable = false)
    private String size;
}
