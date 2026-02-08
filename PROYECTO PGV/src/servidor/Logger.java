package servidor;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Sistema de logging con niveles para registrar eventos del servidor.
 * 
 * NIVELES DE LOG:
 * - ERROR: Errores críticos que necesitan atención
 * - WARNING: Advertencias, situaciones anormales pero controladas
 * - INFO: Información general del sistema
 * - DEBUG: Información detallada para depuración
 */
public class Logger {
    // Niveles de log disponibles
    public enum Nivel {
        ERROR, // Más crítico
        WARNING,
        INFO,
        DEBUG // Menos crítico
    }

    // Configuración
    private static final String ARCHIVO_LOG = "servidor_logs.txt";
    private static Nivel nivelMinimo = Nivel.INFO; // Solo registra INFO, WARNING y ERROR
    private static PrintWriter escritor;

    /**
     * Inicializa el sistema de logging
     */
    public static void inicializar() {
        try {
            // Crear archivo de log (append = true para no borrar logs anteriores)
            escritor = new PrintWriter(new FileWriter(ARCHIVO_LOG, true), true);
            info("Sistema de logging inicializado");
        } catch (IOException e) {
            System.err.println("ERROR: No se pudo inicializar el sistema de logging");
        }
    }

    /**
     * Configura el nivel mínimo de logging
     * Por ejemplo: si pones WARNING, solo se registrarán WARNING y ERROR
     */
    public static void setNivelMinimo(Nivel nivel) {
        nivelMinimo = nivel;
    }

    /**
     * Registra un mensaje de ERROR (lo más crítico)
     */
    public static void error(String mensaje) {
        log(Nivel.ERROR, mensaje);
    }

    /**
     * Registra un mensaje de WARNING (advertencia)
     */
    public static void warning(String mensaje) {
        log(Nivel.WARNING, mensaje);
    }

    /**
     * Registra un mensaje de INFO (información general)
     */
    public static void info(String mensaje) {
        log(Nivel.INFO, mensaje);
    }

    /**
     * Registra un mensaje de DEBUG (depuración detallada)
     */
    public static void debug(String mensaje) {
        log(Nivel.DEBUG, mensaje);
    }

    /**
     * Método principal que registra el mensaje si cumple el nivel mínimo
     */
    private static void log(Nivel nivel, String mensaje) {
        // Solo registrar si el nivel es igual o más crítico que el mínimo
        if (nivel.ordinal() <= nivelMinimo.ordinal()) {
            String timestamp = LocalDateTime.now().format(
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

            String lineaLog = String.format("[%s] [%s] %s", timestamp, nivel, mensaje);

            // Escribir en consola
            if (nivel == Nivel.ERROR || nivel == Nivel.WARNING) {
                System.err.println(lineaLog);
            } else {
                System.out.println(lineaLog);
            }

            // Escribir en archivo
            if (escritor != null) {
                escritor.println(lineaLog);
            }
        }
    }

    /**
     * Cierra el sistema de logging
     */
    public static void cerrar() {
        if (escritor != null) {
            info("Sistema de logging cerrado");
            escritor.close();
        }
    }
}
