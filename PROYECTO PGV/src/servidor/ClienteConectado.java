package servidor;

/**
 * Clase que representa la información de un cliente conectado.
 * Se usa para el comando CLIENTES (solo visible por administradores).
 */
public class ClienteConectado {
    private String nombreUsuario;
    private String ip;
    private String rol;
    private long tiempoConexion; // Timestamp de cuándo se conectó

    /**
     * Constructor: crea un registro de cliente conectado
     */
    public ClienteConectado(String nombreUsuario, String ip, String rol) {
        this.nombreUsuario = nombreUsuario;
        this.ip = ip;
        this.rol = rol;
        this.tiempoConexion = System.currentTimeMillis();
    }

    // Getters
    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public String getIp() {
        return ip;
    }

    public String getRol() {
        return rol;
    }

    public long getTiempoConexion() {
        return tiempoConexion;
    }

    /**
     * Convierte la información del cliente a texto
     */
    @Override
    public String toString() {
        return String.format("Usuario: %s | IP: %s | Rol: %s",
                nombreUsuario, ip, rol);
    }
}
