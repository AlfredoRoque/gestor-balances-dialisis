package com.gestor_balance_dialisis.gestor_balance_dialisis.repository;

import com.gestor_balance_dialisis.gestor_balance_dialisis.entity.ExtraFluid;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

/**
 * Repository interface for managing ExtraFluid entities in the database.
 */
@Repository
public interface ExtraFluidRepository extends JpaRepository<ExtraFluid, Long> {

    @EntityGraph(attributePaths = {"patient"})
    List<ExtraFluid> getExtraFluidByDateIsBetweenAndPatientId(Instant dateAfter, Instant dateBefore, Long patientId);

    @Transactional
    void deleteByPatientIdAndDateBefore(Long patientId, Instant filterDate);

    List<ExtraFluid> findByPatientId(Long patientId);
}
