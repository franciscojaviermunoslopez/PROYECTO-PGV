package servidor;

/**
 * Autenticador que usaba una API externa para validar usuarios.
 * 
 * NOTA: API DESHABILITADA - La API reqres.in cambió a servicio de pago en
 * febrero 2026.
 * Este módulo mantiene la estructura por si se desea integrar otra API en el
 * futuro.
 */
public class AutenticadorAPI {

    /**
     * Autentica un usuario usando la API externa
     * 
     * NOTA: API DESHABILITADA - La API de reqres.in ahora requiere suscripción de
     * pago.
     * Este método siempre retorna false para usar autenticación local.
     * 
     * @param email    Email del usuario
     * @param password Contraseña
     * @return false (API deshabilitada)
     */
    public static boolean autenticarConAPI(String email, String password) {
        // API DESHABILITADA: reqres.in cambió a servicio de pago que requiere API keys
        // Retornamos false para que el sistema use solo autenticación local
        Logger.info("API de autenticación deshabilitada - usando solo usuarios locales");
        return false;

        /*
         * CÓDIGO ORIGINAL COMENTADO - La API ya no funciona sin API key
         * try {
         * // Crear la conexión HTTP
         * URL url = URI.create(API_URL).toURL();
         * HttpURLConnection conn = (HttpURLConnection) url.openConnection();
         * 
         * // Configurar la petición POST
         * conn.setRequestMethod("POST");
         * conn.setRequestProperty("Content-Type", "application/json");
         * conn.setDoOutput(true);
         * 
         * // Crear el JSON con las credenciales
         * // Formato: {"email": "usuario", "password": "contraseña"}
         * String jsonInput = String.format(
         * "{\"email\":\"%s\",\"password\":\"%s\"}",
         * email,
         * password);
         * 
         * // Enviar la petición
         * try (OutputStream os = conn.getOutputStream()) {
         * byte[] input = jsonInput.getBytes("utf-8");
         * os.write(input, 0, input.length);
         * }
         * 
         * // Leer la respuesta
         * int codigoRespuesta = conn.getResponseCode();
         * 
         * // Si el código es 200, la autenticación fue exitosa
         * if (codigoRespuesta == 200) {
         * // Leer el token de respuesta (aunque no lo usamos)
         * BufferedReader br = new BufferedReader(
         * new InputStreamReader(conn.getInputStream(), "utf-8"));
         * StringBuilder response = new StringBuilder();
         * String linea;
         * while ((linea = br.readLine()) != null) {
         * response.append(linea.trim());
         * }
         * 
         * Logger.info("Autenticación API exitosa para: " + email);
         * Logger.debug("Respuesta API: " + response.toString());
         * 
         * return true;
         * } else {
         * Logger.warning("Autenticación API fallida para: " + email + " (Código: " +
         * codigoRespuesta + ")");
         * return false;
         * }
         * 
         * } catch (Exception e) {
         * Logger.error("Error al conectar con la API: " + e.getMessage());
         * return false;
         * }
         */
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
