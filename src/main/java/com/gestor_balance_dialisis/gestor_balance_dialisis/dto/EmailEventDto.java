package com.gestor_balance_dialisis.gestor_balance_dialisis.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EmailEventDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "Mail to direction", example = "user@domain.com")
    private String to;

    @Schema(description = "Subject for mail", example = "Balance Report")
    private String subject;

    @Schema(description = "Content for mail", example = "<p>Hello, this is your balance report.</p>")
    private String htmlContent;

    @Schema(description = "Attachments for mail", example = "Base64 encoded file content")
    private byte[] attachment;

    @Schema(description = "Attachment file name", example = "report.pdf")
    private String attachmentName;
}
