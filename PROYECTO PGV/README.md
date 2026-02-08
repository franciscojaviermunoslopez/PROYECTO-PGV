# Sistema de Gestión de Incidencias - Cliente-Servidor

## 📋 Descripción del Proyecto

Este es un sistema cliente-servidor en Java para gestionar incidencias. Permite a múltiples usuarios conectarse simultáneamente, crear incidencias, listarlas, cerrarlas y (si eres administrador) ver qué usuarios están conectados.

## 🚀 Usuarios Predefinidos

El sistema tiene estos usuarios ya configurados:

| Usuario | Contraseña | Rol |
|---------|-----------|-----|
| admin | admin123 | ADMINISTRADOR |
| juan | juan123 | USUARIO |
| maria | maria123 | USUARIO |

## 📁 Estructura del Proyecto

```
PROYECTO PGV/
└── src/
    ├── servidor/
    │   ├── Servidor.java           (Servidor principal)
    │   ├── ManejadorCliente.java   (Gestiona cada cliente en un hilo)
    │   ├── GestorIncidencias.java  (Gestiona las incidencias)
    │   ├── GestorUsuarios.java     (Gestiona usuarios y autenticación)
    │   ├── Incidencia.java         (Clase que representa una incidencia)
    │   ├── Usuario.java            (Clase que representa un usuario)
    │   └── ClienteConectado.java   (Info de clientes conectados)
    └── cliente/
        └── Cliente.java            (Aplicación cliente)
```

## 🔧 Cómo Compilar

### Opción 1: Compilar todo junto
```bash
cd "c:\Users\franc\Downloads\PROYECTO PGV"
javac -d bin src/servidor/*.java src/cliente/*.java
```

### Opción 2: Compilar por separado

**Servidor:**
```bash
cd "c:\Users\franc\Downloads\PROYECTO PGV"
javac -d bin src/servidor/*.java
```

**Cliente:**
```bash
cd "c:\Users\franc\Downloads\PROYECTO PGV"
javac -d bin src/cliente/*.java
```

## ▶️ Cómo Ejecutar

### 1. Ejecutar el Servidor (primero)
```bash
cd "c:\Users\franc\Downloads\PROYECTO PGV"
java -cp bin servidor.Servidor
```

Deberías ver:
```
=== SERVIDOR DE GESTIÓN DE INCIDENCIAS ===
Iniciando servidor en puerto 5000...
Servidor iniciado correctamente
Esperando conexiones de clientes...
```

### 2. Ejecutar el Cliente (en otra terminal)
```bash
cd "c:\Users\franc\Downloads\PROYECTO PGV"
java -cp bin cliente.Cliente
```

Deberías ver:
```
Conectando al servidor localhost:5000...
Conexión establecida correctamente
Bienvenido al Sistema de Gestión de Incidencias
```

## 🎮 Cómo Usar el Sistema

### 1. Iniciar Sesión
Cuando ejecutes el cliente, te pedirá usuario y contraseña:
```
Usuario: admin
Contraseña: admin123
```

### 2. Menú de Opciones

**Para USUARIOS normales:**
- **Opción 1 - ALTA**: Crear una nueva incidencia
- **Opción 2 - LISTAR**: Ver todas las incidencias
- **Opción 3 - CERRAR**: Cerrar una incidencia por ID
- **Opción 5 - SALIR**: Cerrar sesión

**Para ADMINISTRADORES** (además de las anteriores):
- **Opción 4 - CLIENTES**: Ver qué usuarios están conectados

### 3. Ejemplos de Uso

**Crear una incidencia:**
```
Selecciona una opción: 1
Descripción de la incidencia: No funciona el aire acondicionado
OK: Incidencia creada con ID 1
```

**Listar incidencias:**
```
Selecciona una opción: 2
=== LISTADO DE INCIDENCIAS ===
ID: 1 | No funciona el aire acondicionado | 08/02/2026 19:15:30 | Estado: ABIERTA | Usuario: admin
```

**Cerrar una incidencia:**
```
Selecciona una opción: 3
ID de la incidencia a cerrar: 1
OK: Incidencia 1 cerrada correctamente
```

**Ver clientes conectados (solo admin):**
```
Selecciona una opción: 4
=== CLIENTES CONECTADOS ===
Usuario: admin | IP: 127.0.0.1 | Rol: ADMINISTRADOR
Usuario: juan | IP: 127.0.0.1 | Rol: USUARIO
```

## 🎯 Comandos del Protocolo

| Comando | Formato | Descripción |
|---------|---------|-------------|
| LOGIN | `LOGIN <usuario> <contraseña>` | Iniciar sesión |
| ALTA | `ALTA <descripción>` | Crear incidencia |
| LISTAR | `LISTAR` | Ver todas las incidencias |
| CERRAR | `CERRAR <id>` | Cerrar incidencia por ID |
| CLIENTES | `CLIENTES` | Ver clientes conectados (solo admin) |
| SALIR | `SALIR` | Cerrar sesión |

## ✅ Requisitos Implementados

### Requisitos Obligatorios
- ✅ Arquitectura cliente-servidor
- ✅ Servidor multi-hilo (cada cliente en su propio hilo)
- ✅ Concurrencia segura (uso de `synchronized`)
- ✅ Validación de entradas
- ✅ Control de accesos por rol (USUARIO / ADMINISTRADOR)
- ✅ Gestión de incidencias (ALTA, LISTAR, CERRAR)
- ✅ Comando CLIENTES (solo admin)
- ✅ Autenticación básica
- ✅ Límite de clientes simultáneos (10 máximo)

## 🔍 Verificación del Sistema

### Prueba 1: Multi-cliente
1. Ejecuta el servidor
2. Ejecuta varios clientes (2-3) en diferentes terminales
3. Inicia sesión con diferentes usuarios en cada uno
4. Crea incidencias desde cada cliente
5. Lista las incidencias desde otro cliente (deberías ver todas)

### Prueba 2: Roles
1. Conéctate como usuario normal (juan/juan123)
2. Intenta ejecutar el comando CLIENTES
3. Deberías ver un error de permisos
4. Conéctate como admin (admin/admin123)
5. Ejecuta CLIENTES y verás los usuarios conectados

### Prueba 3: Concurrencia
1. Conecta 2 clientes
2. Desde ambos, crea incidencias al mismo tiempo
3. Lista desde ambos: deberías ver todas sin problemas
4. Esto demuestra que la concurrencia funciona correctamente

## 📝 Configuración

Si necesitas cambiar la configuración, edita estas constantes:

**En Servidor.java:**
```java
private static final int PUERTO = 5000;        // Puerto del servidor
private static final int MAX_CLIENTES = 10;    // Máximo de clientes
```

**En Cliente.java:**
```java
private static final String HOST = "localhost"; // IP del servidor
private static final int PUERTO = 5000;         // Puerto del servidor
```

## 🛠️ Solución de Problemas

**Error: "Connection refused"**
- Asegúrate de que el servidor esté ejecutándose primero
- Verifica que el puerto 5000 esté libre

**Error: "Cliente rechazado"**
- Se alcanzó el límite de 10 clientes conectados
- Espera a que algún cliente se desconecte

**No se ve la respuesta completa en LISTAR**
- Es normal, el cliente lee línea por línea
- El código está diseñado para leer respuestas multilínea

## 📚 Próximas Mejoras (Opcionales)

Para conseguir más puntos, puedes añadir:
- **EDITAR**: Comando para modificar incidencias ✅ **IMPLEMENTADO**
- **Persistencia**: Guardar incidencias en archivo ✅ **IMPLEMENTADO**
- **Logging**: Sistema de logs con niveles ✅ **IMPLEMENTADO**
- **JSON/API**: Autenticación con API externa ✅ **IMPLEMENTADO**
- **Excepciones**: Gestión más robusta de errores ✅ **IMPLEMENTADO**

---

## 🌟 FUNCIONALIDADES EXTRA IMPLEMENTADAS

### 1. Comando EDITAR (0.5 puntos)
Permite modificar la descripción de incidencias existentes:
```
Selecciona una opción: 4
ID de la incidencia a editar: 1
Nueva descripción: Problema solucionado
OK: Incidencia 1 editada correctamente
```

### 2. Sistema de Logging con Niveles (0.5 puntos)
Sistema profesional de logging con 4 niveles:
- **ERROR**: Errores críticos
- **WARNING**: Advertencias
- **INFO**: Información general
- **DEBUG**: Detalles de depuración

Los logs se guardan en `servidor_logs.txt` automáticamente.

### 3. Persistencia de Datos (0.5 puntos)
Las incidencias se guardan automáticamente en `incidencias.dat` después de cada ALTA.
Formato: CSV separado por pipes (|)

### 4. Gestión Robusta de Excepciones (0.5 puntos)
Manejo inteligente de errores:
- Múltiples niveles de captura de excepciones
- El servidor no se cae ante errores inesperados
- Mensajes claros al usuario sobre problemas de red/servidor

### 5. Autenticación con API y JSON (1.0 punto)
Autenticador que usa API REST externa (https://reqres.in):
- Peticiones HTTP POST con JSON
- Usuarios de prueba de la API:
  - `eve.holt@reqres.in` / `cityslicka` (ADMINISTRADOR)
  - `charles.morris@reqres.in` / `password123` (USUARIO)

**Ver `funcionalidades_extra.md` para detalles completos**

---
