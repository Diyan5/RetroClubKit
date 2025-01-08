package org.retroclubkit.models.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tshirts")
@Getter
@Setter
@NoArgsConstructor
public class Tshirt extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private double price;

    @Column
    private String description;

    @ManyToOne
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @OneToMany(mappedBy = "tshirt", cascade = CascadeType.ALL)
    private List<TshirtSize> sizes = new ArrayList<>();

    @Transient
    private boolean isAvailable; // True if available, false if out of stock
}
