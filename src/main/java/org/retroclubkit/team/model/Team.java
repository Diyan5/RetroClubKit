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
    private String name;

    @Column(nullable = false)
    private String country;

    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL)
    private List<Tshirt> tshirts;
}
