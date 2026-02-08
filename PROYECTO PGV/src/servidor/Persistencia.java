package servidor;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase para guardar y cargar incidencias en disco.
 * Usa un formato de texto simple (CSV) para facilitar la lectura.
 * 
 * FORMATO DEL ARCHIVO:
 * id|descripcion|fechaHora|estado|usuario
 * 
 * FUNCIONALIDAD EXTRA: Persistencia de datos
 */
public class Persistencia {
    private static final String ARCHIVO_DATOS = "incidencias.dat";
    private static final String SEPARADOR = "|";
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Guarda todas las incidencias en un archivo
     * 
     * @param incidencias Lista de incidencias a guardar
     */
    public static void guardar(List<Incidencia> incidencias) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(ARCHIVO_DATOS))) {
            // Guardar cada incidencia en una línea
            for (Incidencia inc : incidencias) {
                String linea = String.format("%d%s%s%s%s%s%s%s%s",
                        inc.getId(),
                        SEPARADOR,
                        inc.getDescripcion(),
                        SEPARADOR,
                        inc.getFechaHora().format(FORMATO_FECHA),
                        SEPARADOR,
                        inc.getEstado(),
                        SEPARADOR,
                        inc.getUsuario());
                writer.println(linea);
            }

            Logger.info("Incidencias guardadas en disco: " + incidencias.size() + " registros");

        } catch (IOException e) {
            Logger.error("Error al guardar incidencias: " + e.getMessage());
        }
    }

    /**
     * Carga las incidencias desde el archivo
     * 
     * @return Lista de incidencias cargadas (vacía si no existe el archivo)
     */
    public static List<Incidencia> cargar() {
        List<Incidencia> incidencias = new ArrayList<>();
        File archivo = new File(ARCHIVO_DATOS);

        // Si no existe el archivo, devolver lista vacía
        if (!archivo.exists()) {
            Logger.info("No hay archivo de persistencia. Iniciando con base de datos vacía.");
            return incidencias;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(archivo))) {
            String linea;
            int maxId = 0;

            // Leer cada línea del archivo
            while ((linea = reader.readLine()) != null) {
                try {
                    // Dividir la línea por el separador
                    String[] partes = linea.split("\\" + SEPARADOR);

                    if (partes.length == 5) {
                        // Parsear los datos
                        int id = Integer.parseInt(partes[0]);
                        String descripcion = partes[1];
                        LocalDateTime fechaHora = LocalDateTime.parse(partes[2], FORMATO_FECHA);
                        String estado = partes[3];
                        String usuario = partes[4];

                        // Crear la incidencia con todos los datos originales
                        Incidencia inc = new Incidencia(id, descripcion, fechaHora, estado, usuario);

                        incidencias.add(inc);

                        // Rastrear el ID máximo para ajustar el contador
                        if (id > maxId) {
                            maxId = id;
                        }
                    }

                } catch (Exception e) {
                    Logger.warning("Línea corrupta en archivo de datos: " + linea);
                }
            }

            Logger.info("Incidencias cargadas desde disco: " + incidencias.size() + " registros");

        } catch (IOException e) {
            Logger.error("Error al cargar incidencias: " + e.getMessage());
        }

        return incidencias;
    }

    /**
     * Guarda automáticamente las incidencias cada cierto tiempo
     * Este método podría llamarse desde un hilo separado
     */
    public static void guardarAutomatico(GestorIncidencias gestor) {
        guardar(gestor.listarIncidencias());
    }
}
