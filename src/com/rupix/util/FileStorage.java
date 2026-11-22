
package com.rupix.util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

public class FileStorage {

    private static final Path DATA_DIRECTORY =
            Path.of("data");

    // Create the data folder if it does not exist
    public static void initialize() {
        try {
            Files.createDirectories(DATA_DIRECTORY);
        } catch (IOException e) {
            throw new IllegalStateException(
                    "Unable to create data directory.", e
            );
        }
    }

    // Read all lines from a text file
    public static synchronized List<String> readLines(
            String fileName
    ) {
        Path file = resolveFile(fileName);

        try {
            if (!Files.exists(file)) {
                return new ArrayList<>();
            }

            return Files.readAllLines(
                    file,
                    StandardCharsets.UTF_8
            );

        } catch (IOException e) {
            throw new IllegalStateException(
                    "Unable to read file: " + fileName, e
            );
        }
    }

    // Save all lines, replacing existing content
    public static synchronized void writeLines(
            String fileName,
            List<String> lines
    ) {
        Path file = resolveFile(fileName);

        try {
            Path tempFile = Files.createTempFile(
                    DATA_DIRECTORY,
                    "rupix-",
                    ".tmp"
            );

            try {
                Files.write(
                        tempFile,
                        lines,
                        StandardCharsets.UTF_8
                );

                try {
                    Files.move(
                            tempFile,
                            file,
                            StandardCopyOption.REPLACE_EXISTING,
                            StandardCopyOption.ATOMIC_MOVE
                    );
                } catch (java.nio.file.AtomicMoveNotSupportedException e) {
                    Files.move(
                            tempFile,
                            file,
                            StandardCopyOption.REPLACE_EXISTING
                    );
                }

            } finally {
                Files.deleteIfExists(tempFile);
            }

        } catch (IOException e) {
            throw new IllegalStateException(
                    "Unable to write file: " + fileName, e
            );
        }
    }

    // Add a new line without deleting previous records
    public static synchronized void appendLine(
            String fileName,
            String line
    ) {
        List<String> lines = readLines(fileName);
        lines.add(line);
        writeLines(fileName, lines);
    }

    // Resolve a file inside the data directory
    private static Path resolveFile(String fileName) {

        if (fileName == null ||
                !fileName.matches("[a-zA-Z0-9_-]+\\.txt")) {
            throw new IllegalArgumentException(
                    "Invalid file name. Use a .txt file."
            );
        }

        initialize();

        return DATA_DIRECTORY.resolve(fileName);
    }
}
