package com.dslexample.support;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class TestUtil {

    public static List<File> getJobFiles() {
        File jobsDir = new File("src/jobs");
        List<File> files = new ArrayList<>();
        for (File file : jobsDir.listFiles()) {
            if (file.getName().endsWith(".groovy")) {
                files.add(file);
            }
        }
        return files;
    }

    /**
     * Write a single XML file, creating any nested dirs.
     */
    public static void writeFile(File dir, String name, String xml) throws IOException {
        Path pathToFile = dir.toPath().resolve(name + ".xml");

        // Ensure all directories exist
        Files.createDirectories(pathToFile.getParent());

         // Write content to file
         Files.write(pathToFile, xml.getBytes());
    }
}
