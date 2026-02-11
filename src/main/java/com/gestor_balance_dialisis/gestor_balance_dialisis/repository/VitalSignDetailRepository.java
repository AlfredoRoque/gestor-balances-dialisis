package com.gestor_balance_dialisis.gestor_balance_dialisis.repository;

import com.gestor_balance_dialisis.gestor_balance_dialisis.entity.VitalSignDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing VitalSignDetail entities, providing CRUD operations and database interactions.
 */
@Repository
public interface VitalSignDetailRepository extends JpaRepository<VitalSignDetail, Long> {
}
