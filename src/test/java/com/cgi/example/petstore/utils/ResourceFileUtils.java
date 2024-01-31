package com.cgi.example.petstore.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ResourceFileUtils {

    public String readFile(String filePath) {
        Path path =  Paths.get("src/test/resources", filePath);

        try {
            return Files.readString(path);
        } catch (IOException e) {
            String message = "Given the supplied file path [%s] unable to read the file [%s]"
                    .formatted(filePath, path.toFile().getAbsolutePath());
            throw new RuntimeException(message, e);
        }
    }
}
