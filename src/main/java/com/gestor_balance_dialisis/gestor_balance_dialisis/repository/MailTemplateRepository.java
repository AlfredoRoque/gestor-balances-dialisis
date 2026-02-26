package com.gestor_balance_dialisis.gestor_balance_dialisis.repository;

import com.gestor_balance_dialisis.gestor_balance_dialisis.entity.MailTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing MailTemplate entities in the database.
 * This interface extends JpaRepository, providing CRUD operations and additional query methods for MailTemplate entities.
 */
@Repository
public interface MailTemplateRepository extends JpaRepository<MailTemplate,Long> {
    MailTemplate findByName(String name);
}
