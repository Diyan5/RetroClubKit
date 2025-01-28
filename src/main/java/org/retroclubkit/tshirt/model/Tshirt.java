package org.retroclubkit.tshirt.model;

import jakarta.persistence.*;
import lombok.*;
import org.retroclubkit.order.model.Order;
import org.retroclubkit.team.model.Team;

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
    private double price; // Цена на тениската

    @Column
    private String image;

    @Column
    private String description; // Описание на тениската

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Category category; // Категория на тениската (енумерация: RETRO, LIMITED EDITION)


    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Size size; // Размери на тениската (енумерации: XS, S, M, L, XL)

    @Column(nullable = false)
    private boolean isAvailable; // Наличност на тениската (дали е в продажба)

    @ManyToOne
    @JoinColumn(name = "team_id", nullable = false)
    private Team team; // Свързано с отбора, към който принадлежи тениската

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = true)
    private Order order;

}
