# 📍 DÓNDE ESTÁN LOS USUARIOS Y LA API

Esta guía te muestra **exactamente dónde** está definido todo en el código.

---

## 👥 USUARIOS LOCALES

### Archivo: `src/servidor/GestorUsuarios.java`

**Líneas 17-24 - Constructor:**

```java
public GestorUsuarios() {
    usuarios = new HashMap<>();
    
    // Crear usuarios de prueba (puedes cambiar estos datos)
    usuarios.put("admin", new Usuario("admin", "admin123", "ADMINISTRADOR"));
    usuarios.put("juan", new Usuario("juan", "juan123", "USUARIO"));
    usuarios.put("maria", new Usuario("maria", "maria123", "USUARIO"));
}
```

### ✏️ Cómo añadir más usuarios locales:

Dentro del constructor, añade más líneas:

```java
usuarios.put("pedro", new Usuario("pedro", "pedro123", "USUARIO"));
usuarios.put("superadmin", new Usuario("superadmin", "super123", "ADMINISTRADOR"));
```

**Formato:**
```java
usuarios.put("nombre_usuario", new Usuario("nombre_usuario", "contraseña", "ROL"));
```

**Roles disponibles:**
- `"USUARIO"` - Usuario normal
- `"ADMINISTRADOR"` - Administrador con acceso a CLIENTES

---

## 🌐 AUTENTICACIÓN CON API

### Archivo: `src/servidor/AutenticadorAPI.java`

### 📍 URL de la API (Línea 19):

```java
private static final String API_URL = "https://reqres.in/api/login";
```

**Esta es una API pública gratuita de prueba:** https://reqres.in

### 👤 Usuarios válidos en la API:

**Definidos en la documentación de reqres.in:**

| Email | Contraseña | Rol en nuestro sistema |
|-------|-----------|------------------------|
| `eve.holt@reqres.in` | `cityslicka` | ADMINISTRADOR |
| Cualquier otro email de reqres.in | `password123` o similar de los propuestos por la API| USUARIO |

### 📍 Método de autenticación (Líneas 24-90):

```java
public static boolean autenticarConAPI(String email, String password) {
    try {
        // 1. Crear conexión HTTP a la API
        URL url = URI.create(API_URL).toURL();
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        
        // 2. Configurar como POST con JSON
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);
        
        // 3. Crear JSON: {"email": "usuario", "password": "contraseña"}
        String jsonInput = String.format(
            "{\"email\":\"%s\",\"password\":\"%s\"}", 
            email, 
            password
        );
        
        // 4. Enviar la petición
        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = jsonInput.getBytes("utf-8");
            os.write(input, 0, input.length);
        }
        
        // 5. Leer respuesta (200 = éxito, 400 = error)
        int codigoRespuesta = conn.getResponseCode();
        
        if (codigoRespuesta == 200) {
            Logger.info("Autenticación API exitosa para: " + email);
            return true;
        } else {
            Logger.warning("Autenticación API fallida");
            return false;
        }
        
    } catch (Exception e) {
        Logger.error("Error al conectar con la API: " + e.getMessage());
        return false;
    }
}
```

### 📍 Asignación de roles (Líneas 92-102):

```java
public static String determinarRol(String email) {
    // El primer usuario de la API será el administrador
    if (email.equals("eve.holt@reqres.in")) {
        return "ADMINISTRADOR";
    } else {
        return "USUARIO";
    }
}
```

### ✏️ Cómo añadir más administradores de la API:

Modifica el método `determinarRol()`:

```java
public static String determinarRol(String email) {
    // Lista de emails que son administradores
    if (email.equals("eve.holt@reqres.in") || 
        email.equals("otro_admin@reqres.in")) {
        return "ADMINISTRADOR";
    } else {
        return "USUARIO";
    }
}
```

---

## ⚙️ CÓMO FUNCIONA LA AUTENTICACIÓN AHORA

### Archivo: `src/servidor/GestorUsuarios.java`

**Método `autenticar()` actualizado (Líneas 26-57):**

```java
public Usuario autenticar(String nombreUsuario, String contrasena) {
    // 1️⃣ PRIMERO: Intentar con API REST
    Logger.info("Intentando autenticación con API para: " + nombreUsuario);
    
    if (AutenticadorAPI.autenticarConAPI(nombreUsuario, contrasena)) {
        String rol = AutenticadorAPI.determinarRol(nombreUsuario);
        Logger.info("✓ Autenticación con API exitosa");
        return new Usuario(nombreUsuario, contrasena, rol);
    }
    
    // 2️⃣ SEGUNDO: Si la API falla, usar usuarios locales (backup)
    Logger.info("API falló, intentando autenticación local...");
    Usuario usuario = usuarios.get(nombreUsuario);
    
    if (usuario != null && usuario.verificarContrasena(contrasena)) {
        Logger.info("✓ Autenticación local exitosa");
        return usuario;
    }
    
    // ❌ NINGUNO FUNCIONÓ
    Logger.warning("✗ Autenticación fallida");
    return null;
}
```

### 🔄 Flujo de autenticación:

```
Usuario introduce credenciales
         ↓
    ┌────────────────────┐
    │  GestorUsuarios   │
    │  .autenticar()    │
    └────────┬───────────┘
             ↓
    ┌────────────────────┐
    │  1. Probar API     │
    │  AutenticadorAPI   │
    └────────┬───────────┘
             ↓
      ¿API funcionó?
         ↙      ↘
      SÍ         NO
       ↓          ↓
    ✅ OK    ┌─────────────────┐
             │ 2. Probar local│
             │ usuarios map   │
             └────────┬────────┘
                      ↓
                ¿Usuario existe?
                  ↙      ↘
                SÍ         NO
                 ↓          ↓
               ✅ OK      ❌ ERROR
```

---

## 📝 RESUMEN

### Usuarios Locales (Backup):
- **Archivo:** `src/servidor/GestorUsuarios.java`
- **Línea:** 20-23
- **Usuarios:**
  - `admin` / `admin123` (ADMINISTRADOR)
  - `juan` / `juan123` (USUARIO)
  - `maria` / `maria123` (USUARIO)

### Autenticación API (Principal):
- **Archivo:** `src/servidor/AutenticadorAPI.java`
- **API URL:** `https://reqres.in/api/login` (Línea 19)
- **Usuario admin API:** `eve.holt@reqres.in` / `cityslicka`
- **Determinación de rol:** Línea 92-102

### Sistema de Autenticación:
- **Archivo:** `src/servidor/GestorUsuarios.java`
- **Método:** `autenticar()` - Líneas 33-57
- **Estrategia:** API primero → Usuarios locales segundo

---

## 🧪 CÓMO PROBAR

### Probar API (necesita internet):
```
Usuario: eve.holt@reqres.in
Contraseña: cityslicka
```
Verás en el servidor:
```
[INFO] Intentando autenticación con API para: eve.holt@reqres.in
[INFO] Autenticación API exitosa para: eve.holt@reqres.in
[INFO] ✓ Autenticación con API exitosa
```

### Probar usuarios locales (si API falla o sin internet):
```
Usuario: admin
Contraseña: admin123
```
Verás en el servidor:
```
[INFO] Intentando autenticación con API para: admin
[WARNING] Autenticación API fallida
[INFO] API falló, intentando autenticación local...
[INFO] ✓ Autenticación local exitosa: admin
```

---

## 💡 BENEFICIOS DE ESTE SISTEMA

✅ **Doble seguridad:** Si la API falla (internet caído, servidor API offline), el sistema sigue funcionando con usuarios locales

✅ **Fácil migración:** Puedes integrar cualquier API de autenticación real

✅ **Logs claros:** Puedes ver exactamente qué método de autenticación se usó

✅ **Flexibilidad:** Usuarios pueden usar email (API) o nombre de usuario (local)

**¡Ahora tu sistema tiene autenticación dual!** 🎉
