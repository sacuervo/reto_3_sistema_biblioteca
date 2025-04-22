# Reto 3 - Sistema de Biblioteca

---

:warning: Las clases modelo, repositorio y servicio fueron diseñadas y determinadas por el equipo docente de Dev Senior. Todos los métodos del LibraryService, a excepción del del `addBook()` fueron implementados por mí. Las pruebas unitarias también son de mi autoría.

Este proyecto es un sistema de biblioteca diseñado para gestionar usuarios, libros y préstamos. Está estructurado en capas para facilitar la organización y el mantenimiento del código. Además, incluye pruebas unitarias para garantizar la calidad del software.

---

## Estructura del Proyecto

El proyecto está dividido en las siguientes capas y componentes principales:

### Modelos

- **User**: Representa a un usuario del sistema.
- **Book**: Representa un libro disponible en la biblioteca.
- **Loan**: Representa un préstamo realizado por un usuario.

### Repositorio

- **BookRepository**: Maneja las operaciones relacionadas con los libros en la base de datos.
- **LoanRepository**: Maneja las operaciones relacionadas con los préstamos en la base de datos.

### Servicio

- **LibraryService**: Contiene la lógica de negocio del sistema, como la gestión de préstamos y la validación de reglas.

### Excepciones

- **LoanException**: Excepción personalizada que extiende `Exception` para manejar errores relacionados con los préstamos.

---

## Sistema de Logs

Se implementó un sistema de logs utilizando las siguientes librerías:

- **SLF4J**: Como fachada de logging para una abstracción de alto nivel.
- **Log4j2**: Como implementación de logging para el registro de eventos y errores.

### Configuración

La configuración de Log4j2 se encuentra en el archivo `src/main/resources/log4j2.xml`, donde se definen:

- **Appenders**: Para la salida de logs a la consola y archivos.
- **Loggers**: Con niveles específicos para diferentes paquetes.
- **Root Logger**: Configuración global del nivel de log.

### Uso en el Código

Los logs se implementan en las clases principales del sistema:

```java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LibraryService {
    private static final Logger logger = LoggerFactory.getLogger(LibraryService.class);

    // Métodos con logs
    public void borrowBook(int userId, int bookId) throws LoanException {
        logger.info("Attempting to borrow book with ID {} for user {}", bookId, userId);
        // ...implementación
    }
}
```

---

## Pruebas Unitarias

Se implementaron pruebas unitarias para la clase `LibraryService` utilizando las siguientes herramientas:

- **Mockito**: Para simular dependencias y verificar interacciones.
- **JUnit**: Para escribir y ejecutar las pruebas unitarias.
- **JaCoCo**: Para medir la cobertura de las pruebas unitarias.

### Detalles de las Pruebas

- Se crearon pruebas unitarias únicamente para la clase `LibraryService`.
- No se implementaron pruebas unitarias para las capas de repositorio ni para los modelos.
- Las pruebas incluyen casos de éxito y manejo de excepciones, como usuarios no encontrados, libros no disponibles o préstamos inexistentes.

---

## Ejecución del Proyecto

Para ejecutar el proyecto, sigue los siguientes pasos:

1. Asegúrate de tener instalado **Java 21** y **Maven** en tu sistema.
2. Clona este repositorio en tu máquina local.
3. Navega al directorio raíz del proyecto.
4. Ejecuta el siguiente comando para compilar, ejecutar las pruebas y generar el `site` report de JaCoCo:

   ```bash
   mvn clean verify site
   ```
