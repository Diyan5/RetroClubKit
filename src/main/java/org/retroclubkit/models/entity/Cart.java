package org.retroclubkit.models.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "cart")
@Getter
@Setter
@NoArgsConstructor
public class Cart extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "tshirt_id", nullable = false)
    private Tshirt tshirt;

    @Column(nullable = false)
    private String size;

    @Column(nullable = false)
    private int quantity;
}

