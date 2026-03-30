# 🏥 Gestor de Balances de Diálisis

API REST desarrollada con **Spring Boot 3** para el registro y seguimiento clínico de pacientes en tratamiento de diálisis peritoneal. Permite llevar un control detallado del balance de líquidos, signos vitales, medicamentos y fluidos adicionales de cada paciente.

---

## 📋 ¿Qué hace este proyecto?

El sistema está pensado para personal médico (médicos, enfermeras) que necesitan:

- **Registrar y gestionar pacientes** en tratamiento de diálisis, incluyendo su tipo de bolsa de diálisis y estatus activo/inactivo.
- **Llevar el balance de líquidos**: registrar cuánto líquido fue infundido y cuánto fue drenado en cada sesión, con descripción y fecha.
- **Registrar fluidos extra**: como orina, líquidos ingeridos u otros fluidos fuera del proceso de diálisis.
- **Controlar signos vitales**: presión arterial, frecuencia cardíaca, temperatura, etc.
- **Gestionar detalles de signos vitales**: registro histórico por paciente y rango de fechas.
- **Gestionar medicamentos** asignados a cada paciente, incluyendo dosis y frecuencia de toma.
- **Gestionar detalles de medicamentos**: historial de prescripción con dosis, frecuencia y estado.
- **Gestionar tipos de bolsa de diálisis**: catalogar y consultar los tipos de bolsas disponibles.
- **Consultar fechas activas de fluidos**: obtener las fechas disponibles en los registros de balance.
- **Calcular el balance líquido neto** (infundido - drenado) por rango de fechas.
- **Generar reportes en PDF** mediante JasperReports con toda la información clínica.
- **Enviar notificaciones por correo electrónico** usando plantillas Thymeleaf y SendGrid.
- **Recuperar contraseña** mediante validación del correo registrado y envío de nueva clave.
- **Gestión de suscripciones y planes de pago** integrada con el microservicio externo de pagos (Stripe).
- **Notificaciones al usuario** para limpiar el historial de balances y hacer respaldos antes de una fecha límite.

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
| Spring Boot            | 3.5.11   | Framework base                           |
| Spring Security        | -        | Autenticación y autorización             |
| Spring Data JPA        | -        | Acceso a base de datos                   |
| Spring WebFlux         | -        | Comunicación reactiva con microservicios |
| PostgreSQL             | -        | Base de datos relacional                 |
| JWT (jjwt)             | 0.11.5   | Tokens de autenticación                  |
| JasperReports          | 7.0.3    | Generación de reportes PDF               |
| iTextPDF / OpenPDF     | 7.2.5    | Manipulación de PDFs                     |
| SpringDoc OpenAPI      | 2.8.5    | Documentación Swagger UI                 |
| Spring Mail + Thymeleaf| -        | Envío de correos con plantillas HTML     |
| SendGrid               | 4.10.2   | Envío de correos transaccionales         |
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
├── remote/          → Servicios remotos (integración con microservicio de pagos)
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
| Método | Ruta                   | Descripción                                        | Seguridad |
|--------|------------------------|----------------------------------------------------|-----------|
| GET    | `/public-key`          | Obtener clave pública RSA para cifrado             | Pública   |
| POST   | `/login`               | Iniciar sesión, retorna JWT                        | Pública   |
| GET    | `/logout`              | Invalidar token JWT (cerrar sesión)                | JWT       |
| GET    | `/validate/mail`       | Validar si un correo electrónico existe en el sistema | Pública |
| GET    | `/recover/password`    | Iniciar proceso de recuperación de contraseña por email | Pública |

### 👤 Usuarios — `/api/users`
| Método | Ruta                          | Descripción                        | Seguridad |
|--------|-------------------------------|------------------------------------|-----------|
| POST   | `/save`                       | Crear nuevo usuario                | Pública   |
| GET    | `/{userId}/update-password`   | Actualizar contraseña del usuario  | JWT       |

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

### 📅 Fechas de Fluidos — `/api/fluid-dates` ⭐ _Nuevo_
| Método | Ruta             | Descripción                                         | Seguridad |
|--------|------------------|-----------------------------------------------------|-----------|
| GET    | `/active-dates`  | Obtener todas las fechas activas de registros de fluidos | JWT  |

### 🩺 Signos Vitales — `/api/vital-signs`
| Método | Ruta               | Descripción                        | Seguridad |
|--------|--------------------|------------------------------------|-----------|
| POST   | `/save`            | Registrar nuevos signos vitales    | JWT       |
| GET    | `/`                | Obtener todos los signos vitales   | JWT       |
| PATCH  | `/{vitalSignId}`   | Actualizar signos vitales          | JWT       |
| DELETE | `/{vitalSignId}`   | Eliminar signos vitales            | JWT       |

### 📊 Detalles de Signos Vitales — `/api/vital-signs/details` ⭐ _Nuevo_
| Método | Ruta                                         | Descripción                                                    | Seguridad |
|--------|----------------------------------------------|----------------------------------------------------------------|-----------|
| POST   | `/save`                                      | Registrar nuevo detalle de signo vital                         | JWT       |
| PATCH  | `/{vitalSignDetailId}`                       | Actualizar detalle de signo vital                              | JWT       |
| DELETE | `/{vitalSignDetailId}`                       | Eliminar detalle de signo vital                                | JWT       |
| GET    | `/patients/{patientId}/actual-date`          | Obtener detalles de signos vitales de un paciente en fecha actual | JWT    |
| GET    | `/patients/{patientId}/dates`                | Obtener detalles de signos vitales por rango de fechas         | JWT       |

### 💊 Medicamentos — `/api/medicines`
| Método | Ruta             | Descripción                        | Seguridad |
|--------|------------------|------------------------------------|-----------|
| POST   | `/save`          | Registrar nuevo medicamento        | JWT       |
| GET    | `/`              | Obtener todos los medicamentos     | JWT       |
| PATCH  | `/{medicineId}`  | Actualizar medicamento             | JWT       |
| DELETE | `/{medicineId}`  | Eliminar medicamento               | JWT       |

### 💊 Detalles de Medicamentos — `/api/medicines/details` ⭐ _Nuevo_
| Método | Ruta                          | Descripción                                      | Seguridad |
|--------|-------------------------------|--------------------------------------------------|-----------|
| POST   | `/save`                       | Registrar nuevo detalle de medicamento (dosis, frecuencia) | JWT |
| PATCH  | `/{medicineDetailId}`         | Actualizar detalle de medicamento                | JWT       |
| DELETE | `/{medicineDetailId}`         | Eliminar detalle de medicamento                  | JWT       |
| GET    | `/patients/{patientId}`       | Obtener detalles de medicamentos por paciente    | JWT       |

### 🧃 Fluidos Extra — `/api/extra-fluids`
| Método | Ruta                | Descripción                          | Seguridad |
|--------|---------------------|--------------------------------------|-----------|
| POST   | `/save`             | Registrar fluido extra               | JWT       |
| PATCH  | `/{extraFluidId}`   | Actualizar fluido extra              | JWT       |
| DELETE | `/{extraFluidId}`   | Eliminar fluido extra                | JWT       |
| GET    | `/by-date`          | Obtener fluidos extra por fecha      | JWT       |

### 🩹 Tipos de Bolsa — `/api/bag-types` ⭐ _Nuevo_
| Método | Ruta    | Descripción                                | Seguridad |
|--------|---------|--------------------------------------------|-----------|
| POST   | `/save` | Registrar nuevo tipo de bolsa de diálisis  | JWT       |
| GET    | `/`     | Obtener todos los tipos de bolsa           | JWT       |

### 🔔 Notificaciones — `/api/notifications` ⭐ _Nuevo_
| Método | Ruta                           | Descripción                                                              | Seguridad |
|--------|--------------------------------|--------------------------------------------------------------------------|-----------|
| GET    | `/users/balances/clean-history`| Obtener notificación de advertencia para limpiar historial de balances   | JWT       |

### 💳 Pagos — `/api/payments` ⭐ _Nuevo_
| Método | Ruta            | Descripción                                          | Seguridad |
|--------|-----------------|------------------------------------------------------|-----------|
| POST   | `/`             | Crear nueva suscripción con el precio indicado       | JWT       |
| POST   | `/cancel`       | Cancelar la suscripción activa del usuario           | JWT       |
| POST   | `/change-plan`  | Cambiar el plan de suscripción actual                | JWT       |
| POST   | `/change-cards` | Actualizar la tarjeta de pago de la suscripción      | JWT       |

### 📦 Planes — `/api/plans` ⭐ _Nuevo_
| Método | Ruta | Descripción                            | Seguridad |
|--------|------|----------------------------------------|-----------|
| GET    | `/`  | Obtener lista de planes disponibles    | JWT       |

### 🔄 Suscripciones — `/api/subscriptions` ⭐ _Nuevo_
| Método | Ruta                       | Descripción                                         | Seguridad |
|--------|----------------------------|-----------------------------------------------------|-----------|
| GET    | `/users/exist-subscription`| Verificar si el usuario tiene una suscripción activa| JWT       |
| GET    | `/users/subscription`      | Obtener detalles de la suscripción del usuario      | JWT       |

---

## 🔌 Integración con Microservicio de Pagos ⭐ _Nuevo_

Este servicio se comunica de forma reactiva (WebFlux) con el microservicio externo **`api-payments`** para gestionar todo lo relacionado con pagos y suscripciones mediante **Stripe**. La URL base y los paths se configuran en `application.yaml`:

```yaml
subscription:
  api:
    host:
      uri: "http://localhost:8084"
    create:
      path: "/api/subscriptions/create"
    cancel:
      path: "/api/subscriptions/cancel"
    change:
      path: "/api/subscriptions/change-plan"
    exist:
      path: "/api/subscriptions/exist-subscription"
    cards:
      path: "/api/subscriptions/change-cards"
    user:
      path: "/api/subscriptions/users/active"
```

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

# SendGrid (correos transaccionales)
SENDGRID_API_KEY=tu_sendgrid_api_key

# Puerto del servidor (opcional, default: 8082)
PORT=8082
```
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

La aplicación estará disponible en: `http://localhost:8082`

---

## 🐳 Docker

El proyecto incluye un `Dockerfile` multi-stage para construir y ejecutar la aplicación en un contenedor:

```bash
# Construir la imagen
docker build -t gestor-balances-dialisis .

# Ejecutar el contenedor
docker run -p 8082:8082 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://host:5432/gestor_dialisis \
  -e SPRING_DATASOURCE_USERNAME=postgres \
  -e SPRING_DATASOURCE_PASSWORD=tu_password \
  gestor-balances-dialisis
```

---

## 📖 Documentación de la API (Swagger UI)

Una vez ejecutada la aplicación, accede a la documentación interactiva:

```
http://localhost:8082/swagger-ui.html
```

---

## 🗂️ Modelos de la Base de Datos

| Entidad           | Tabla                  | Descripción                                                      |
|-------------------|------------------------|------------------------------------------------------------------|
| `User`            | `usuario`              | Usuarios del sistema (médicos/enfermeras)                        |
| `Patient`         | `paciente`             | Pacientes en tratamiento de diálisis                             |
| `BagType`         | `tipo_bolsa`           | Tipos de bolsa de diálisis ⭐                                   |
| `FluidBalance`    | `balance_liquido`      | Registros de balance de líquidos                                 |
| `FluidDate`       | `fecha_fluido`         | Fechas de registro de fluidos                                    |
| `ExtraFluid`      | `fluido_extra`         | Fluidos adicionales (orina, ingesta, etc.)                       |
| `VitalSign`       | `signo_vital`          | Catálogo de signos vitales                                       |
| `VitalSignDetail` | `detalle_signo_vital`  | Registros históricos de medición de signos vitales por paciente ⭐ |
| `Medicine`        | `medicamento`          | Catálogo de medicamentos del usuario                             |
| `MedicineDetail`  | `detalle_medicina`     | Historial de prescripciones: dosis y frecuencia por paciente ⭐  |
| `MailTemplate`    | -                      | Plantillas de correo electrónico ⭐                             |

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

