# 🏥 Gestor de Balances de Diálisis

API REST desarrollada con **Spring Boot 3** para el registro y seguimiento clínico de pacientes en tratamiento de diálisis peritoneal. Permite llevar un control detallado del balance de líquidos, signos vitales, medicamentos y fluidos adicionales de cada paciente.

---

## 📋 ¿Qué hace este proyecto?

El sistema está pensado para personal médico (médicos, enfermeras) que necesitan:

- **Registrar y gestionar pacientes** en tratamiento de diálisis, incluyendo su tipo de bolsa de diálisis y estatus activo/inactivo.
- **Llevar el balance de líquidos**: registrar cuánto líquido fue infundido y cuánto fue drenado en cada sesión, con descripción y fecha.
- **Registrar fluidos extra**: como orina, líquidos ingeridos u otros fluidos fuera del proceso de diálisis.
- **Controlar signos vitales**: presión arterial, frecuencia cardíaca, temperatura, etc.
- **Gestionar medicamentos** asignados a cada paciente.
- **Calcular el balance líquido neto** (infundido - drenado) por rango de fechas.
- **Generar reportes en PDF** mediante JasperReports con toda la información clínica.
- **Enviar notificaciones por correo electrónico** usando plantillas Thymeleaf.

---

## 🔐 Seguridad

- Autenticación basada en **JWT (JSON Web Tokens)**.
- Contraseñas protegidas con **BCrypt**.
- **Cifrado RSA** para el envío seguro de contraseñas desde el frontend:
  - El backend genera un par de claves RSA al iniciar.
  - El frontend obtiene la clave pública vía `GET /api/auth/public-key`.
  - El frontend cifra la contraseña con esa clave antes de enviarla.
  - El backend la descifra con la clave privada antes de validarla.
- Roles de usuario: **ADMIN** y **USER**.
- Logout mediante invalidación del token (por versión de token).

---

## 🛠️ Stack Tecnológico

| Tecnología             | Versión  | Uso                                      |
|------------------------|----------|------------------------------------------|
| Java                   | 17       | Lenguaje principal                       |
| Spring Boot            | 3.4.2    | Framework base                           |
| Spring Security        | -        | Autenticación y autorización             |
| Spring Data JPA        | -        | Acceso a base de datos                   |
| PostgreSQL             | -        | Base de datos relacional                 |
| JWT (jjwt)             | 0.11.5   | Tokens de autenticación                  |
| JasperReports          | 7.0.3    | Generación de reportes PDF               |
| iTextPDF / OpenPDF     | 7.2.5    | Manipulación de PDFs                     |
| SpringDoc OpenAPI      | 2.8.5    | Documentación Swagger UI                 |
| Spring Mail + Thymeleaf| -        | Envío de correos con plantillas HTML     |
| Lombok                 | -        | Reducción de boilerplate                 |
| Maven                  | -        | Gestor de dependencias y build           |

---

## 📁 Estructura del Proyecto

```
src/main/java/com/gestor_balance_dialisis/gestor_balance_dialisis/
├── config/          → Configuración de seguridad, CORS, Swagger
├── controller/      → Controladores REST (endpoints)
├── dto/             → Objetos de transferencia de datos (Request/Response)
├── entity/          → Entidades JPA (tablas de la base de datos)
├── enums/           → Enumeraciones (UserRol, StatusEnum)
├── exception/       → Manejo global de excepciones
├── repository/      → Repositorios JPA
├── security/        → JWT, RSA, UserDetailsService
├── service/         → Lógica de negocio
└── util/            → Utilidades generales

src/main/resources/
├── application.yaml     → Configuración de la aplicación
└── reports/             → Plantillas JasperReports (.jrxml / .jasper)
```

---

## 🔗 Endpoints Principales

### 🔑 Autenticación — `/api/auth`
| Método | Ruta              | Descripción                              | Seguridad |
|--------|-------------------|------------------------------------------|-----------|
| GET    | `/public-key`     | Obtener clave pública RSA para cifrado   | Pública   |
| POST   | `/login`          | Iniciar sesión, retorna JWT              | Pública   |
| GET    | `/logout`         | Invalidar token JWT (cerrar sesión)      | JWT       |

### 👤 Usuarios — `/api/users`
| Método | Ruta             | Descripción                        | Seguridad |
|--------|------------------|------------------------------------|-----------|
| POST   | `/save`          | Crear nuevo usuario                | Pública   |
| PATCH  | `/password`      | Actualizar contraseña              | JWT       |

### 🧑‍⚕️ Pacientes — `/api/patients`
| Método | Ruta              | Descripción                                  | Seguridad |
|--------|-------------------|----------------------------------------------|-----------|
| POST   | `/save`           | Registrar nuevo paciente                     | JWT       |
| GET    | `/users/{userId}` | Obtener pacientes de un usuario              | JWT       |
| PATCH  | `/{patientId}`    | Actualizar datos del paciente                | JWT       |
| DELETE | `/{patientId}`    | Eliminar paciente                            | JWT       |

### 💧 Balance de Líquidos — `/api/fluid-balances`
| Método | Ruta                    | Descripción                                     | Seguridad |
|--------|-------------------------|-------------------------------------------------|-----------|
| POST   | `/save`                 | Registrar nuevo balance                         | JWT       |
| PATCH  | `/{fluidBalanceId}`     | Actualizar balance                              | JWT       |
| DELETE | `/{fluidBalanceId}`     | Eliminar balance                                | JWT       |
| GET    | `/by-date`              | Obtener balances por rango de fechas y paciente | JWT       |
| GET    | `/calculate/{patientId}`| Calcular balance neto del paciente              | JWT       |
| GET    | `/report`               | Generar reporte PDF del balance                 | JWT       |

### 🩺 Signos Vitales — `/api/vital-signs`
| Método | Ruta               | Descripción                        | Seguridad |
|--------|--------------------|------------------------------------|-----------|
| POST   | `/save`            | Registrar nuevos signos vitales    | JWT       |
| GET    | `/`                | Obtener todos los signos vitales   | JWT       |
| PATCH  | `/{vitalSignId}`   | Actualizar signos vitales          | JWT       |
| DELETE | `/{vitalSignId}`   | Eliminar signos vitales            | JWT       |

### 💊 Medicamentos — `/api/medicines`
| Método | Ruta             | Descripción                        | Seguridad |
|--------|------------------|------------------------------------|-----------|
| POST   | `/save`          | Registrar nuevo medicamento        | JWT       |
| GET    | `/`              | Obtener todos los medicamentos     | JWT       |
| PATCH  | `/{medicineId}`  | Actualizar medicamento             | JWT       |
| DELETE | `/{medicineId}`  | Eliminar medicamento               | JWT       |

### 🧃 Fluidos Extra — `/api/extra-fluids`
| Método | Ruta                | Descripción                          | Seguridad |
|--------|---------------------|--------------------------------------|-----------|
| POST   | `/save`             | Registrar fluido extra               | JWT       |
| PATCH  | `/{extraFluidId}`   | Actualizar fluido extra              | JWT       |
| DELETE | `/{extraFluidId}`   | Eliminar fluido extra                | JWT       |
| GET    | `/by-date`          | Obtener fluidos extra por fecha      | JWT       |

---

## ⚙️ Variables de Entorno

El proyecto usa variables de entorno para no exponer credenciales sensibles. Crea un archivo `.env` o configúralas en tu sistema/servidor:

```env
# Base de datos PostgreSQL
PGHOST=localhost
PGPORT=5432
PGDATABASE=gestor_dialisis
PGUSER=tu_usuario
PGPASSWORD=tu_contrasena

# Correo electrónico (SMTP Gmail)
MAIL_USERNAME=tu_correo@gmail.com
MAIL_PASSWORD=tu_app_password

# Puerto del servidor (opcional, default: 8080)
PORT=8080
```

> **Nota:** Para Gmail, usa una [contraseña de aplicación](https://support.google.com/accounts/answer/185833), no tu contraseña normal.

---

## 🚀 Cómo ejecutar el proyecto

### Prerrequisitos
- Java 17+
- Maven 3.8+
- PostgreSQL

### Pasos

```bash
# 1. Clonar el repositorio
git clone <url-del-repositorio>
cd gestor-balances-dialisis

# 2. Configurar las variables de entorno (ver sección anterior)

# 3. Compilar y ejecutar
./mvnw spring-boot:run
```

La aplicación estará disponible en: `http://localhost:8080`

---

## 📖 Documentación de la API (Swagger UI)

Una vez ejecutada la aplicación, accede a la documentación interactiva:

```
http://localhost:8080/swagger-ui.html
```

---

## 🗂️ Modelos de la Base de Datos

| Entidad         | Tabla                  | Descripción                                 |
|-----------------|------------------------|---------------------------------------------|
| `User`          | `usuario`              | Usuarios del sistema (médicos/enfermeras)   |
| `Patient`       | `paciente`             | Pacientes en tratamiento de diálisis        |
| `BagType`       | `tipo_bolsa`           | Tipos de bolsa de diálisis                  |
| `FluidBalance`  | `balance_liquido`      | Registros de balance de líquidos            |
| `ExtraFluid`    | `fluido_extra`         | Fluidos adicionales (orina, ingesta, etc.)  |
| `VitalSign`     | `signo_vital`          | Registros de signos vitales                 |
| `Medicine`      | `medicamento`          | Medicamentos del paciente                   |
| `FluidDate`     | `fecha_fluido`         | Fechas de registro de fluidos               |

---

## 📄 Reportes PDF

El sistema genera reportes clínicos en PDF usando **JasperReports**. Los reportes incluyen:

- Resumen del balance de líquidos (infundido vs. drenado)
- Tabla de medicamentos
- Registro de signos vitales
- Datos del paciente

Las plantillas (`.jrxml`) se encuentran en `src/main/resources/reports/`.

---

## 👨‍💻 Autor

Desarrollado como sistema de apoyo clínico para el seguimiento de pacientes en diálisis peritoneal.

