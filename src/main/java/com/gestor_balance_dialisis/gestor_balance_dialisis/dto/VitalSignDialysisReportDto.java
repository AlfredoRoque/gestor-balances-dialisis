package com.gestor_balance_dialisis.gestor_balance_dialisis.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * Data Transfer Object (DTO) for representing a vital sign report related to dialysis.
 * This DTO contains information about the day of the check, blood pressure value, and glucose value.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VitalSignDialysisReportDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private List<String> columns;
    private List<RowDto> rows;

}
