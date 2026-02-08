package servidor;

import java.util.HashMap;
import java.util.Map;

/**
 * Clase que gestiona los usuarios del sistema.
 * Almacena usuarios predefinidos y valida credenciales.
 */
public class GestorUsuarios {
    // Mapa que almacena usuarios: clave = nombre de usuario, valor = objeto Usuario
    private Map<String, Usuario> usuarios;

    /**
     * Constructor: crea usuarios de prueba
     */
    public GestorUsuarios() {
        usuarios = new HashMap<>();

        // Crear usuarios de prueba (puedes cambiar estos datos)
        usuarios.put("admin", new Usuario("admin", "admin123", "ADMINISTRADOR"));
        usuarios.put("juan", new Usuario("juan", "juan123", "USUARIO"));
        usuarios.put("maria", new Usuario("maria", "maria123", "USUARIO"));
    }

    /**
     * Autentica un usuario verificando sus credenciales
     * FUNCIONALIDAD EXTRA: Primero intenta con API REST, luego con autenticación local
     * 
     * @param nombreUsuario Nombre de usuario o email
     * @param contrasena Contraseña
     * @return El objeto Usuario si las credenciales son correctas, null si no
     */
    public Usuario autenticar(String nombreUsuario, String contrasena) {
        // FUNCIONALIDAD EXTRA: Intentar primero con API REST
        Logger.info("Intentando autenticación con API para: " + nombreUsuario);
        
        if (AutenticadorAPI.autenticarConAPI(nombreUsuario, contrasena)) {
            // Autenticación exitosa con API
            String rol = AutenticadorAPI.determinarRol(nombreUsuario);
            Logger.info("✓ Autenticación con API exitosa: " + nombreUsuario + " (" + rol + ")");
            return new Usuario(nombreUsuario, contrasena, rol);
        }
        
        // Si falla la API, intentar con autenticación local (backup)
        Logger.info("API falló, intentando autenticación local...");
        Usuario usuario = usuarios.get(nombreUsuario);
        
        // Verificar si existe el usuario y si la contraseña es correcta
        if (usuario != null && usuario.verificarContrasena(contrasena)) {
            Logger.info("✓ Autenticación local exitosa: " + nombreUsuario);
            return usuario;
        }
        
        Logger.warning("✗ Autenticación fallida para: " + nombreUsuario);
        return null; // Credenciales incorrectas en ambos sistemas
    }
}
