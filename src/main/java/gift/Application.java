package gift;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.validation.annotation.Validated;

@SpringBootApplication
@Validated
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
