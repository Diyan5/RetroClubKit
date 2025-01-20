package org.retroclubkit.tshirt.repository;

import org.retroclubkit.tshirt.model.Tshirt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TshirtRepository extends JpaRepository<Tshirt, UUID> {
}
