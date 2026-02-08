package servidor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Clase que representa una incidencia en el sistema.
 * Cada incidencia tiene un ID único, descripción, fecha/hora, estado y usuario
 * que la creó.
 */
public class Incidencia {
    // Contador estático para generar IDs únicos automáticamente
    private static int contadorId = 1;

    // Atributos de la incidencia
    private int id;
    private String descripcion;
    private LocalDateTime fechaHora;
    private String estado; // "ABIERTA" o "CERRADA"
    private String usuario; // Usuario que creó la incidencia

    /**
     * Constructor: crea una nueva incidencia con estado ABIERTA
     * 
     * @param descripcion Texto descriptivo de la incidencia
     * @param usuario     Usuario que crea la incidencia
     */
    public Incidencia(String descripcion, String usuario) {
        this.id = contadorId++;
        this.descripcion = descripcion;
        this.fechaHora = LocalDateTime.now();
        this.estado = "ABIERTA";
        this.usuario = usuario;
    }

    /**
     * Constructor especial para cargar incidencias desde persistencia
     * Restaura todos los valores originales incluyendo ID, fecha/hora y estado
     * 
     * @param id          ID original de la incidencia
     * @param descripcion Texto descriptivo de la incidencia
     * @param fechaHora   Fecha y hora original
     * @param estado      Estado original (ABIERTA/CERRADA)
     * @param usuario     Usuario que creó la incidencia
     */
    public Incidencia(int id, String descripcion, LocalDateTime fechaHora, String estado, String usuario) {
        this.id = id;
        this.descripcion = descripcion;
        this.fechaHora = fechaHora;
        this.estado = estado;
        this.usuario = usuario;

        // Ajustar el contador si este ID es mayor al actual
        if (id >= contadorId) {
            contadorId = id + 1;
        }
    }

    // Getters (métodos para obtener los valores)
    public int getId() {
        return id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public String getEstado() {
        return estado;
    }

    public String getUsuario() {
        return usuario;
    }

    /**
     * Cierra la incidencia cambiando su estado a CERRADA
     */
    public void cerrar() {
        this.estado = "CERRADA";
    }

    /**
     * Edita la descripción de la incidencia (FUNCIONALIDAD EXTRA)
     * 
     * @param nuevaDescripcion Nueva descripción para la incidencia
     */
    public void setDescripcion(String nuevaDescripcion) {
        this.descripcion = nuevaDescripcion;
    }

    /**
     * Convierte la incidencia a texto para mostrar
     */
    @Override
    public String toString() {
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        return String.format("ID: %d | %s | %s | Estado: %s | Usuario: %s",
                id, descripcion, fechaHora.format(formato), estado, usuario);
    }
}
