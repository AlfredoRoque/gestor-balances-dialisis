package com.gestor_balance_dialisis.gestor_balance_dialisis.repository;

import com.gestor_balance_dialisis.gestor_balance_dialisis.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing Patient entities in the database.
 * Provides methods for performing CRUD operations and custom queries related to patients.
 */
@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {

    List<Patient> findByUserId(Long id);

    List<Patient> findByName(String name);

    Optional<Patient> findByNameAndUserId(String name, Long userId);

    Integer countByUserId(Long userId);
}
