package org.retroclubkit.tshirt.model;

import jakarta.persistence.*;
import lombok.*;
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

    @Column(nullable = false)
    private String name; // Име на тениската (например "Real Madrid 2004")

    @Column(nullable = false)
    private BigDecimal price; // Цена на тениската

    @Column
    private String image;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Category category; // Категория на тениската (енумерация: RETRO, LIMITED EDITION)


    @ElementCollection
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "tshirt_sizes", joinColumns = @JoinColumn(name = "tshirt_id", referencedColumnName = "id"))
    @Column(name = "size")
    private List<Size> sizes; // Множество размери на тениската

    @Column(nullable = false)
    private boolean isAvailable; // Наличност на тениската (дали е в продажба)

    @ManyToOne
    @JoinColumn(name = "team_id", nullable = false)
    private Team team; // Свързано с отбора, към който принадлежи тениската

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = true)
    private Order order;

}
