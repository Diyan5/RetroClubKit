package org.retroclubkit.tshirt.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.validator.constraints.URL;
import org.retroclubkit.order.model.Order;
import org.retroclubkit.team.model.Team;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "tshirts")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Tshirt {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private BigDecimal price;


    @Column(nullable = false)
    private String image;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Category category;

    private boolean isDeleted = false;

    @ElementCollection
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "tshirt_sizes", joinColumns = @JoinColumn(name = "tshirt_id", referencedColumnName = "id"))
    @Column(name = "size")
    private List<Size> sizes;

    @Column(nullable = false)
    private boolean isAvailable;

    @ManyToOne
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = true)
    private Order order;

}
