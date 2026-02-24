package com.gestor_balance_dialisis.gestor_balance_dialisis.repository;

import com.gestor_balance_dialisis.gestor_balance_dialisis.entity.VitalSign;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for managing VitalSign entities, providing CRUD operations and database interactions.
 */
@Repository
public interface VitalSignRepository extends JpaRepository<VitalSign, Long> {

    List<VitalSign> findByUserId(Long userId);
}
