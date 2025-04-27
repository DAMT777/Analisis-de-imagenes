//package pruebas;
//
//import com.cloudinary.Cloudinary;
//import com.cloudinary.utils.ObjectUtils;
//import io.github.cdimascio.dotenv.Dotenv;
//
//import java.util.Map;
//
//public class CloudinaryTest {
//    public static void main(String[] args) {
//        // Cargar las credenciales desde el archivo .env
//        Dotenv dotenv = Dotenv.load();
//        String cloudName = dotenv.get("CLOUDINARY_CLOUD_NAME");
//        String apiKey = dotenv.get("CLOUDINARY_API_KEY");
//        String apiSecret = dotenv.get("CLOUDINARY_API_SECRET");
//
//        // Crear una instancia de Cloudinary con las credenciales
//        Cloudinary cloudinary = new Cloudinary(ObjectUtils.asMap(
//                "cloud_name", cloudName,
//                "api_key", apiKey,
//                "api_secret", apiSecret));
//
//        try {
//            Map<String, Object> accountInfo = cloudinary.api().usage(ObjectUtils.emptyMap());
//            System.out.println("Conexión exitosa con Cloudinary!");
//            System.out.println("Información de la cuenta: " + accountInfo);
//        } catch (Exception e) {
//            System.out.println("Error al conectar con Cloudinary: " + e.getMessage());
//        }
//    }
//}
