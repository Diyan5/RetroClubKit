package org.retroclubkit.repository;

import org.retroclubkit.models.entity.Tshirt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TshirtRepository extends JpaRepository<Tshirt, Long> {

}
