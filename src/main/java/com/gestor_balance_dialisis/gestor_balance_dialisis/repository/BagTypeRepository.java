package com.gestor_balance_dialisis.gestor_balance_dialisis.repository;

import com.gestor_balance_dialisis.gestor_balance_dialisis.entity.BagType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing BagType entities, providing CRUD operations and database interactions.
 */
@Repository
public interface BagTypeRepository extends JpaRepository<BagType, Long> {
}
