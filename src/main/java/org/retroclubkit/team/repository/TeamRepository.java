package org.retroclubkit.team.repository;

import jakarta.validation.constraints.NotNull;
import org.retroclubkit.team.model.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TeamRepository extends JpaRepository<Team, UUID> {
    Optional<Team> findByName(@NotNull(message = "Team name cannot be null!") String name);

    @Query("SELECT t FROM Team t WHERE t.isDeleted = false")
    List<Team> findAllByIsDeletedFalse();
}
