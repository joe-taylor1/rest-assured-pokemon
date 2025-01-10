import io.restassured.module.jsv.JsonSchemaValidator;

import java.io.InputStream;
import java.net.URL;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchema;

public class CustomJsonSchemaValidator {

    public static JsonSchemaValidator matchesJsonSchemaInClasspath(String pathToSchemaInClasspath) {
        URL schemaResource = Thread.currentThread().getContextClassLoader().getResource(pathToSchemaInClasspath);
        if (schemaResource == null) {
            throw new RuntimeException("Schema file not found in classpath: " + pathToSchemaInClasspath);
        }

        try (InputStream inputStream = schemaResource.openStream()) {
            return matchesJsonSchema(inputStream);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load schema from classpath: " + pathToSchemaInClasspath, e);
        }
    }
}