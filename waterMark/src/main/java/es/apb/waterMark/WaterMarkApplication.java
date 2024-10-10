package es.apb.waterMark;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
public class WaterMarkApplication extends SpringBootServletInitializer {

	public static void main(String... args) {
		SpringApplication.run(WaterMarkApplication.class, args);
	}
}
