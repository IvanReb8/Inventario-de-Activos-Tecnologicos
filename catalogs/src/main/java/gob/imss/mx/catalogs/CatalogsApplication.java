package gob.imss.mx.catalogs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Clase principal de arranque de la aplicación Spring Boot.
 *
 * Marca el inicio del servicio `products` y activa el escaneo de componentes,
 * configuración automática y todos los mecanismos de arranque de Spring Boot.
 */
@SpringBootApplication
public class CatalogsApplication {

	public static void main(String[] args) {
		SpringApplication.run(CatalogsApplication.class, args);
	}

}
