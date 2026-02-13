package com.gestor_balance_dialisis.gestor_balance_dialisis.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * DTO for calculating fluid balance response.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CalculateFluidBalanceResponseDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "Fluid balances for patient", example = "[{'id': 1, 'patientId': 1, 'date': '2024-06-01T10:00:00', 'ingested': 500.00, 'urine': 200.00}]")
    private List<FluidBalanceResponse> fluidBalances;

    @Schema(description = "Extra fluids for patient", example = "[{'id': 1, 'patientId': 1, 'urine': 200.00, 'ingested': 500.00, 'date': '2024-06-01T10:00:00'}]")
    private List<ExtraFluidResponseDto> extraFluids;

    @Schema(description = "Vital signs for patient", example = "[{'id': 1, 'patientId': 1, 'date': '2024-06-01T10:00:00', 'bloodPressure': '120/80', 'heartRate': 70}]")
    private List<VitalSignDetailResponse> vitalSignDetails;

    @Schema(description = "Medicines for patient", example = "[{'id': 1, 'patientId': 1, 'date': '2024-06-01T10:00:00', 'medicineName': 'Medicine A', 'dose': '500mg'}]")
    private List<MedicineDetailResponseDto> medicineDetails;

    @Schema(description = "Partial balance", example = "100.00")
    private BigDecimal partialBalance;

    @Schema(description = "Total balance", example = "300.00")
    private BigDecimal totalBalance;

    @Schema(description = "Total ingested", example = "500.00")
    private BigDecimal totalIngested;

    @Schema(description = "Total urine", example = "200.00")
    private BigDecimal totalUrine;

    @Schema(description = "Final total balance", example = "200.00")
    private BigDecimal finalBalance;
}
