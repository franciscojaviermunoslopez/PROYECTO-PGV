# Guía de Pruebas del Sistema - Paso a Paso

Esta guía te muestra cómo probar **TODAS** las funcionalidades implementadas en el proyecto.

---

## 📝 PASO 1: Compilar el Proyecto

Abre **PowerShell** o **CMD** en la carpeta del proyecto:

```powershell
cd "c:\Users\franc\Downloads\PROYECTO PGV"
```

### Compilar todo:
```powershell
javac -d bin src/servidor/*.java src/cliente/*.java
```

Si ves errores, asegúrate de tener Java instalado. Verifica con:
```powershell
java -version
javac -version
```

---

## 🚀 PASO 2: Ejecutar el Servidor

En la **primera terminal/ventana** de PowerShell:

```powershell
cd "c:\Users\franc\Downloads\PROYECTO PGV"
java -cp bin servidor.Servidor
```

### ✅ Deberías ver:
```
[2026-02-08 19:45:00] [INFO] === SERVIDOR DE GESTIÓN DE INCIDENCIAS ===
[2026-02-08 19:45:00] [INFO] Iniciando servidor en puerto 5000...
[2026-02-08 19:45:00] [INFO] Servidor iniciado correctamente
[2026-02-08 19:45:00] [INFO] Esperando conexiones de clientes...
[2026-02-08 19:45:00] [INFO] Máximo de clientes: 10
```

**✨ FUNCIONALIDAD EXTRA PROBADA:** Sistema de logging - Los mensajes tienen timestamp y nivel [INFO]

### 📄 Verifica el archivo de log:
Busca en la carpeta del proyecto el archivo **`servidor_logs.txt`** - debería existir ahora.

---

## 💻 PASO 3: Ejecutar el Cliente 1 (Usuario Normal)

Abre una **SEGUNDA terminal/ventana** de PowerShell:

```powershell
cd "c:\Users\franc\Downloads\PROYECTO PGV"
java -cp bin cliente.Cliente
```

### ✅ Deberías ver:
```
Conectando al servidor localhost:5000...
Conexión establecida correctamente

Bienvenido al Sistema de Gestión de Incidencias
Por favor, inicie sesión con: LOGIN <usuario> <contraseña>

=== INICIO DE SESIÓN ===
Usuario: 
```

---

## 🧪 PRUEBAS BÁSICAS

### 1️⃣ Probar LOGIN con usuario normal

```
Usuario: juan
Contraseña: juan123
```

### ✅ Deberías ver:
```
✓ Inicio de sesión exitoso
Rol: USUARIO

=== MENÚ DE OPCIONES ===
1. ALTA - Crear nueva incidencia
2. LISTAR - Ver todas las incidencias
3. CERRAR - Cerrar una incidencia
4. EDITAR - Editar una incidencia
6. SALIR - Cerrar sesión
```

**✨ FUNCIONALIDAD EXTRA PROBADA:** ¡Observa que aparece la opción 4. EDITAR!

### En el servidor deberías ver:
```
[2026-02-08 19:46:00] [INFO] Nuevo cliente conectado desde: 127.0.0.1
[2026-02-08 19:46:00] [INFO] Cliente conectado desde 127.0.0.1
[2026-02-08 19:46:15] [INFO] Login exitoso: juan (USUARIO) desde 127.0.0.1
[2026-02-08 19:46:15] [INFO] Usuario autenticado: juan (USUARIO)
```

---

### 2️⃣ Probar ALTA (Crear incidencia)

En el cliente:
```
Selecciona una opción: 1
Descripción de la incidencia: El aire acondicionado no funciona
```

### ✅ Deberías ver:
```
OK: Incidencia creada con ID 1
```

### En el servidor deberías ver:
```
[2026-02-08 19:47:00] [INFO] Nueva incidencia creada: ID=1 por juan
[2026-02-08 19:47:00] [INFO] Incidencias guardadas en disco: 1 registros
```

**✨ FUNCIONALIDAD EXTRA PROBADA:** Persistencia - Se guarda automáticamente

### 📄 Verifica el archivo de persistencia:
Busca el archivo **`incidencias.dat`** en la carpeta del proyecto. Ábrelo con Notepad y deberías ver:
```
1|El aire acondicionado no funciona|2026-02-08 19:47:00|ABIERTA|juan
```

---

### 3️⃣ Crear más incidencias

Crea 2-3 incidencias más para tener datos:
```
Opción: 1
Descripción: Impresora atascada
OK: Incidencia creada con ID 2

Opción: 1
Descripción: Problema con el wifi
OK: Incidencia creada con ID 3
```

---

### 4️⃣ Probar LISTAR

```
Selecciona una opción: 2
```

### ✅ Deberías ver:
```
=== LISTADO DE INCIDENCIAS ===
ID: 1 | El aire acondicionado no funciona | 08/02/2026 19:47:00 | Estado: ABIERTA | Usuario: juan
ID: 2 | Impresora atascada | 08/02/2026 19:48:00 | Estado: ABIERTA | Usuario: juan
ID: 3 | Problema con el wifi | 08/02/2026 19:48:30 | Estado: ABIERTA | Usuario: juan
```

---

### 5️⃣ Probar EDITAR ✨ (FUNCIONALIDAD EXTRA)

```
Selecciona una opción: 4
ID de la incidencia a editar: 1
Nueva descripción: Aire acondicionado reparado
```

### ✅ Deberías ver:
```
OK: Incidencia 1 editada correctamente
```

### En el servidor:
```
[2026-02-08 19:49:00] [INFO] Usuario juan editó incidencia 1
```

### Verificar que se editó:
```
Opción: 2
```
Deberías ver que la incidencia 1 ahora tiene la nueva descripción.

---

### 6️⃣ Probar CERRAR

```
Selecciona una opción: 3
ID de la incidencia a cerrar: 2
```

### ✅ Deberías ver:
```
OK: Incidencia 2 cerrada correctamente
```

### En el servidor:
```
[2026-02-08 19:50:00] [INFO] Usuario juan cerró incidencia 2
```

### Verificar con LISTAR:
```
Opción: 2
```
La incidencia 2 debería mostrar `Estado: CERRADA`

---

## 👑 PRUEBAS CON ADMINISTRADOR

### 7️⃣ Abrir un TERCER cliente (Administrador)

Abre una **TERCERA terminal**:

```powershell
cd "c:\Users\franc\Downloads\PROYECTO PGV"
java -cp bin cliente.Cliente
```

### Login como admin:
```
Usuario: admin
Contraseña: admin123
```

### ✅ Deberías ver:
```
✓ Inicio de sesión exitoso
Rol: ADMINISTRADOR

=== MENÚ DE OPCIONES ===
1. ALTA - Crear nueva incidencia
2. LISTAR - Ver todas las incidencias
3. CERRAR - Cerrar una incidencia
4. EDITAR - Editar una incidencia
5. CLIENTES - Ver clientes conectados (Solo Admin)  ← ¡NUEVA OPCIÓN!
6. SALIR - Cerrar sesión
```

---

### 8️⃣ Probar CLIENTES (Solo Admin)

```
Selecciona una opción: 5
```

### ✅ Deberías ver:
```
=== CLIENTES CONECTADOS ===
Usuario: juan | IP: 127.0.0.1 | Rol: USUARIO
Usuario: admin | IP: 127.0.0.1 | Rol: ADMINISTRADOR
```

**✅ PROBADO:** Control de acceso por roles - Solo admin puede ver esto

---

### 9️⃣ Intentar CLIENTES desde el cliente normal (juan)

Ve al cliente de juan (segunda terminal) e intenta:
```
Selecciona una opción: 5
```

### ✅ Deberías ver:
```
ERROR: Opción no válida
```

Porque juan no es administrador, la opción 5 no existe en su menú.

**✅ PROBADO:** El cliente respeta el rol y oculta opciones de admin

---

## 🛡️ PROBAR GESTIÓN DE EXCEPCIONES

### 🔟 Probar validación de entradas

En cualquier cliente:

**Comando vacío:**
```
Opción: 1
Descripción de la incidencia: [déjalo vacío, solo ENTER]
```
Deberías ver: `ERROR: La descripción no puede estar vacía`

**ID inválido:**
```
Opción: 3
ID de la incidencia a cerrar: abc
```
Servidor debería responder: `ERROR: El ID debe ser un número`

**ID que no existe:**
```
Opción: 3
ID de la incidencia a cerrar: 999
```
Debería responder: `ERROR: No se encontró la incidencia con ID 999`

**✨ FUNCIONALIDAD EXTRA PROBADA:** Gestión robusta de excepciones

---

## 💾 PROBAR PERSISTENCIA

### 1️⃣ Detener el servidor

En la terminal del servidor, presiona **Ctrl + C**

Deberías ver al final:
```
[2026-02-08 19:55:00] [INFO] Sistema de logging cerrado
```

### 2️⃣ Verificar el archivo de datos

Abre con Notepad: **`incidencias.dat`**

Deberías ver todas las incidencias guardadas en formato CSV:
```
1|Aire acondicionado reparado|2026-02-08 19:47:00|ABIERTA|juan
2|Impresora atascada|2026-02-08 19:48:00|CERRADA|juan
3|Problema con el wifi|2026-02-08 19:48:30|ABIERTA|juan
```

### 3️⃣ Reiniciar el servidor

```powershell
java -cp bin servidor.Servidor
```

### 4️⃣ Conectar un cliente y listar

```powershell
java -cp bin cliente.Cliente
```
Login y LISTAR... 

**NOTA:** En la implementación actual, las incidencias NO se cargan automáticamente al iniciar (esa parte está comentada en el servidor). Para activarla, necesitarías descomentar el código en `Servidor.java`. Pero el sistema de guardado SÍ funciona.

**✨ FUNCIONALIDAD EXTRA PROBADA:** Sistema de persistencia implementado

---

## 🌐 PROBAR AUTENTICACIÓN CON API (OPCIONAL)

**NOTA:** Esta funcionalidad está implementada en `AutenticadorAPI.java` pero NO está activada por defecto (usa autenticación local).

### Para activarla:

1. Modifica `GestorUsuarios.java`, método `autenticar()`:
```java
public Usuario autenticar(String nombreUsuario, String contrasena) {
    // Intentar con API primero
    if (AutenticadorAPI.autenticarConAPI(nombreUsuario, contrasena)) {
        String rol = AutenticadorAPI.determinarRol(nombreUsuario);
        return new Usuario(nombreUsuario, contrasena, rol);
    }
    return null;
}
```

2. Recompila y ejecuta

3. Usa estos usuarios de la API:
```
Usuario: eve.holt@reqres.in
Contraseña: cityslicka
```

**✨ FUNCIONALIDAD EXTRA IMPLEMENTADA:** Autenticación con API REST y JSON

---

## 📊 REVISAR LOS LOGS

Abre **`servidor_logs.txt`** con Notepad.

Deberías ver un registro completo de TODO lo que pasó:

```
[2026-02-08 19:45:00] [INFO] Sistema de logging inicializado
[2026-02-08 19:45:00] [INFO] === SERVIDOR DE GESTIÓN DE INCIDENCIAS ===
[2026-02-08 19:45:00] [INFO] Servidor iniciado correctamente
[2026-02-08 19:46:00] [INFO] Nuevo cliente conectado desde: 127.0.0.1
[2026-02-08 19:46:15] [INFO] Login exitoso: juan (USUARIO) desde 127.0.0.1
[2026-02-08 19:47:00] [INFO] Nueva incidencia creada: ID=1 por juan
[2026-02-08 19:49:00] [INFO] Usuario juan editó incidencia 1
[2026-02-08 19:50:00] [INFO] Usuario juan cerró incidencia 2
...
```

**✅ PROBADO:** Sistema de logging con niveles funcionando perfectamente

---

## ✅ CHECKLIST DE PRUEBAS COMPLETADAS

### Funcionalidades Básicas:
- [ ] Servidor se inicia correctamente
- [ ] Cliente se conecta al servidor
- [ ] LOGIN funciona (usuarios: admin, juan, maria)
- [ ] ALTA crea incidencias
- [ ] LISTAR muestra todas las incidencias
- [ ] CERRAR cambia el estado a CERRADA
- [ ] CLIENTES solo funciona para admin
- [ ] Roles funcionan correctamente (USUARIO vs ADMINISTRADOR)
- [ ] Múltiples clientes simultáneos funcionan
- [ ] SALIR desconecta correctamente

### Funcionalidades Extra:
- [ ] EDITAR modifica descripción de incidencias
- [ ] Logging registra eventos en servidor_logs.txt
- [ ] Logging tiene niveles (INFO, WARNING, ERROR)
- [ ] Persistencia guarda en incidencias.dat
- [ ] Persistencia se activa tras cada ALTA
- [ ] Validación de entradas (campos vacíos)
- [ ] Gestión de errores (IDs inválidos, números incorrectos)
- [ ] AutenticadorAPI implementado (aunque no activado por defecto)

---

## 🎬 PRUEBA FINAL: TODO JUNTO

1. **Ejecuta el servidor**
2. **Abre 3 clientes:**
   - Cliente 1: Login como `juan` (USUARIO)
   - Cliente 2: Login como `maria` (USUARIO)
   - Cliente 3: Login como `admin` (ADMINISTRADOR)
3. **Desde juan:** Crear 2 incidencias
4. **Desde maria:** Crear 1 incidencia
5. **Desde admin:** Ver CLIENTES (deberías ver los 3 conectados)
6. **Desde juan:** EDITAR una incidencia
7. **Desde maria:** LISTAR (debería ver las 3 incidencias, incluida la editada)
8. **Desde admin:** CERRAR una incidencia
9. **Revisar:** `servidor_logs.txt` - debería tener todos los eventos
10. **Revisar:** `incidencias.dat` - debería tener las 3 incidencias

**Si todo esto funciona, tu proyecto está PERFECTO para presentar.** 🎉

---

## 💡 TIPS PARA LA PRESENTACIÓN

- Ejecuta el servidor primero, luego los clientes
- Ten preparados los usuarios: admin/admin123, juan/juan123, maria/maria123
- Abre el `servidor_logs.txt` en Notepad++ para mostrarlo en tiempo real
- Abre `incidencias.dat` para mostrar cómo se guarda
- Ten varias terminales abiertas para demostrar multi-cliente

**¡Buena suerte con la presentación!** 🍀
