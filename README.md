# BiblioAndes — Sistema de Gestión y Préstamos de Biblioteca

> Aplicación móvil multiplataforma desarrollada con **Kotlin Multiplatform (KMP)** y **Compose Multiplatform** para Android e iOS, siguiendo estrictamente los principios de **Clean Architecture**, **MVVM con StateFlow** e **Inyección de Dependencias con Koin**.

---

## 1. Resumen del Proyecto

* **Institución:** Universidad Peruana Unión (UPeU) – EP Ingeniería de Sistemas.
* **Asignatura:** Desarrollo de Aplicaciones Móviles (Ciclo VI · 2026-2).
* **Desarrollador:** Andrey Mestanza ([@17Yerdna](https://github.com/17Yerdna)).
* **Caso de Negocio:** BiblioAndes (3 sedes, ~1800 estudiantes). Consulta de catálogo, solicitud de préstamos de libros y control de vencimientos con datos simulados en memoria (*zero network/DB persistence*).

---

## 2. Ecosistema Tecnológico

* **Lenguaje:** Kotlin 2.4.10 (Multiplatform `commonMain`, `androidMain`, `iosMain`).
* **UI Multiplataforma:** Compose Multiplatform 1.11.1 con Material 3 (`material3:1.11.0-alpha07`).
* **Inyección de Dependencias:** Koin Core & Compose 4.0.2.
* **Concurrencia:** Kotlinx Coroutines 1.10.1 (`Mutex` para thread-safety en memoria y `StateFlow` reactivo).
* **Fechas:** `kotlinx-datetime:0.6.1` para cálculos y reglas de vencimiento dinámicas.
* **Gestión de Versiones:** Gradle Version Catalog (`gradle/libs.versions.toml`).

---

## 3. Arquitectura y Estructura de Paquetes

Implementación canónica en `shared/src/commonMain/kotlin/pe/upeu/biblioandes/`:

```
pe/upeu/biblioandes/
├── domain/                      # Capa Pura de Negocio (Sin frameworks de UI)
│   ├── model/                  # Entidades: Libro, Prestamo, EstadoPrestamo, Estudiante
│   ├── repository/             # Interfaz de repositorio (BibliotecaRepository)
│   └── usecase/                # Casos de uso con reglas operativas RN-01 a RN-04
├── data/                        # Capa de Datos (Aislada)
│   ├── local/                  # DatosSimulados (En memoria, fechas dinámicas)
│   └── repository/             # BibliotecaRepositoryFake con Mutex y latencia simulada
├── presentation/                # Capa de Presentación (Compose UI + ViewModels)
│   ├── inicio/                 # RF-01: Saludo, tarjeta destacada préstamo y accesos rápidos
│   ├── catalogo/               # RF-02 y RF-05: LazyColumn, chips categorías y búsqueda
│   ├── detalle/                # RF-03: Información de libro, validación y diálogo confirmación
│   ├── prestamos/              # RF-04: Mis préstamos ordenados por vencimiento y filtros
│   ├── perfil/                 # RF-06: Datos estudiante, toggle tema y switch simulación de error
│   ├── navigation/             # RF-07: Scaffold, NavigationBar y gestión de retorno
│   └── theme/                  # Material 3 Design System (Claro / Oscuro)
└── di/                          # Inyección de Dependencias con Koin (AppModule.kt)
```

---

## 4. Instrucciones de Ejecución

### Pruebas Automatizadas:
```powershell
.\gradlew.bat :shared:testAndroidHostTest
```

### Compilar y Ejecutar en Android:
```powershell
.\gradlew.bat :androidApp:installDebug
adb shell am start -n pe.upeu.biblioandes/.MainActivity
```
