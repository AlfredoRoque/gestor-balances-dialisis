package com.gestor_balance_dialisis.gestor_balance_dialisis.repository;

import com.gestor_balance_dialisis.gestor_balance_dialisis.entity.VitalSignDetail;
import com.gestor_balance_dialisis.gestor_balance_dialisis.enums.StatusEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * Repository interface for managing VitalSignDetail entities, providing CRUD operations and database interactions.
 */
@Repository
public interface VitalSignDetailRepository extends JpaRepository<VitalSignDetail, Long> {

    List<VitalSignDetail> getVitalSignDetailsByDateIsBetweenAndPatientIdAndStatus(Date dateAfter, Date dateBefore, Long patientId, StatusEnum status);
}
