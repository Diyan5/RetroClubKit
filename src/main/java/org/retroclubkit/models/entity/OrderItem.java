package org.retroclubkit.models.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "order_items")
@Getter
@Setter
@NoArgsConstructor
public class OrderItem extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne
    @JoinColumn(name = "tshirt_id", nullable = false)
    private Tshirt tshirt;

    @Column(nullable = false)
    private String size;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false)
    private double price;
}
