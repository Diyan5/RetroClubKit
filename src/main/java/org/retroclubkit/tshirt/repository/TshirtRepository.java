package org.retroclubkit.tshirt.repository;

import org.retroclubkit.tshirt.model.Category;
import org.retroclubkit.tshirt.model.Tshirt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Repository
public interface TshirtRepository extends JpaRepository<Tshirt, UUID> {
    List<Tshirt> getProductsByNameIgnoreCase(String teamName);
    List<Tshirt> getTshirtsByCategoryAndIsAvailableTrue(Category category);
    // Използваме JPQL за ограничаване на резултатите
    @Query("SELECT t FROM Tshirt t WHERE t.isAvailable = true ")
    List<Tshirt> getAllTshirtsLimitAvailableTrue(@Param("limit") int limit);

    Collection<Object> findByCategory(Category category);
    List<Tshirt> findByIsAvailableTrue();
    List<Tshirt> findByIsAvailableFalse();

}
