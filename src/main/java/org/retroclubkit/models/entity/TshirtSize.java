package org.retroclubkit.models.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tshirt_sizes")
@Getter
@Setter
@NoArgsConstructor
public class TshirtSize extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "tshirt_id", nullable = false)
    private Tshirt tshirt;

    @Column(nullable = false)
    private String size;

    @Column(nullable = false)
    private int quantity;
}

