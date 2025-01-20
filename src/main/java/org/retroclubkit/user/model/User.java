package org.retroclubkit.user.model;

import jakarta.persistence.*;
import lombok.*;
import org.retroclubkit.order.model.Order;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "users")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(nullable = false, unique = true)
    private String username;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    private String firstName;

    private String lastName;

    @Column(unique = true)
    private String email;

    @Column(nullable = false)
    private String password;


    private boolean isActive;

    @Column(nullable = false)
    private LocalDateTime createdOn;

    @Column(nullable = false)
    private LocalDateTime updatedOn;

    @OneToMany(fetch = FetchType.EAGER,mappedBy = "user")
    private List<Order> orders;
}
