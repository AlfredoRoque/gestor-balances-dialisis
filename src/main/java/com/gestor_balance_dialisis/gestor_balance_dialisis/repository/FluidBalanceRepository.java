package com.gestor_balance_dialisis.gestor_balance_dialisis.repository;

import com.gestor_balance_dialisis.gestor_balance_dialisis.entity.FluidBalance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

/**
 * Repository interface for managing FluidBalance entities.
 * This interface extends JpaRepository, providing CRUD operations and additional query methods for FluidBalance.
 */
@Repository
public interface FluidBalanceRepository extends JpaRepository<FluidBalance, Long> {

    List<FluidBalance> getFluidBalancesByDateBetweenAndPatientIdOrderByDateAsc(Instant dateAfter, Instant dateBefore, Long patientId);

    List<FluidBalance> findByDateAndPatientId(Instant date, Long patientId);

    Integer countByPatientId(Long patientId);

    @Transactional
    void deleteByPatientIdAndDateBefore(Long patientId, Instant date);

    List<FluidBalance> findByPatientId(Long patientId);
}
