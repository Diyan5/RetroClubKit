package org.retroclubkit.contact.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;
import org.retroclubkit.contact.model.ContactMessage;

@Repository
public interface ContactRepository extends JpaRepository<ContactMessage, UUID> {
}
