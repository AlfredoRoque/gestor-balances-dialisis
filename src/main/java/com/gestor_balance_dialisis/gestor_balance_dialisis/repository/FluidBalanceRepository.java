package com.gestor_balance_dialisis.gestor_balance_dialisis.repository;

import com.gestor_balance_dialisis.gestor_balance_dialisis.entity.FluidBalance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * Repository interface for managing FluidBalance entities.
 * This interface extends JpaRepository, providing CRUD operations and additional query methods for FluidBalance.
 */
@Repository
public interface FluidBalanceRepository extends JpaRepository<FluidBalance, Long> {

    List<FluidBalance> getFluidBalancesByDateBetweenAndPatientIdOrderByDateAsc(LocalDateTime dateAfter, LocalDateTime dateBefore, Long patientId);

}
