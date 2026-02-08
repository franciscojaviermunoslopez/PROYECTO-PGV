package servidor;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

/**
 * Autenticador que usa una API externa para validar usuarios.
 * 
 * FUNCIONALIDAD EXTRA: Autenticación con JSON y API
 * 
 * USA LA API PÚBLICA: https://reqres.in (API de prueba gratuita)
 * 
 * Usuarios válidos en esta API:
 * - eve.holt@reqres.in / cityslicka (administrador en nuestro sistema)
 * - charles.morris@reqres.in / password123 (usuario normal)
 * - george.bluth@reqres.in / password123 (usuario normal)
 */
public class AutenticadorAPI {
    // URL de la API de prueba (gratuita y pública)
    private static final String API_URL = "https://reqres.in/api/login";

    /**
     * Autentica un usuario usando la API externa
     * 
     * @param email    Email del usuario
     * @param password Contraseña
     * @return true si las credenciales son válidas, false si no
     */
    public static boolean autenticarConAPI(String email, String password) {
        try {
            // Crear la conexión HTTP
            URL url = URI.create(API_URL).toURL();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            // Configurar la petición POST
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            // Crear el JSON con las credenciales
            // Formato: {"email": "usuario", "password": "contraseña"}
            String jsonInput = String.format(
                    "{\"email\":\"%s\",\"password\":\"%s\"}",
                    email,
                    password);

            // Enviar la petición
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonInput.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // Leer la respuesta
            int codigoRespuesta = conn.getResponseCode();

            // Si el código es 200, la autenticación fue exitosa
            if (codigoRespuesta == 200) {
                // Leer el token de respuesta (aunque no lo usamos)
                BufferedReader br = new BufferedReader(
                        new InputStreamReader(conn.getInputStream(), "utf-8"));
                StringBuilder response = new StringBuilder();
                String linea;
                while ((linea = br.readLine()) != null) {
                    response.append(linea.trim());
                }

                Logger.info("Autenticación API exitosa para: " + email);
                Logger.debug("Respuesta API: " + response.toString());

                return true;
            } else {
                Logger.warning("Autenticación API fallida para: " + email + " (Código: " + codigoRespuesta + ")");
                return false;
            }

        } catch (Exception e) {
            Logger.error("Error al conectar con la API: " + e.getMessage());
            return false;
        }
    }

    /**
     * Determina el rol según el email
     * En un sistema real, esto vendría de la API
     * 
     * @param email Email del usuario
     * @return Rol del usuario
     */
    public static String determinarRol(String email) {
        // El primer usuario de la API será el administrador
        if (email.equals("eve.holt@reqres.in")) {
            return "ADMINISTRADOR";
        } else {
            return "USUARIO";
        }
    }
}
