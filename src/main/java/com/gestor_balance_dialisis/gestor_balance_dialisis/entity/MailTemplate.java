package com.gestor_balance_dialisis.gestor_balance_dialisis.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entity class representing an email template in the system. This class is mapped to the "plantilla_correo" table in the database.
 * It contains fields for the template's ID, name, and content. The content field is defined as TEXT to allow for longer templates.
 */
@Entity
@Table(name = "plantilla_correo",
        indexes = {
                @Index(name = "idx_mail_template_id_plantilla_correo", columnList = "id_plantilla_correo"),
                @Index(name = "idx_mail_template_nombre", columnList = "nombre")
        })
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MailTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_plantilla_correo", nullable = false, unique = true)
    private Long id;

    @Column(name = "nombre", nullable = false)
    private String name;

    @Column(name = "plantilla", nullable = false, columnDefinition = "TEXT")
    private String content;
}