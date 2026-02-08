package servidor;

/**
 * Clase que representa un usuario del sistema.
 * Almacena nombre de usuario, contraseña y rol (USUARIO o ADMINISTRADOR).
 */
public class Usuario {
    private String nombreUsuario;
    private String contrasena;
    private String rol; // "USUARIO" o "ADMINISTRADOR"

    /**
     * Constructor: crea un usuario con sus credenciales y rol
     */
    public Usuario(String nombreUsuario, String contrasena, String rol) {
        this.nombreUsuario = nombreUsuario;
        this.contrasena = contrasena;
        this.rol = rol;
    }

    // Getters
    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public String getContrasena() {
        return contrasena;
    }

    public String getRol() {
        return rol;
    }

    /**
     * Verifica si la contraseña es correcta
     */
    public boolean verificarContrasena(String contrasena) {
        return this.contrasena.equals(contrasena);
    }
}
