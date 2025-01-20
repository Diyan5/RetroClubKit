package org.retroclubkit.team.model;

import jakarta.persistence.*;
import lombok.*;

import org.retroclubkit.tshirt.model.Tshirt;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "teams")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Team {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String name; // Име на отбора (например: Real Madrid, Chelsea)

    @Column(nullable = false)
    private String country; // Страна на отбора (например: Испания, Англия)

    @Column(nullable = false)
    private long trophies;

    @OneToMany(mappedBy = "team",fetch = FetchType.EAGER)
    private List<Tshirt> tshirts; // Тениски на отбора
}
