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

    @Query("SELECT t FROM Tshirt t WHERE t.category = :category AND t.isAvailable = true AND t.isDeleted = false")
    List<Tshirt> getTshirtsByCategoryAndIsAvailableTrueAndDeletedFalse(Category category);

    @Query("SELECT t FROM Tshirt t WHERE t.isAvailable = true AND t.isDeleted = false ORDER BY FUNCTION('RAND')")
    List<Tshirt> getAllTshirtsLimitAvailableTrueAndDeletedFalse(@Param("limit") int limit);

    @Query("SELECT t FROM Tshirt t WHERE t.isDeleted = false")
    List<Tshirt> getAllTshirtsDeletedFalse();

    @Query("SELECT t FROM Tshirt t WHERE t.isAvailable = true AND t.isDeleted = false ")
    List<Tshirt> findByIsAvailableTrueAndDeletedFalse();

    @Query("SELECT t FROM Tshirt t WHERE t.isAvailable = false AND t.isDeleted = false ")
    List<Tshirt> findByIsAvailableFalseAndDeletedFalse();

    @Query("SELECT t FROM Tshirt t WHERE LOWER(t.team.name) = LOWER(:teamName) AND t.isDeleted = false")
    List<Tshirt> findByTeamNameIgnoreCaseAndDeletedFalse(String teamName);

    @Query("SELECT t FROM Tshirt t WHERE t.name = :name AND t.isDeleted = false")
    Optional<Tshirt> findByNameAndDeletedFalse(@NotBlank(message = "Name cannot be empty") @NotNull(message = "Name cannot be empty") String name);
}
