package com.gestor_balance_dialisis.gestor_balance_dialisis.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.Map;

/**
 * Data Transfer Object (DTO) for representing a row of data in a report, containing the date, day, and a map of values.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RowDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Instant date;
    private String day;
    private Map<String, String> values;

    public RowDto(Instant date, Map<String, String> values) {
        this.date = date;
        this.values = values;
    }
}
