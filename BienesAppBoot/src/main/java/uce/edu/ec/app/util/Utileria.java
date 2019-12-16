package uce.edu.ec.app.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.servlet.http.HttpServletRequest;
import org.springframework.web.multipart.MultipartFile;

public class Utileria {
	
	/**
	 * Metodo que guarda una imagen atraves de un formulario HTML al disco duro.
	 * @param multiPart
	 * @param request
	 * @return
	 */
	public static String guardarImagen(MultipartFile multiPart, HttpServletRequest request) {
		String upload_folder = ".//src//main//webapp//resources//images//";
		// Obtenemos el nombre original del archivo.
		String nombreOriginal = multiPart.getOriginalFilename();
		// Reemplazamos en el nombre de archivo los espacios por guiones.
		nombreOriginal = nombreOriginal.replace(" ", "-");
		// Agregamos al nombre del archivo 8 caracteres aleatorios para evitar
		// duplicados.
		String nombreFinal = randomAlphaNumeric(8) + nombreOriginal;
		// Obtenemos la ruta ABSOLUTA del directorio images.
		// apache-tomcat/webapps/cineapp/resources/images/

		try {
			// Formamos el nombre del archivo para guardarlo en el disco duro.

			byte[] bytes = multiPart.getBytes();
			Path path = Paths.get(upload_folder + nombreFinal);
			// Aqui se guarda fisicamente el archivo en el disco duro.
			Files.write(path, bytes);

			return nombreFinal;
		} catch (IOException e) {
			System.out.println("Error " + e.getMessage());
			return null;
		}
	}

	/**
	 * Metodo para generar una cadena aleatoria de longitud N
	 * @param count
	 * @return
	 */
	public static String randomAlphaNumeric(int count) {
		String CARACTERES = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
		StringBuilder builder = new StringBuilder();
		while (count-- != 0) {
			int character = (int) (Math.random() * CARACTERES.length());
			builder.append(CARACTERES.charAt(character));
		}
		return builder.toString();
	}
}