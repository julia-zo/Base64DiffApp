package org.juliazo.diff;

/*
  Base64 diff application
  This app is a REST application composed of two POST endpoints (v1/diff/{id}/left and v1/diff/{id}/right)
  that receive a base64 encoded data and one GET endpoint (v1/diff/{id}) that performs a diff operation
  of the data provided on both POST endpoints. The result does not contain the actual diff, only an indication
  of how many bytes are different and where they are located.

  This application was developed using SpringBoot Framework.

  How to execute it:
  run mvn clean package
  run mvn spring-boot:run
  app will be available on http://localhost:8080

  @author Julia Zottis
 */
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Base64DiffApp {

    /**
     * The constant logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(Base64DiffApp.class);

    public static void main(String[] args) {
        logger.info("Starting Base64 Diff Application");
        SpringApplication.run(Base64DiffApp.class, args);
    }

}
