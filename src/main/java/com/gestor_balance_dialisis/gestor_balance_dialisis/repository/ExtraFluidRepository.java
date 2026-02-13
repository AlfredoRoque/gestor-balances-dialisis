package com.gestor_balance_dialisis.gestor_balance_dialisis.repository;

import com.gestor_balance_dialisis.gestor_balance_dialisis.entity.ExtraFluid;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * Repository interface for managing ExtraFluid entities in the database.
 * This interface extends JpaRepository, providing basic CRUD operations for ExtraFluid records.
 */
@Repository
public interface ExtraFluidRepository extends JpaRepository<ExtraFluid, Long> {

    List<ExtraFluid> getExtraFluidByDateIsBetweenAndPatientId(Date dateAfter, Date dateBefore, Long patientId);
}
