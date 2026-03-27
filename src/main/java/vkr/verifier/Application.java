package vkr.verifier;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {
	public static void main(String[] args) {
		String baseDir = System.getProperty("user.dir");
		System.out.println(baseDir.toString());
		SpringApplication.run(Application.class, args);
	}
}
