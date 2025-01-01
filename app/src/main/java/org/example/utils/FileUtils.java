package org.example.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.List;

import org.example.models.History;

/**
 * FileUtils
 */
public class FileUtils {

    public static void openFile(File file) {
        if (file.exists()) {
            try {
                // Cross-platform way to open files using ProcessBuilder
                String os = System.getProperty("os.name").toLowerCase();

                if (os.contains("win")) {
                    // Windows
                    new ProcessBuilder("cmd", "/c", file.getAbsolutePath()).start();
                } else if (os.contains("mac")) {
                    // macOS
                    new ProcessBuilder("open", file.getAbsolutePath()).start();
                } else if (os.contains("nix") || os.contains("nux")) {
                    // Linux/Unix
                    new ProcessBuilder("xdg-open", file.getAbsolutePath()).start();
                } else {
                    throw new UnsupportedOperationException("Unsupported operating system");
                }

            } catch (IOException e) {
                System.err.println("Failed to open file: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            AlertUtils.showErrorAlert("File Not Found",
                    "The file you are looking for has been moved or permanently removed.");
        }
    }

    public static void openFileFolder(File file) {
        if (file.exists()) {
            File folder = file.getParentFile(); // Get the parent directory of the file

            try {
                String os = System.getProperty("os.name").toLowerCase();

                if (os.contains("win")) {
                    // Windows
                    new ProcessBuilder("explorer", folder.getAbsolutePath()).start();
                } else if (os.contains("mac")) {
                    // macOS
                    new ProcessBuilder("open", folder.getAbsolutePath()).start();
                } else if (os.contains("nix") || os.contains("nux")) {
                    // Linux/Unix
                    new ProcessBuilder("xdg-open", folder.getAbsolutePath()).start();
                } else {
                    throw new UnsupportedOperationException("Unsupported operating system");
                }

                System.out.println("Folder opened: " + folder.getAbsolutePath());
            } catch (IOException e) {
                System.err.println("Failed to open folder: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            AlertUtils.showErrorAlert("Folder Not Found",
                    "The folder you are looking for has been moved or permanently removed.");
        }
    }

    public static boolean handleDelete(File file) {
        boolean fileDeleted = false;
        if (file.exists()) {
            fileDeleted = file.delete();
            boolean isConfirmed = AlertUtils.showConfirmationAlert("Delete" + file.toString(),
                    "Are you sure to delete the file?");
            if (isConfirmed) {
                if (fileDeleted) {
                    AlertUtils.showInfoAlert("File Deleted", file.toString() + " is deleted successfully");
                } else {
                    AlertUtils.showInfoAlert("Unknown Error", "Failed to delete " + file.toString());
                }
            }
            return fileDeleted;
        }
        AlertUtils.showErrorAlert("File Not Found",
                "The file is not deleted");
        return fileDeleted;
    }

}
