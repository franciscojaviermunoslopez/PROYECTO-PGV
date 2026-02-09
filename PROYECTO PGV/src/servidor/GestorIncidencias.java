package servidor;

import java.util.ArrayList;
import java.util.List;

/**
 * Clase que gestiona todas las incidencias del sistema de forma segura para
 * múltiples hilos.
 * Utiliza 'synchronized' para evitar problemas de concurrencia.
 */
public class GestorIncidencias {
    // Lista que almacena todas las incidencias
    private List<Incidencia> incidencias;

    /**
     * Constructor: inicializa la lista vacía
     */
    public GestorIncidencias() {
        this.incidencias = new ArrayList<>();
    }

    /**
     * Crea una nueva incidencia (ALTA)
     * synchronized = solo un hilo puede ejecutar este método a la vez
     * 
     * @param descripcion Descripción de la incidencia
     * @param usuario     Usuario que crea la incidencia
     * @return La incidencia creada
     */
    public synchronized Incidencia crearIncidencia(String descripcion, String usuario) {
        Incidencia nuevaIncidencia = new Incidencia(descripcion, usuario);
        incidencias.add(nuevaIncidencia);
        return nuevaIncidencia;
    }

    /**
     * Obtiene todas las incidencias (LISTAR)
     * 
     * @return Lista con todas las incidencias
     */
    public synchronized List<Incidencia> listarIncidencias() {
        // Devolvemos una copia para evitar modificaciones externas
        return new ArrayList<>(incidencias);
    }

    /**
     * Cierra una incidencia por su ID (CERRAR)
     * 
     * @param id ID de la incidencia a cerrar
     * @return true si se cerró correctamente, false si no se encontró
     */
    public synchronized boolean cerrarIncidencia(int id) {
        for (Incidencia inc : incidencias) {
            if (inc.getId() == id) {
                inc.cerrar();
                return true;
            }
        }
        return false; // No se encontró la incidencia
    }

    /**
     * Busca una incidencia por ID
     * 
     * @param id ID de la incidencia
     * @return La incidencia o null si no existe
     */
    public synchronized Incidencia buscarPorId(int id) {
        for (Incidencia inc : incidencias) {
            if (inc.getId() == id) {
                return inc;
            }
        }
        return null;
    }

    /**
     * Edita la descripción de una incidencia 
     * 
     * @param id               ID de la incidencia a editar 
     * @param nuevaDescripcion Nueva descripción
     * @return true si se editó correctamente, false si no se encontró
     */
    public synchronized boolean editarIncidencia(int id, String nuevaDescripcion) {
        Incidencia incidencia = buscarPorId(id);
        if (incidencia != null) {
            incidencia.setDescripcion(nuevaDescripcion);
            return true;
        }
        return false;
    }

    /**
     * Carga incidencias desde persistencia 
     * Reemplaza las incidencias actuales con las cargadas del disco
     * 
     * @param incidenciasGuardadas Lista de incidencias a cargar
     */
    public synchronized void cargarIncidencias(List<Incidencia> incidenciasGuardadas) {
        this.incidencias = new ArrayList<>(incidenciasGuardadas);
        Logger.info("Incidencias cargadas en el gestor: " + incidencias.size());
    }
}
