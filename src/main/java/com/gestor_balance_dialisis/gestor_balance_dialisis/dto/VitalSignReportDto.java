package com.gestor_balance_dialisis.gestor_balance_dialisis.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VitalSignReportDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "Day check", example = "01/01/2024")
    private String day;

    @Schema(description = "Blood pressure value", example = "120/80")
    private String bloodPressure;

    @Schema(description = "Glucose value", example = "90")
    private String glucose;
}
