package app.fitkit.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FitKitBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(FitKitBackendApplication.class, args);
		System.out.println("\t STARTED\t");
	}

}
