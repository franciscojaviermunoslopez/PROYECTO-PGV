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
     * Usa solo autenticación local (la API externa fue deshabilitada)
     * 
     * @param nombreUsuario Nombre de usuario
     * @param contrasena    Contraseña
     * @return El objeto Usuario si las credenciales son correctas, null si no
     */
    public Usuario autenticar(String nombreUsuario, String contrasena) {
        // Nota: La autenticación con API REST está deshabilitada
        // Solo se usan usuarios locales: admin, juan, maria
        Logger.info("Verificando credenciales para: " + nombreUsuario);

        // Intentar autenticación local
        Usuario usuario = usuarios.get(nombreUsuario);

        // Verificar si existe el usuario y si la contraseña es correcta
        if (usuario != null && usuario.verificarContrasena(contrasena)) {
            Logger.info("Autenticación exitosa: " + nombreUsuario + " (Rol: " + usuario.getRol() + ")");
            return usuario;
        }

        Logger.warning("Autenticación fallida para: " + nombreUsuario);
        return null; // Credenciales incorrectas
    }
}
