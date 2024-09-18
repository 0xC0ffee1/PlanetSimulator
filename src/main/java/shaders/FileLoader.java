package shaders;

import java.io.File;
import java.io.InputStream;
import java.util.Scanner;

public class FileLoader {
    public static String load(String file){
        String output = "";
        // Use ClassLoader to get the resource InputStream
        try (InputStream inputStream = FileLoader.class.getClassLoader().getResourceAsStream(file)) {
            if (inputStream != null) {
                Scanner scanner = new Scanner(inputStream, "UTF-8");
                output = scanner.useDelimiter("\\A").next();
                scanner.close(); // Close the scanner to free resources
            } else {
                throw new IllegalArgumentException("File not found: " + file);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return output;
    }
}
