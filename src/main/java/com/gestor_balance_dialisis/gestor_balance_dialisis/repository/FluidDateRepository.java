package com.gestor_balance_dialisis.gestor_balance_dialisis.repository;

import com.gestor_balance_dialisis.gestor_balance_dialisis.entity.FluidDate;
import com.gestor_balance_dialisis.gestor_balance_dialisis.enums.StatusEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for managing FluidDate entities, providing methods to retrieve fluid dates based on their status.
 */
@Repository
public interface FluidDateRepository extends JpaRepository<FluidDate, Long> {
    List<FluidDate> getFluidDateByStatus(StatusEnum status);
}
