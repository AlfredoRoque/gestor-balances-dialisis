package com.gestor_balance_dialisis.gestor_balance_dialisis.repository;

import com.gestor_balance_dialisis.gestor_balance_dialisis.entity.MedicineDetail;
import com.gestor_balance_dialisis.gestor_balance_dialisis.enums.StatusEnum;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

/**
 * Repository interface for managing MedicineDetail entities in the database.
 */
@Repository
public interface MedicineDetailRepository extends JpaRepository<MedicineDetail, Long> {

    @EntityGraph(attributePaths = {"patient", "medicine"})
    List<MedicineDetail> getMedicineDetailByPatientIdAndStatusOrderByDateAsc(Long patientId, StatusEnum status);

    @EntityGraph(attributePaths = {"patient", "medicine"})
    List<MedicineDetail> getMedicineDetailByPatientIdAndDateIsBetweenAndStatusOrderByDateAsc(Long patientId, Instant dateBefore, Instant dateAfter, StatusEnum status);

    List<MedicineDetail> findByPatientId(Long patientId);

    List<MedicineDetail> findByMedicineId(Long medicineId);
}
