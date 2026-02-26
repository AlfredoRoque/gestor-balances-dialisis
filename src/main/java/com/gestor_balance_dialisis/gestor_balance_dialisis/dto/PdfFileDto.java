package com.gestor_balance_dialisis.gestor_balance_dialisis.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * PdfFileDto is a Data Transfer Object (DTO) that represents a PDF file, containing the file name and its binary data.
 * It includes constructors, getters, and setters for these fields. The class implements Serializable for object serialization.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PdfFileDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "File name", example = "report.pdf")
    private String fileName;

    @Schema(description = "File date", example = "JVBERi0xLjQKJcfsj6...")
    private byte[] data;
}
