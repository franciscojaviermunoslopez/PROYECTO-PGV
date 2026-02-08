# 📚 EXPLICACIÓN COMPLETA DEL CÓDIGO - Línea por Línea

Este documento explica **TODO el código** de tu proyecto para que lo entiendas perfectamente.

---

# 📂 ESTRUCTURA DEL PROYECTO

```
PROYECTO PGV/
├── src/
│   ├── servidor/              ← Código del servidor
│   │   ├── Servidor.java      ← Programa principal del servidor
│   │   ├── ManejadorCliente.java  ← Maneja cada cliente conectado
│   │   ├── GestorIncidencias.java ← Gestiona las incidencias
│   │   ├── GestorUsuarios.java    ← Gestiona los usuarios
│   │   ├── Incidencia.java        ← Representa una incidencia
│   │   ├── Usuario.java           ← Representa un usuario
│   │   ├── ClienteConectado.java  ← Info de clientes conectados
│   │   ├── Logger.java            ← Sistema de logging
│   │   ├── Persistencia.java      ← Guardar/cargar datos
│   │   └── AutenticadorAPI.java   ← Autenticación con API
│   └── cliente/
│       └── Cliente.java       ← Programa del cliente
├── bin/                       ← Archivos compilados (.class)
├── incidencias.dat           ← Datos guardados
└── servidor_logs.txt         ← Logs del servidor
```

---

# 🖥️ PARTE 1: SERVIDOR

## 1️⃣ Servidor.java - El Cerebro del Servidor

**¿Qué hace?** Es el programa principal que escucha conexiones de clientes.

### Líneas importantes:

```java
private static final int PUERTO = 5000;
```
**Explicación:** El servidor escucha en el puerto 5000. Es como la "puerta" por donde entran los clientes.

```java
private GestorIncidencias gestorIncidencias;
private GestorUsuarios gestorUsuarios;
```
**Explicación:** Crea dos "gestores":
- `gestorIncidencias`: Maneja todas las incidencias
- `gestorUsuarios`: Maneja los usuarios

**Son compartidos** por todos los clientes (como una base de datos común).

```java
Logger.inicializar();
```
**Explicación:** Inicia el sistema de logging (para escribir en `servidor_logs.txt`).

```java
List<Incidencia> incidenciasGuardadas = Persistencia.cargar();
if (!incidenciasGuardadas.isEmpty()) {
    gestorIncidencias.cargarIncidencias(incidenciasGuardadas);
}
```
**Explicación:** 
1. Lee el archivo `incidencias.dat`
2. Si hay incidencias guardadas, las carga en memoria
3. **Resultado:** Las incidencias persisten aunque apagues el servidor

```java
ServerSocket serverSocket = new ServerSocket(PUERTO);
```
**Explicación:** Crea un "socket servidor" = abre la puerta en el puerto 5000.

```java
while (true) {
    Socket socketCliente = serverSocket.accept();
```
**Explicación:** 
- `while (true)` = bucle infinito, nunca para
- `accept()` = **ESPERA** a que un cliente se conecte (se queda bloqueado aquí)
- Cuando un cliente se conecta, continúa

```java
if (obtenerNumeroClientesConectados() >= MAX_CLIENTES) {
    socketCliente.close();
    continue;
}
```
**Explicación:** 
- Si ya hay 10 clientes (máximo), rechaza la conexión
- `continue` = vuelve al inicio del while para esperar otro cliente

```java
ManejadorCliente manejador = new ManejadorCliente(
    socketCliente, gestorIncidencias, gestorUsuarios, this
);
Thread hiloCliente = new Thread(manejador);
hiloCliente.start();
```
**Explicación:** 
1. Crea un `ManejadorCliente` para este cliente
2. Crea un **hilo** (thread) nuevo
3. `start()` = ejecuta el hilo en paralelo

**¿Por qué hilos?** Para que múltiples clientes puedan conectarse al mismo tiempo sin esperarse.

---

## 2️⃣ ManejadorCliente.java - Habla con Cada Cliente

**¿Qué hace?** Cada cliente tiene su propio `ManejadorCliente` que procesa sus comandos.

### Implementa Runnable:

```java
public class ManejadorCliente implements Runnable {
```
**Explicación:** `Runnable` = puede ejecutarse en un hilo. Debe tener un método `run()`.

### Variables importantes:

```java
private Socket socket;
private BufferedReader entrada;
private PrintWriter salida;
```
**Explicación:**
- `socket`: Conexión con el cliente
- `entrada`: Para LEER mensajes del cliente
- `salida`: Para ENVIAR mensajes al cliente

```java
private Usuario usuarioAutenticado;
```
**Explicación:** Guarda quién está autenticado. Si es `null`, no ha hecho login.

### El método run():

```java
public void run() {
    try {
        entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        salida = new PrintWriter(socket.getOutputStream(), true);
```
**Explicación:** 
- Configura los canales de entrada/salida
- `true` en PrintWriter = autoflush (envía inmediatamente)

```java
salida.println("Bienvenido al Sistema de Gestión de Incidencias");
salida.println("Por favor, inicie sesión con: LOGIN <usuario> <contraseña>");
```
**Explicación:** Envía mensajes de bienvenida al cliente.

```java
String comando;
while ((comando = entrada.readLine()) != null) {
    String respuesta = procesarComando(comando);
    salida.println(respuesta);
}
```
**Explicación:**
1. `entrada.readLine()` = **ESPERA** a que el cliente envíe un comando
2. Cuando llega, llama a `procesarComando()`
3. Envía la respuesta al cliente
4. Vuelve a esperar (bucle)

### Método procesarComando():

```java
private String procesarComando(String comando) {
    String[] partes = comando.split(" ", 2);
    String comandoPrincipal = partes[0].toUpperCase();
    
    switch (comandoPrincipal) {
        case "LOGIN":
            return procesarLogin(partes);
        case "ALTA":
            return procesarAlta(partes);
        // ... más casos
    }
}
```
**Explicación:**
1. `split(" ", 2)` = divide el comando en máximo 2 partes
   - Ejemplo: `"ALTA Problema wifi"` → `["ALTA", "Problema wifi"]`
2. `toUpperCase()` = convierte a mayúsculas (ALTA = alta = Alta)
3. `switch` = elige qué hacer según el comando

### Método procesarLogin():

```java
private String procesarLogin(String[] partes) {
    if (usuarioAutenticado != null) {
        return "ERROR: Ya has iniciado sesión";
    }
    
    String nombreUsuario = partes[1];
    String contrasena = partes[2];
    
    Usuario usuario = gestorUsuarios.autenticar(nombreUsuario, contrasena);
    
    if (usuario != null) {
        usuarioAutenticado = usuario;
        return "OK|LOGIN|" + usuario.getRol();
    } else {
        return "ERROR: Usuario o contraseña incorrectos";
    }
}
```
**Explicación:**
1. Verifica que no esté ya autenticado
2. Extrae usuario y contraseña del comando
3. Llama a `gestorUsuarios.autenticar()` para validar
4. Si es válido, guarda el usuario y responde OK
5. Si no, responde ERROR

### Método procesarAlta():

```java
private String procesarAlta(String[] partes) {
    if (usuarioAutenticado == null) {
        return "ERROR: Debe iniciar sesión primero";
    }
    
    String descripcion = partes[1];
    
    Incidencia incidencia = gestorIncidencias.crearIncidencia(
        descripcion, 
        usuarioAutenticado.getNombreUsuario()
    );
    
    // FUNCIONALIDAD EXTRA: Guardar automáticamente
    Persistencia.guardarAutomatico(gestorIncidencias);
    
    return "OK: Incidencia creada con ID " + incidencia.getId();
}
```
**Explicación:**
1. Verifica que esté autenticado
2. Extrae la descripción
3. Crea la incidencia llamando al gestor
4. **Guarda en disco automáticamente**
5. Responde con el ID de la incidencia creada

---

## 3️⃣ GestorIncidencias.java - Gestiona las Incidencias

**¿Qué hace?** Almacena y maneja todas las incidencias de forma segura para múltiples hilos.

```java
private List<Incidencia> incidencias;
```
**Explicación:** Una lista que guarda TODAS las incidencias en memoria.

```java
public synchronized Incidencia crearIncidencia(String descripcion, String usuario) {
    Incidencia nuevaIncidencia = new Incidencia(descripcion, usuario);
    incidencias.add(nuevaIncidencia);
    return nuevaIncidencia;
}
```
**Explicación:**
- `synchronized` = **CRÍTICO**: Solo un hilo puede ejecutar este método a la vez
- Sin synchronized, dos clientes creando incidencias al mismo tiempo causarían problemas
- Crea la incidencia y la añade a la lista

**¿Por qué synchronized?**
```
Cliente A crea incidencia → accede a la lista
Cliente B crea incidencia → ESPERA
Cliente A termina → Cliente B puede acceder
```
Sin synchronized, ambos accederían al mismo tiempo = **corrupción de datos**.

```java
public synchronized List<Incidencia> listarIncidencias() {
    return new ArrayList<>(incidencias);
}
```
**Explicación:**
- Devuelve una **copia** de la lista
- ¿Por qué copia? Para que el cliente no pueda modificar la lista original

```java
public synchronized boolean cerrarIncidencia(int id) {
    for (Incidencia inc : incidencias) {
        if (inc.getId() == id) {
            inc.cerrar();
            return true;
        }
    }
    return false;
}
```
**Explicación:**
1. Busca la incidencia por ID
2. Si la encuentra, llama a `cerrar()` y devuelve `true`
3. Si no, devuelve `false`

---

## 4️⃣ Incidencia.java - Representa una Incidencia

**¿Qué hace?** Define cómo es una incidencia (sus datos).

```java
private static int contadorId = 1;
```
**Explicación:**
- `static` = **compartido por todas las incidencias**
- Cada nueva incidencia incrementa este contador
- Garantiza que cada ID sea único

```java
private int id;
private String descripcion;
private LocalDateTime fechaHora;
private String estado;
private String usuario;
```
**Explicación:** Los datos de cada incidencia:
- `id`: Número único (1, 2, 3...)
- `descripcion`: Texto del problema
- `fechaHora`: Cuándo se creó
- `estado`: "ABIERTA" o "CERRADA"
- `usuario`: Quién la creó

```java
public Incidencia(String descripcion, String usuario) {
    this.id = contadorId++;
    this.descripcion = descripcion;
    this.fechaHora = LocalDateTime.now();
    this.estado = "ABIERTA";
    this.usuario = usuario;
}
```
**Explicación:**
1. `contadorId++` = usa el valor actual y luego lo incrementa
   - Primera incidencia: id=1, luego contador=2
   - Segunda incidencia: id=2, luego contador=3
2. `LocalDateTime.now()` = fecha y hora actual
3. Estado inicial siempre "ABIERTA"

```java
public void cerrar() {
    this.estado = "CERRADA";
}
```
**Explicación:** Cambia el estado a CERRADA (no se puede reabrir).

```java
public void setDescripcion(String nuevaDescripcion) {
    this.descripcion = nuevaDescripcion;
}
```
**Explicación:** FUNCIONALIDAD EXTRA - Permite editar la descripción.

---

## 5️⃣ GestorUsuarios.java - Gestiona Usuarios

**¿Qué hace?** Almacena usuarios y valida credenciales.

```java
private Map<String, Usuario> usuarios;
```
**Explicación:** 
- `Map` = diccionario: clave → valor
- Clave: nombre de usuario (String)
- Valor: objeto Usuario

```java
public GestorUsuarios() {
    usuarios = new HashMap<>();
    
    usuarios.put("admin", new Usuario("admin", "admin123", "ADMINISTRADOR"));
    usuarios.put("juan", new Usuario("juan", "juan123", "USUARIO"));
    usuarios.put("maria", new Usuario("maria", "maria123", "USUARIO"));
}
```
**Explicación:**
- Crea usuarios de prueba al iniciar
- `put(clave, valor)` = añade al diccionario
- **Aquí defines los usuarios del sistema**

```java
public Usuario autenticar(String nombreUsuario, String contrasena) {
    // PASO 1: Intentar con API
    Logger.info("Intentando autenticación con API para: " + nombreUsuario);
    
    if (AutenticadorAPI.autenticarConAPI(nombreUsuario, contrasena)) {
        String rol = AutenticadorAPI.determinarRol(nombreUsuario);
        Logger.info("✓ Autenticación con API exitosa");
        return new Usuario(nombreUsuario, contrasena, rol);
    }
    
    // PASO 2: Si falla API, usar usuarios locales
    Logger.info("API falló, intentando autenticación local...");
    Usuario usuario = usuarios.get(nombreUsuario);
    
    if (usuario != null && usuario.verificarContrasena(contrasena)) {
        Logger.info("✓ Autenticación local exitosa");
        return usuario;
    }
    
    Logger.warning("✗ Autenticación fallida");
    return null;
}
```
**Explicación:**
1. **Primero:** Intenta autenticar con API REST
2. **Si falla:** Busca usuario en el Map local
3. **Verificación:** Comprueba que existe Y que la contraseña coincide
4. **Resultado:** Devuelve Usuario si OK, null si falla

---

## 6️⃣ Usuario.java - Representa un Usuario

```java
private String nombreUsuario;
private String contrasena;
private String rol;
```
**Explicación:** Datos de cada usuario.

```java
public boolean verificarContrasena(String contrasenaIngresada) {
    return this.contrasena.equals(contrasenaIngresada);
}
```
**Explicación:** 
- `equals()` = compara strings (NO uses `==` para strings)
- Devuelve `true` si coinciden, `false` si no

---

## 7️⃣ Logger.java - Sistema de Logging

**¿Qué hace?** Escribe mensajes en consola Y en archivo.

```java
public enum Nivel {
    ERROR, WARNING, INFO, DEBUG
}
```
**Explicación:** Los 4 niveles de severidad (de más grave a menos).

```java
private static PrintWriter escritor;
private static Nivel nivelMinimo = Nivel.INFO;
```
**Explicación:**
- `escritor`: Para escribir en el archivo
- `nivelMinimo`: Solo registra este nivel o superiores

```java
public static void inicializar() {
    try {
        File archivoLog = new File("servidor_logs.txt");
        escritor = new PrintWriter(new FileWriter(archivoLog, true));
        
        info("Sistema de logging inicializado");
    } catch (IOException e) {
        System.err.println("Error al inicializar logging");
    }
}
```
**Explicación:**
1. Crea/abre el archivo `servidor_logs.txt`
2. `true` = modo append (añade al final, no sobrescribe)
3. Crea el PrintWriter para escribir

```java
private static void log(Nivel nivel, String mensaje) {
    if (nivel.ordinal() <= nivelMinimo.ordinal()) {
        LocalDateTime ahora = LocalDateTime.now();
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        
        String lineaLog = String.format("[%s] [%s] %s",
            ahora.format(formato),
            nivel.name(),
            mensaje
        );
        
        System.out.println(lineaLog);
        
        if (escritor != null) {
            escritor.println(lineaLog);
            escritor.flush();
        }
    }
}
```
**Explicación:**
1. `ordinal()` = posición en el enum (ERROR=0, WARNING=1, INFO=2, DEBUG=3)
2. Solo registra si el nivel es <= al mínimo (más importante)
3. Formatea: `[fecha hora] [NIVEL] mensaje`
4. Escribe en consola con `System.out.println()`
5. Escribe en archivo con `escritor.println()`
6. `flush()` = fuerza la escritura inmediata al disco

---

## 8️⃣ Persistencia.java - Guardar/Cargar Datos

**¿Qué hace?** Guarda incidencias en disco y las carga al iniciar.

```java
private static final String ARCHIVO_DATOS = "incidencias.dat";
```
**Explicación:** Nombre del archivo donde se guardan los datos.

```java
public static void guardar(List<Incidencia> incidencias) {
    try (PrintWriter writer = new PrintWriter(new FileWriter(ARCHIVO_DATOS))) {
        for (Incidencia inc : incidencias) {
            String linea = String.format("%d|%s|%s|%s|%s",
                inc.getId(),
                inc.getDescripcion(),
                inc.getFechaHora().format(FORMATO_FECHA),
                inc.getEstado(),
                inc.getUsuario()
            );
            writer.println(linea);
        }
    }
}
```
**Explicación:**
1. `try (...)` = try-with-resources: cierra automáticamente el archivo
2. Para cada incidencia, crea una línea separada por `|`
3. Formato: `id|descripcion|fecha|estado|usuario`
4. Ejemplo: `1|Wifi roto|2026-02-08 20:00:00|ABIERTA|juan`

```java
public static List<Incidencia> cargar() {
    List<Incidencia> incidencias = new ArrayList<>();
    
    File archivo = new File(ARCHIVO_DATOS);
    if (!archivo.exists()) {
        return incidencias; // Archivo no existe, devuelve lista vacía
    }
    
    try (BufferedReader reader = new BufferedReader(new FileReader(ARCHIVO_DATOS))) {
        String linea;
        while ((linea = reader.readLine()) != null) {
            String[] partes = linea.split("\\|");
            
            int id = Integer.parseInt(partes[0]);
            String descripcion = partes[1];
            LocalDateTime fechaHora = LocalDateTime.parse(partes[2], FORMATO_FECHA);
            String estado = partes[3];
            String usuario = partes[4];
            
            Incidencia inc = new Incidencia(id, descripcion, fechaHora, estado, usuario);
            incidencias.add(inc);
        }
    }
    
    return incidencias;
}
```
**Explicación:**
1. Verifica si existe el archivo
2. Lee línea por línea
3. `split("\\|")` = divide por `|` (doble \\ porque | es especial en regex)
4. Convierte cada parte a su tipo (int, LocalDateTime, etc.)
5. Crea una Incidencia con el constructor especial
6. Añade a la lista

```java
public static void guardarAutomatico(GestorIncidencias gestor) {
    guardar(gestor.listarIncidencias());
}
```
**Explicación:** 
- Recibe el gestor
- Obtiene todas las incidencias
- Las guarda en disco

---

## 9️⃣ AutenticadorAPI.java - Autenticación con API

**¿Qué hace?** Valida credenciales contra una API REST externa.

```java
private static final String API_URL = "https://reqres.in/api/login";
```
**Explicación:** URL de la API de prueba.

```java
public static boolean autenticarConAPI(String email, String password) {
    try {
        // 1. Crear conexión
        URL url = URI.create(API_URL).toURL();
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        
        // 2. Configurar como POST con JSON
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);
        
        // 3. Crear JSON
        String jsonInput = String.format(
            "{\"email\":\"%s\",\"password\":\"%s\"}", 
            email, 
            password
        );
        
        // 4. Enviar
        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = jsonInput.getBytes("utf-8");
            os.write(input, 0, input.length);
        }
        
        // 5. Leer respuesta
        int codigoRespuesta = conn.getResponseCode();
        
        if (codigoRespuesta == 200) {
            return true;
        } else {
            return false;
        }
        
    } catch (Exception e) {
        Logger.error("Error al conectar con la API: " + e.getMessage());
        return false;
    }
}
```
**Explicación paso a paso:**

1. **Crear conexión HTTP** a la URL de la API
2. **Configurar petición:**
   - `POST` = enviar datos
   - `Content-Type: application/json` = datos en formato JSON
3. **Crear JSON:** `{"email":"...", "password":"..."}`
4. **Enviar:** Escribe el JSON al servidor
5. **Leer respuesta:**
   - `200` = OK (credenciales correctas)
   - Cualquier otro = ERROR
6. **Si hay error** (sin internet, Cloudflare, etc.), devuelve `false`

```java
public static String determinarRol(String email) {
    if (email.equals("eve.holt@reqres.in")) {
        return "ADMINISTRADOR";
    } else {
        return "USUARIO";
    }
}
```
**Explicación:** Asigna roles según el email (hardcoded para simplicidad).

---

# 💻 PARTE 2: CLIENTE

## 🔟 Cliente.java - Programa del Cliente

**¿Qué hace?** Interfaz de consola para que el usuario interactúe con el servidor.

```java
private static final String HOST = "localhost";
private static final int PUERTO = 5000;
```
**Explicación:** 
- `localhost` = este mismo ordenador (127.0.0.1)
- `5000` = mismo puerto que el servidor

```java
private Socket socket;
private BufferedReader entrada;
private PrintWriter salida;
private Scanner teclado;
```
**Explicación:**
- `socket`: Conexión con el servidor
- `entrada`: Leer respuestas del servidor
- `salida`: Enviar comandos al servidor
- `teclado`: Leer entrada del usuario

### Método conectar():

```java
public boolean conectar() {
    try {
        socket = new Socket(HOST, PUERTO);
        entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        salida = new PrintWriter(socket.getOutputStream(), true);
        
        String mensajeBienvenida;
        while ((mensajeBienvenida = entrada.readLine()) != null) {
            System.out.println(mensajeBienvenida);
            if (mensajeBienvenida.contains("LOGIN")) {
                break;
            }
        }
        
        return true;
    } catch (IOException e) {
        System.err.println("ERROR: No se pudo conectar al servidor");
        return false;
    }
}
```
**Explicación:**
1. `new Socket(HOST, PUERTO)` = **conecta con el servidor**
2. Configura entrada/salida
3. Lee mensajes de bienvenida hasta encontrar "LOGIN"
4. Si falla, captura el error y devuelve `false`

### Método login():

```java
public boolean login() {
    while (true) {
        System.out.print("Usuario: ");
        String usuario = teclado.nextLine().trim();
        
        System.out.print("Contraseña: ");
        String contrasena = teclado.nextLine().trim();
        
        String comandoLogin = "LOGIN " + usuario + " " + contrasena;
        salida.println(comandoLogin);
        
        String respuesta = entrada.readLine();
        
        if (respuesta.startsWith("OK|LOGIN|")) {
            String[] partes = respuesta.split("\\|");
            rolUsuario = partes[2];
            return true;
        } else {
            System.out.println(respuesta);
            System.out.println("Inténtalo de nuevo");
        }
    }
}
```
**Explicación:**
1. Bucle infinito hasta que login sea exitoso
2. Lee usuario y contraseña del teclado
3. Construye comando: `"LOGIN admin admin123"`
4. Envía al servidor
5. Lee respuesta:
   - Si empieza con `OK|LOGIN|`, extrae el rol y sale
   - Si no, muestra error y vuelve a pedir

### Método ejecutar():

```java
public void ejecutar() {
    while (true) {
        mostrarMenu();
        String opcion = teclado.nextLine().trim();
        
        switch (opcion) {
            case "1": // ALTA
                // Pedir descripción y enviar comando
                break;
            case "2": // LISTAR
                comando = "LISTAR";
                break;
            // ... más casos
        }
        
        if (comando != null) {
            enviarComando(comando);
        }
    }
}
```
**Explicación:**
1. Muestra el menú
2. Lee la opción del usuario
3. Según la opción, construye el comando
4. Envía el comando al servidor

### Método enviarComando():

```java
private void enviarComando(String comando) {
    try {
        salida.println(comando);
        
        String respuesta = entrada.readLine();
        
        if (respuesta.contains("===")) {
            System.out.println(respuesta);
            String linea;
            while ((linea = entrada.readLine()) != null && !linea.trim().isEmpty()) {
                System.out.println(linea);
                if (!linea.startsWith("ID:") && !linea.startsWith("Usuario:")) {
                    break;
                }
            }
        } else {
            System.out.println(respuesta);
        }
    } catch (IOException e) {
        System.err.println("ERROR: Error de comunicación");
    }
}
```
**Explicación:**
1. Envía el comando al servidor
2. Lee la respuesta
3. **Si es LISTAR o CLIENTES** (contiene ===), lee múltiples líneas
4. Si es respuesta simple, imprime una línea
5. Captura errores de red

---

# 🔗 CÓMO SE RELACIONAN TODOS LOS ARCHIVOS

```
INICIO DEL SERVIDOR:
1. main() en Servidor.java
2. crea GestorIncidencias y GestorUsuarios
3. inicializa Logger
4. carga datos con Persistencia
5. espera conexiones en un loop

CLIENTE SE CONECTA:
6. Servidor.accept() detecta cliente
7. crea ManejadorCliente en un Thread nuevo
8. ManejadorCliente.run() empieza a ejecutarse

CLIENTE HACE LOGIN:
9. Cliente envía: "LOGIN admin admin123"
10. ManejadorCliente recibe y llama a procesarLogin()
11. procesarLogin() llama a GestorUsuarios.autenticar()
12. GestorUsuarios intenta AutenticadorAPI (falla por Cloudflare)
13. GestorUsuarios busca en usuarios locales
14. Encuentra al Usuario y devuelve
15. ManejadorCliente responde: "OK|LOGIN|ADMINISTRADOR"
16. Cliente procesa la respuesta y guarda el rol

CLIENTE CREA INCIDENCIA:
17. Cliente envía: "ALTA Problema con wifi"
18. ManejadorCliente.procesarAlta()
19. Llama a GestorIncidencias.crearIncidencia()
20. GestorIncidencias crea nueva Incidencia (ID automático)
21. Añade a la lista (synchronized)
22. Llama a Persistencia.guardarAutomatico()
23. Persistencia escribe en incidencias.dat
24. ManejadorCliente responde: "OK: Incidencia creada con ID 1"
25. Logger registra todo en servidor_logs.txt
```

---

# 💡 CONCEPTOS CLAVE PARA ENTENDER

## 🔄 ¿Qué es synchronized?

```java
public synchronized void metodo() {
    // código
}
```

**Sin synchronized:**
```
Hilo A entra al método
Hilo B entra al método AL MISMO TIEMPO
Ambos modifican la misma lista
RESULTADO: Lista corrupta ❌
```

**Con synchronized:**
```
Hilo A entra al método
Hilo B intenta entrar → BLOQUEADO (espera)
Hilo A termina
Hilo B puede entrar ahora
RESULTADO: Lista segura ✅
```

## 🧵 ¿Qué son los hilos (Threads)?

**Un hilo = una línea de ejecución**

```
SIN Threads:
Cliente A se conecta → Servidor procesa A
Cliente B se conecta → B ESPERA a que A termine
Cliente C se conecta → C ESPERA a que B termine
```

```
CON Threads:
Cliente A se conecta → Thread 1 procesa A
Cliente B se conecta → Thread 2 procesa B (en paralelo)
Cliente C se conecta → Thread 3 procesa C (en paralelo)
Todos funcionan AL MISMO TIEMPO
```

## 📡 ¿Qué son Sockets?

**Socket = tubería de comunicación**

```
Cliente                 Servidor
  |                        |
  |----"LOGIN admin"------>|
  |                        |
  |<----"OK|LOGIN|..."-----|
  |                        |
  |----"ALTA Problema"---->|
  |                        |
  |<----"OK: ID 1"---------|
```

---

# ✅ RESUMEN FINAL

**Tu proyecto tiene:**

1. **Servidor multi-hilo** que acepta múltiples clientes
2. **Gestión segura** de datos compartidos con `synchronized`
3. **Autenticación dual** (API + local)
4. **Persistencia** (guarda y carga datos)
5. **Logging profesional** con niveles
6. **Gestión de excepciones** robusta
7. **Cliente interactivo** fácil de usar

**Cada archivo tiene una responsabilidad clara:**
- Servidor → Acepta conexiones
- ManejadorCliente → Habla con cada cliente
- GestorIncidencias → Maneja incidencias
- GestorUsuarios → Valida usuarios
- Logger → Registra eventos
- Persistencia → Guarda datos
- AutenticadorAPI → Integra API externa

**¿Dudas sobre algún archivo específico? Pregúntame y te lo explico más a fondo.** 🚀
