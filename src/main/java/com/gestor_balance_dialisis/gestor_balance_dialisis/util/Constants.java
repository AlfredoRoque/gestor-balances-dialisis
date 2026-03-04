package com.gestor_balance_dialisis.gestor_balance_dialisis.util;

import lombok.experimental.UtilityClass;

/**
 * Utility class containing constant values used throughout the application.
 * This class is marked as a utility class to prevent instantiation and to group related constants together.
 */
@UtilityClass
public class Constants {

    public static final String PATH_MAIN_REPORT = "/reports/Dialisis_main.jasper";
    public static final String PATH_REPORTS = "/reports/";

    public static final String SEND_MAIL_PROVIDER_ENDPOINT = "mail/send";

    public static final String INVALID_CREDENTIALS = "Credenciales invalidas";
    public static final String UPDATE_ERROR_CREDENTIALS = "Error al actualizar credenciales.";
    public static final String USER_NOT_FOUND = "Usuario no encontrado";
    public static final String ERROR_RECOVERING_PASSWORD = "Error al recuperar contraseña";
    public static final String MEDICINE_DETAIL_NOT_FOUND = "Detalle de medicina no encontrado.";
    public static final String MEDICINE_NOT_FOUND = "Medicina no encontrada.";
    public static final String PATIENT_NOT_FOUND = "Paciente no encontrado";
    public static final String GENERATE_REPORT_ERROR = "Error al generar el reporte del paciente.";
    public static final String FLUID_BALANCE_DOEST_EXIST = "No existe el balance de fluido.";
    public static final String EXTRA_FLUID_NOT_FOUND = "No se encontró fluido extra para el ID: %s";
    public static final String FLUID_BALANCE_EXIST = "Ya existe un balance de fluido registrado para la fecha y paciente.";
    public static final String EMAIL_USER_EXIST = "Ya existe un usuario asignado al correo con el que intentas registrarte.";
    public static final String USER_NAME_EXIST = "Ya existe un usuario con el nombre que intentas registrarte.";
    public static final String VITAL_SIGN_DETAIL_NOT_FOUND = "Detalle de signo vital no encontrado.";
    public static final String INVALID_TOKEN = "Token no válido o expirado.";
    public static final String SEND_MAIL_ERROR = "Ocurrio un error al enviar el correo";
    public static final String NEW_MAIL_MESSAGE_NOTE = "Tienes un nuevo mensaje.";
    public static final String BALANCE_FILE_NAME = "Balance de %s de %s a %s";
    public static final String RECOVER_PASSWORD_SUBJECT = "Recuperar contraseña.";
    public static final String PATIENT_ALREADY_EXIST = "Ya existe un paciente registrado con el nombre que intentas registrar.";
    public static final String SAME_PASSWORD = "N/A";
}
