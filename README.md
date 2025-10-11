# BalanzApp

**Autores:** Angelica Amaya, Miriam Castillo, Diana Espinal
**Tecnologías:** JavaFX • Maven • PostgreSQL • JDBC • MVC

---

## Descripción general

**BalanzApp** es un sistema contable de escritorio desarrollado en **JavaFX**, que permite realizar autenticación de usuarios con roles y gestionar módulos contables básicos: **Libro Diario**, **Libro Mayor**, **Catálogo de Cuentas** y **Documentos**.

Incluye control de acceso según el tipo de usuario (**Administrador, Empleado y Auditor**).

---

## ⚙️ Funcionalidades principales

| Módulo | Descripción |
|--------|--------------|
| **Login y Roles** | Autenticación con conexión a PostgreSQL y control de permisos según nivel de acceso. |
| **Inicio** | Panel principal con menú lateral e identificación del usuario activo. |
| **Libro Diario** | Vista hasta el momento* |
| **Libro Mayor** | Vista hasta el momento* |

---

## Arquitectura del sistema

El proyecto sigue el patrón **MVC (Modelo–Vista–Controlador)**.

## Requisitos

**Java JDK 21+**

**JavaFX SDK 21+**

**PostgreSQL 14+**

**Maven 3.8+**

**IDE: IntelliJ IDEA con plugin de JavaFX**
---

## Clonar el Repositorio
Con las dependencias instaladas, clona el repositorio de BalanzApp en tu equipo utilizando Git. Ejecuta el siguiente comando en tu terminal o usa tu herramienta de Git preferida:
https://github.com/isabeCastillo/balanzApp.git
---
## Abrir en tu IDE

Abre el proyecto con IntelliJ IDEA (o tu IDE preferido) y asegúrate de:

Tener configurado el JDK 21+.

Añadir las librerías de JavaFX al módulo del proyecto.
--
## Configurar la base de datos

Crear una base de datos en PostgreSQL db_contables .

Importar el script SQL (query.sql).

Configurar las credenciales de conexión en el archivo ConexionDB cambia las contraseña de tu postgred (en la linea 13) y si es necesario en cambiar el user en la linea 12

Para establecer la conexión debes de estar atendo que en la linea 9 del url este el puerto espefico de tu postGres y seguido el nombre de tu base de datos que en est caso es db_contables
<img width="1516" height="644" alt="image" src="https://github.com/user-attachments/assets/6259863b-1e44-471d-a5bb-89b1083d4817" />

Para poder ejecutarlo solamente debes de localizarte en el archivo HelloApplication y dar clic en el boton run
<img width="976" height="464" alt="image" src="https://github.com/user-attachments/assets/98f303fa-6168-4a00-9da1-096fbd8c21ea" />

## **Con esto, tu proyecto se debería estar ejecutando sin problemas y mostrarte el Inicio de Sesión.**
<img width="1364" height="711" alt="image" src="https://github.com/user-attachments/assets/759a34f4-cbda-4637-9894-a632109a9dd0" />

##Si tienes algun problema que que te aparezca que te falta JDK lo que debes de hacer es que al clonarlo y abrirlo en el Intellij IDEA dale click a la pestaña que aparece en la parte inferior derecha de la pantalla y listo
