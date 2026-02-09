package servidor;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase principal del servidor.
 * Escucha conexiones de clientes y crea un hilo para cada uno.
 */
public class Servidor {
    // Configuración del servidor
    private static final int PUERTO = 5000;
    private static final int MAX_CLIENTES = 10;

    // Gestores compartidos por todos los clientes
    private GestorIncidencias gestorIncidencias;
    private GestorUsuarios gestorUsuarios;

    // Lista de clientes conectados (thread-safe)
    private List<ClienteConectado> clientesConectados;

    /**
     * Constructor: inicializa los gestores
     */
    public Servidor() {
        this.gestorIncidencias = new GestorIncidencias();
        this.gestorUsuarios = new GestorUsuarios();
        this.clientesConectados = new ArrayList<>();
    }

    /**
     * Inicia el servidor y escucha conexiones
     */
    public void iniciar() {
        // FUNCIONALIDAD EXTRA: Inicializar sistema de logging
        Logger.inicializar();

        Logger.info("SERVIDOR DE GESTIÓN DE INCIDENCIAS ");
        Logger.info("Iniciando servidor en puerto " + PUERTO + "...");

        // FUNCIONALIDAD EXTRA: Cargar incidencias guardadas desde disco
        Logger.info("Cargando incidencias desde persistencia...");
        List<Incidencia> incidenciasGuardadas = Persistencia.cargar();
        if (!incidenciasGuardadas.isEmpty()) {
            gestorIncidencias.cargarIncidencias(incidenciasGuardadas);
            Logger.info("Incidencias recuperadas: " + incidenciasGuardadas.size());
        } else {
            Logger.info("No hay incidencias previas. Base de datos vacía.");
        }

        try (ServerSocket serverSocket = new ServerSocket(PUERTO)) {
            Logger.info("Servidor iniciado correctamente");
            Logger.info("Esperando conexiones de clientes...");
            Logger.info("Máximo de clientes: " + MAX_CLIENTES);
            System.out.println();

            // Bucle infinito: aceptar conexiones
            while (true) {
                try {
                    // Esperar a que un cliente se conecte
                    Socket socketCliente = serverSocket.accept();

                    // Verificar si hay espacio para más clientes
                    if (obtenerNumeroClientesConectados() >= MAX_CLIENTES) {
                        Logger.warning("Cliente rechazado: límite de clientes alcanzado");
                        socketCliente.close();
                        continue;
                    }

                    // Mostrar información del cliente
                    String ipCliente = socketCliente.getInetAddress().getHostAddress();
                    Logger.info("Nuevo cliente conectado desde: " + ipCliente);

                    // Crear un manejador para este cliente
                    ManejadorCliente manejador = new ManejadorCliente(
                            socketCliente,
                            gestorIncidencias,
                            gestorUsuarios,
                            this);

                    // Crear y arrancar un nuevo hilo para este cliente
                    Thread hiloCliente = new Thread(manejador); //thread es un hilo que permite que el servidor maneje varios clientes a la vez 
                    hiloCliente.start();

                } catch (IOException e) {
                    Logger.error("Error al aceptar cliente: " + e.getMessage());
                }
            }

        } catch (IOException e) {
            Logger.error("Error al iniciar servidor: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Cerrar el logger al salir
            Logger.cerrar();
        }
    }

    /**
     * Registra un cliente como conectado
     */
    public synchronized void registrarClienteConectado(ClienteConectado cliente) {
        clientesConectados.add(cliente);
        Logger.info("Usuario autenticado: " + cliente.getNombreUsuario() + " (" + cliente.getRol() + ")");
    }

    /**
     * Elimina un cliente de la lista de conectados
     */
    public synchronized void eliminarClienteConectado(String nombreUsuario) {
        clientesConectados.removeIf(c -> c.getNombreUsuario().equals(nombreUsuario));
    }

    /**
     * Obtiene la lista de clientes conectados
     */
    public synchronized List<ClienteConectado> obtenerClientesConectados() {
        return new ArrayList<>(clientesConectados);
    }

    /**
     * Obtiene el número actual de clientes conectados
     */
    public synchronized int obtenerNumeroClientesConectados() {
        return clientesConectados.size();
    }

    /**
     * Método main: punto de entrada del servidor
     */
    public static void main(String[] args) {
        Servidor servidor = new Servidor();
        servidor.iniciar();
    }
}
