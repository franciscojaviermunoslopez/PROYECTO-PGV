package servidor;

import java.io.*;
import java.net.Socket;
import java.util.List;

/**
 * Clase que maneja la comunicación con un cliente específico.
 * Cada cliente tiene su propio hilo (Thread) para atenderlo.
 */
public class ManejadorCliente implements Runnable {
    private Socket socket;
    private GestorIncidencias gestorIncidencias;
    private GestorUsuarios gestorUsuarios;
    private Servidor servidor;
    private BufferedReader entrada;
    private PrintWriter salida;
    private Usuario usuarioAutenticado;

    /**
     * Constructor: inicializa el manejador con el socket del cliente
     */
    public ManejadorCliente(Socket socket, GestorIncidencias gestorIncidencias,
            GestorUsuarios gestorUsuarios, Servidor servidor) {
        this.socket = socket;
        this.gestorIncidencias = gestorIncidencias;
        this.gestorUsuarios = gestorUsuarios;
        this.servidor = servidor;
        this.usuarioAutenticado = null;
    }

    /**
     * Método principal del hilo: se ejecuta cuando se inicia el hilo     */
    @Override
    public void run() {
        String ipCliente = socket.getInetAddress().getHostAddress();

        try {
            // Crear flujos de entrada y salida
            entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            salida = new PrintWriter(socket.getOutputStream(), true);

            Logger.info("Cliente conectado desde " + ipCliente);

            // Mensaje de bienvenida
            salida.println("Bienvenido al Sistema de Gestión de Incidencias");
            salida.println("Por favor, inicie sesión con: LOGIN <usuario> <contraseña>");

            // Bucle principal: leer comandos del cliente
            String comando;
            while ((comando = entrada.readLine()) != null) {
                Logger.debug("Comando recibido de " + ipCliente + ": " + comando);

                try {
                    // Procesar el comando y obtener respuesta
                    String respuesta = procesarComando(comando);

                    // Enviar respuesta al cliente
                    salida.println(respuesta);

                } catch (Exception e) {
                    // capturar cualquier error en el procesamiento
                    Logger.error("Error procesando comando de " + ipCliente + ": " + e.getMessage());
                    salida.println("ERROR: Error interno del servidor al procesar el comando");
                }

                // Si el comando es SALIR, terminar
                if (comando.trim().equalsIgnoreCase("SALIR")) {
                    break;
                }
            }

        } catch (IOException e) {
            // Error de comunicación (cliente desconectado abruptamente, etc.)
            Logger.warning("Error de comunicación con cliente " + ipCliente + ": " + e.getMessage());
        } catch (Exception e) {
            // Cualquier otro error inesperado
            Logger.error("Error inesperado con cliente " + ipCliente + ": " + e.getMessage());
            e.printStackTrace();
        } finally {
            cerrarConexion();
        }
    }

    /**
     * Procesa un comando recibido del cliente
     * 
     * @param comando Comando completo recibido
     * @return Respuesta a enviar al cliente
     */
    private String procesarComando(String comando) {
        // Normalizar: quitar espacios extras y dividir en partes
        comando = comando.trim();
        String[] partes = comando.split("\\s+");

        if (partes.length == 0) {
            return "ERROR: Comando vacío";
        }

        // Obtener el comando principal (en mayúsculas)
        String comandoPrincipal = partes[0].toUpperCase();

        // Procesar según el comando
        switch (comandoPrincipal) {
            case "LOGIN":
                return procesarLogin(partes); 

            case "ALTA":
                return procesarAlta(partes);

            case "LISTAR":
                return procesarListar();

            case "CERRAR":
                return procesarCerrar(partes);

            case "EDITAR":
                return procesarEditar(partes);

            case "CLIENTES":
                return procesarClientes();

            case "SALIR":
                return procesarSalir();

            default:
                return "ERROR: Comando desconocido. Comandos disponibles: LOGIN, ALTA, LISTAR, CERRAR, EDITAR, CLIENTES, SALIR";
        }
    }

    /**
     * Procesa el comando LOGIN
     * Formato: LOGIN <usuario> <contraseña>
     */
    private String procesarLogin(String[] partes) {
        // Validar que el comando tenga el formato correcto
        if (partes.length != 3) {
            return "ERROR: Formato incorrecto. Uso: LOGIN <usuario> <contraseña>";
        }

        // Verificar si ya está autenticado
        if (usuarioAutenticado != null) {
            return "ERROR: Ya has iniciado sesión como " + usuarioAutenticado.getNombreUsuario();
        }

        String nombreUsuario = partes[1];
        String contrasena = partes[2];

        // Intentar autenticar
        Usuario usuario = gestorUsuarios.autenticar(nombreUsuario, contrasena);

        if (usuario != null) {
            usuarioAutenticado = usuario;

            // Registrar cliente conectado en el servidor
            String ip = socket.getInetAddress().getHostAddress();
            servidor.registrarClienteConectado(new ClienteConectado(nombreUsuario, ip, usuario.getRol()));

            Logger.info("Login exitoso: " + nombreUsuario + " (" + usuario.getRol() + ") desde " + ip);

            return "OK|LOGIN|" + usuario.getRol();
        } else {
            Logger.warning("Intento de login fallido: " + nombreUsuario);
            return "ERROR: Usuario o contraseña incorrectos";
        }
    }

    /**
     * Procesa el comando ALTA
     * Formato: ALTA <descripción de la incidencia>
     */
    private String procesarAlta(String[] partes) {
        // Verificar autenticación
        if (usuarioAutenticado == null) {
            return "ERROR: Debe iniciar sesión primero";
        }

        // Validar que haya una descripción
        if (partes.length < 2) {
            return "ERROR: Formato incorrecto. Uso: ALTA <descripción>";
        }

        // Reconstruir la descripción (puede tener espacios)
        StringBuilder descripcion = new StringBuilder();
        for (int i = 1; i < partes.length; i++) {
            if (i > 1)
                descripcion.append(" ");
            descripcion.append(partes[i]);
        }

        // Crear la incidencia
        Incidencia incidencia = gestorIncidencias.crearIncidencia(
                descripcion.toString(),
                usuarioAutenticado.getNombreUsuario());

        Logger.info(
                "Nueva incidencia creada: ID=" + incidencia.getId() + " por " + usuarioAutenticado.getNombreUsuario());

        // Guardar automáticamente tras cada alta (persistencia)
        Persistencia.guardarAutomatico(gestorIncidencias);

        return "OK: Incidencia creada con ID " + incidencia.getId();
    }

    /**
     * Procesa el comando LISTAR
     */
    private String procesarListar() {
        // Verificar autenticación
        if (usuarioAutenticado == null) {
            return "ERROR: Debe iniciar sesión primero";
        }

        List<Incidencia> incidencias = gestorIncidencias.listarIncidencias();

        if (incidencias.isEmpty()) {
            return "No hay incidencias registradas";
        }

        // Construir la lista de incidencias
        StringBuilder respuesta = new StringBuilder();
        respuesta.append("LISTADO DE INCIDENCIAS \n");
        for (Incidencia inc : incidencias) {
            respuesta.append(inc.toString()).append("\n");
        }

        return respuesta.toString();
    }

    /**
     * Procesa el comando CERRAR
     * Formato: CERRAR <id>
     */
    private String procesarCerrar(String[] partes) {
        // Verificar autenticación
        if (usuarioAutenticado == null) {
            return "ERROR: Debe iniciar sesión primero";
        }

        // Validar formato
        if (partes.length != 2) {
            return "ERROR: Formato incorrecto. Uso: CERRAR <id>";
        }

        try {
            int id = Integer.parseInt(partes[1]);

            boolean cerrada = gestorIncidencias.cerrarIncidencia(id);

            if (cerrada) {
                Logger.info("Usuario " + usuarioAutenticado.getNombreUsuario() + " cerró incidencia " + id);
                return "OK: Incidencia " + id + " cerrada correctamente";
            } else {
                return "ERROR: No se encontró la incidencia con ID " + id;
            }

        } catch (NumberFormatException e) {
            return "ERROR: El ID debe ser un número";
        }
    }

    /**
     * Procesa el comando EDITAR (FUNCIONALIDAD EXTRA)
     * Formato: EDITAR <id> <nueva descripción>
     */
    private String procesarEditar(String[] partes) {
        // Verificar autenticación
        if (usuarioAutenticado == null) {
            return "ERROR: Debe iniciar sesión primero";
        }

        // Validar formato (mínimo: EDITAR id descripcion)
        if (partes.length < 3) {
            return "ERROR: Formato incorrecto. Uso: EDITAR <id> <nueva descripción>";
        }

        try {
            // Parsear el ID
            int id = Integer.parseInt(partes[1]);

            // Reconstruir la nueva descripción (todo después del ID)
            StringBuilder nuevaDescripcion = new StringBuilder();
            for (int i = 2; i < partes.length; i++) {
                if (i > 2)
                    nuevaDescripcion.append(" ");
                nuevaDescripcion.append(partes[i]);
            }

            // Editar la incidencia
            boolean editada = gestorIncidencias.editarIncidencia(id, nuevaDescripcion.toString());

            if (editada) {
                Logger.info("Usuario " + usuarioAutenticado.getNombreUsuario() + " editó incidencia " + id);
                return "OK: Incidencia " + id + " editada correctamente";
            } else {
                return "ERROR: No se encontró la incidencia con ID " + id;
            }

        } catch (NumberFormatException e) {
            return "ERROR: El ID debe ser un número";
        }
    }

    /**
     * Procesa el comando CLIENTES (solo para administradores)
     */
    private String procesarClientes() {
        // Verificar autenticación
        if (usuarioAutenticado == null) {
            return "ERROR: Debe iniciar sesión primero";
        }

        // Verificar que sea administrador
        if (!usuarioAutenticado.getRol().equals("ADMINISTRADOR")) {
            return "ERROR: No tiene permisos para ejecutar este comando";
        }

        List<ClienteConectado> clientes = servidor.obtenerClientesConectados();

        if (clientes.isEmpty()) {
            return "No hay clientes conectados";
        }

        StringBuilder respuesta = new StringBuilder();
        respuesta.append("=== CLIENTES CONECTADOS ===\n");
        for (ClienteConectado cliente : clientes) {
            respuesta.append(cliente.toString()).append("\n");
        }

        return respuesta.toString();
    }

    /**
     * Procesa el comando SALIR
     */
    private String procesarSalir() {
        if (usuarioAutenticado != null) {
            servidor.eliminarClienteConectado(usuarioAutenticado.getNombreUsuario());
        }
        return "Hasta luego. Conexión cerrada.";
    }

    /**
     * Cierra la conexión y libera recursos
     */
    private void cerrarConexion() {
        try {
            if (usuarioAutenticado != null) {
                Logger.info("Cliente desconectado: " + usuarioAutenticado.getNombreUsuario());
                servidor.eliminarClienteConectado(usuarioAutenticado.getNombreUsuario());
            }

            if (entrada != null)
                entrada.close();
            if (salida != null)
                salida.close();
            if (socket != null && !socket.isClosed())
                socket.close();

        } catch (IOException e) {
            Logger.error("Error al cerrar conexión: " + e.getMessage());
        }
    }
}
