package cliente;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

/**
 * Clase principal del cliente.
 * Conecta con el servidor y permite al usuario enviar comandos.
 */
public class Cliente {
    // Configuración de conexión
    private static final String HOST = "localhost"; // Cambiar a IP del servidor si está en otra máquina
    private static final int PUERTO = 5000;

    private Socket socket;
    private BufferedReader entrada;
    private PrintWriter salida;
    private Scanner teclado;
    private String rolUsuario;

    /**
     * Constructor: inicializa el scanner del teclado
     */
    public Cliente() {
        this.teclado = new Scanner(System.in);
        this.rolUsuario = null;
    }

    /**
     * Conecta con el servidor
     */
    public boolean conectar() {
        try {
            System.out.println("Conectando al servidor " + HOST + ":" + PUERTO + "...");

            socket = new Socket(HOST, PUERTO);
            entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            salida = new PrintWriter(socket.getOutputStream(), true);

            System.out.println("Conexión establecida correctamente");
            System.out.println();

            // Leer mensajes de bienvenida del servidor
            String mensajeBienvenida;
            while ((mensajeBienvenida = entrada.readLine()) != null) {
                System.out.println(mensajeBienvenida);
                if (mensajeBienvenida.contains("LOGIN")) {
                    break; // Salir cuando llegue el mensaje de login
                }
            }

            return true;

        } catch (IOException e) {
            System.err.println("ERROR: No se pudo conectar al servidor");
            System.err.println("Asegúrate de que el servidor está ejecutándose en " + HOST + ":" + PUERTO);
            return false;
        } catch (Exception e) {
            // FUNCIONALIDAD EXTRA: Captura cualquier otra excepción inesperada
            System.err.println("ERROR inesperado al conectar: " + e.getMessage());
            return false;
        }
    }

    /**
     * Realiza el proceso de login
     */
    public boolean login() {
        System.out.println();
        System.out.println("=== INICIO DE SESIÓN ===");

        while (true) {
            System.out.print("Usuario: ");
            String usuario = teclado.nextLine().trim();

            System.out.print("Contraseña: ");
            String contrasena = teclado.nextLine().trim();

            // Validar que no estén vacíos
            if (usuario.isEmpty() || contrasena.isEmpty()) {
                System.out.println("ERROR: Usuario y contraseña no pueden estar vacíos");
                continue;
            }

            // Enviar comando LOGIN
            String comandoLogin = "LOGIN " + usuario + " " + contrasena;
            salida.println(comandoLogin);

            try {
                // Leer respuesta del servidor
                String respuesta = entrada.readLine();

                if (respuesta.startsWith("OK|LOGIN|")) {
                    // Login exitoso
                    String[] partes = respuesta.split("\\|");
                    rolUsuario = partes[2];

                    System.out.println();
                    System.out.println("✓ Inicio de sesión exitoso");
                    System.out.println("Rol: " + rolUsuario);
                    System.out.println();
                    return true;

                } else {
                    // Login fallido
                    System.out.println(respuesta);
                    System.out.println("Inténtalo de nuevo");
                    System.out.println();
                }

            } catch (IOException e) {
                System.err.println("ERROR: Error de comunicación con el servidor");
                return false;
            }
        }
    }

    /**
     * Muestra el menú de opciones según el rol del usuario
     * FUNCIONALIDAD EXTRA: Incluye comando EDITAR
     */
    public void mostrarMenu() {
        System.out.println("=== MENÚ DE OPCIONES ===");
        System.out.println("1. ALTA - Crear nueva incidencia");
        System.out.println("2. LISTAR - Ver todas las incidencias");
        System.out.println("3. CERRAR - Cerrar una incidencia");
        System.out.println("4. EDITAR - Editar una incidencia");

        if (rolUsuario.equals("ADMINISTRADOR")) {
            System.out.println("5. CLIENTES - Ver clientes conectados (Solo Admin)");
        }

        System.out.println("6. SALIR - Cerrar sesión");
        System.out.println();
    }

    /**
     * Bucle principal: muestra menú y procesa comandos
     */
    public void ejecutar() {
        while (true) {
            mostrarMenu();
            System.out.print("Selecciona una opción: ");
            String opcion = teclado.nextLine().trim();

            String comando = null;

            switch (opcion) {
                case "1": // ALTA
                    System.out.print("Descripción de la incidencia: ");
                    String descripcion = teclado.nextLine().trim();

                    if (descripcion.isEmpty()) {
                        System.out.println("ERROR: La descripción no puede estar vacía");
                        break;
                    }

                    comando = "ALTA " + descripcion;
                    break;

                case "2": // LISTAR
                    comando = "LISTAR";
                    break;

                case "3": // CERRAR
                    System.out.print("ID de la incidencia a cerrar: ");
                    String id = teclado.nextLine().trim();

                    if (id.isEmpty()) {
                        System.out.println("ERROR: Debe introducir un ID");
                        break;
                    }

                    comando = "CERRAR " + id;
                    break;

                case "4": // EDITAR (FUNCIONALIDAD EXTRA)
                    System.out.print("ID de la incidencia a editar: ");
                    String idEditar = teclado.nextLine().trim();

                    if (idEditar.isEmpty()) {
                        System.out.println("ERROR: Debe introducir un ID");
                        break;
                    }

                    System.out.print("Nueva descripción: ");
                    String nuevaDesc = teclado.nextLine().trim();

                    if (nuevaDesc.isEmpty()) {
                        System.out.println("ERROR: La descripción no puede estar vacía");
                        break;
                    }

                    comando = "EDITAR " + idEditar + " " + nuevaDesc;
                    break;

                case "5": // CLIENTES (solo admin)
                    if (rolUsuario.equals("ADMINISTRADOR")) {
                        comando = "CLIENTES";
                    } else {
                        System.out.println("ERROR: Opción no válida");
                    }
                    break;

                case "6": // SALIR
                    comando = "SALIR";
                    break;

                default:
                    System.out.println("ERROR: Opción no válida");
                    break;
            }

            // Si hay un comando válido, enviarlo
            if (comando != null) {
                enviarComando(comando);

                // Si el comando es SALIR, terminar
                if (comando.equals("SALIR")) {
                    break;
                }
            }

            System.out.println();
        }
    }

    /**
     * Envía un comando al servidor y muestra la respuesta
     */
    private void enviarComando(String comando) {
        try {
            // Enviar comando
            salida.println(comando);

            // Leer respuesta
            String respuesta = entrada.readLine();

            // Si la respuesta tiene múltiples líneas (como LISTAR), leer todas
            if (respuesta.contains("===")) {
                System.out.println(respuesta);

                // Leer líneas adicionales hasta encontrar una vacía o el final
                String linea;
                while ((linea = entrada.readLine()) != null && !linea.trim().isEmpty()) {
                    System.out.println(linea);

                    // Si no empieza con "ID:" es la última línea
                    if (!linea.startsWith("ID:") && !linea.startsWith("Usuario:")) {
                        break;
                    }
                }
            } else {
                System.out.println(respuesta);
            }

        } catch (IOException e) {
            // FUNCIONALIDAD EXTRA: Gestión robusta de excepciones
            System.err.println("ERROR: Error de comunicación con el servidor");
            System.err.println("El servidor puede haberse desconectado o hay problemas de red");
        } catch (NullPointerException e) {
            System.err.println("ERROR: Respuesta inesperada del servidor");
        } catch (Exception e) {
            System.err.println("ERROR inesperado: " + e.getMessage());
        }
    }

    /**
     * Cierra la conexión y libera recursos
     */
    public void desconectar() {
        try {
            if (entrada != null)
                entrada.close();
            if (salida != null)
                salida.close();
            if (socket != null && !socket.isClosed())
                socket.close();
            if (teclado != null)
                teclado.close();

            System.out.println("Desconectado del servidor");

        } catch (IOException e) {
            System.err.println("Error al cerrar conexión: " + e.getMessage());
        } catch (Exception e) {
            // Capturar cualquier otra excepción inesperada al cerrar
            System.err.println("Error inesperado al cerrar: " + e.getMessage());
        }
    }

    /**
     * Método main: punto de entrada del cliente
     */
    public static void main(String[] args) {
        Cliente cliente = new Cliente();

        // Intentar conectar
        if (!cliente.conectar()) {
            System.err.println("No se pudo iniciar el cliente");
            return;
        }

        // Realizar login
        if (!cliente.login()) {
            System.err.println("No se pudo iniciar sesión");
            cliente.desconectar();
            return;
        }

        // Ejecutar bucle principal
        cliente.ejecutar();

        // Desconectar al salir
        cliente.desconectar();
    }
}
