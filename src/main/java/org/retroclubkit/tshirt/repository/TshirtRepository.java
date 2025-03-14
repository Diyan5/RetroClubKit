package org.retroclubkit.tshirt.repository;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.retroclubkit.tshirt.model.Category;
import org.retroclubkit.tshirt.model.Tshirt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TshirtRepository extends JpaRepository<Tshirt, UUID> {

    List<Tshirt> getTshirtsByCategoryAndIsAvailableTrue(Category category);

    @Query("SELECT t FROM Tshirt t WHERE t.isAvailable = true ")
    List<Tshirt> getAllTshirtsLimitAvailableTrue(@Param("limit") int limit);

    List<Tshirt> findByIsAvailableTrue();

    List<Tshirt> findByIsAvailableFalse();

    List<Tshirt> findByTeamNameIgnoreCase(String teamName);

    Optional<Tshirt> findByName(@NotBlank(message = "Name cannot be empty") @NotNull(message = "Name cannot be empty") String name);
}
