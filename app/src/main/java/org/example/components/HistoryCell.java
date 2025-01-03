package org.example.components;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.example.controllers.AppController;
import org.example.models.History;
import org.example.utils.FileUtils;

public class HistoryCell extends ListCell<History> {

    private ImageView typeIcon;
    private Label fileNameLabel;
    private Label detailsLabel;
    private Label statusLabel;
    private ImageView optionIcon;
    private static final Map<String, String> ICON_MAP = new HashMap<>();
    private AppController appController;

    public HistoryCell(AppController appController) {
        this.appController = appController;
    }

    static {
        ICON_MAP.put("image", "/images/image-icon.png");
        ICON_MAP.put("video", "/images/video-icon.png");
        ICON_MAP.put("document", "/images/doc-icon.png");
        ICON_MAP.put("zip", "/images/zip-icon.png");
        ICON_MAP.put("default", "/images/other-icon.png"); // Fallback icon
    }

    private String getFileType(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return "default";
        }

        String extension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();

        switch (extension) {
            case "jpg":
            case "jpeg":
            case "png":
            case "gif":
                return "image";
            case "mp4":
            case "avi":
            case "mov":
            case "mkv":
                return "video";
            case "doc":
            case "docx":
            case "pdf":
            case "txt":
                return "document";
            case "zip":
            case "rar":
                return "zip";
            default:
                return "default";
        }
    }

    @Override
    protected void updateItem(History history, boolean empty) {
        super.updateItem(history, empty);

        if (empty || history == null) {
            setGraphic(null);
            setContextMenu(null);
        } else {
            try {
                // Load custom FXML for the cell
                HBox hBox = FXMLLoader.load(getClass().getResource("/components/HistoryCell.fxml"));
                // Set data to the controller
                typeIcon = (ImageView) hBox.lookup("#typeIcon");
                fileNameLabel = (Label) hBox.lookup("#fileNameLabel");

                detailsLabel = (Label) hBox.lookup("#detailsLabel");

                statusLabel = (Label) hBox.lookup("#statusLabel");
                optionIcon = (ImageView) hBox.lookup("#optionIcon");
                optionIcon.setImage(new Image(getClass().getResource("/images/option-icon.png").toExternalForm()));

                statusLabel.setText(history.getTransferType());
                fileNameLabel.setText(history.getFileName());

                // Set details (transfer type and timestamp)
                detailsLabel.setText(history.getFilePath() + " | " + history.getTimestamp());
                String fileType = getFileType(history.getFileName());
                String iconPath = ICON_MAP.getOrDefault(fileType, ICON_MAP.get("default"));

                typeIcon.setImage(new Image(getClass().getResource(iconPath).toExternalForm()));
                typeIcon.setFitWidth(40);

                ContextMenu contextMenu = new ContextMenu();

                MenuItem openItem = new MenuItem("open");
                openItem.setOnAction(event -> handleOpen(history));
                MenuItem openFolder = new MenuItem("open folder");
                openFolder.setOnAction(event -> handleOpenFolder(history));
                MenuItem deleteItem = new MenuItem("delete file");
                deleteItem.setOnAction(event -> handleDelete(history));
                MenuItem removeItem = new MenuItem("remove from history");
                removeItem.setOnAction(event -> handleRemove(history));

                contextMenu.getItems().addAll(openItem, openFolder, deleteItem, removeItem);

                optionIcon.setOnMousePressed(event -> {
                    contextMenu.show(optionIcon, event.getScreenX(), event.getScreenY());
                });

                setEditable(false);
                setFocusTraversable(false);
                setFocused(false);

                setContextMenu(contextMenu);

                setGraphic(hBox);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleOpen(History history) {
        File file = new File(history.getFilePath()); 

        FileUtils.openFile(file);
    }

    private void handleOpenFolder(History history) {
        File file = new File(history.getFilePath()); 

        FileUtils.openFileFolder(file);
    }

    private void handleRemove(History history) {
        // Delete the record from history.txt
        File historyFile = new File("history.txt");
        if (historyFile.exists() && historyFile.isFile()) {
            try {
                // Read all lines and filter out the record to be deleted
                List<String> lines = Files.readAllLines(historyFile.toPath());
                List<String> updatedLines = lines.stream()
                        .filter(line -> !line.contains(history.getFilePath()))
                        .toList();

                // Write the updated lines back to history.txt
                Files.write(historyFile.toPath(), updatedLines, StandardOpenOption.TRUNCATE_EXISTING);
                appController.updateHistoryListView(history, true);
                System.out.println("Record removed from history.txt: " + history.getFileName());
            } catch (IOException e) {
                System.out.println("Error updating history.txt: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.out.println("history.txt file does not exist.");
        }
    }

    private void handleDelete(History history) {
        File file = new File(history.getFilePath());

        // Delete the actual file
        boolean fileDeleted = FileUtils.handleDelete(file);

        // Delete the record from history.txt
        File historyFile = new File("history.txt");
        if (historyFile.exists() && historyFile.isFile()) {
            try {
                // Read all lines and filter out the record to be deleted
                List<String> lines = Files.readAllLines(historyFile.toPath());
                List<String> updatedLines = lines.stream()
                        .filter(line -> !line.contains(history.getFilePath()))
                        .toList();

                // Write the updated lines back to history.txt
                Files.write(historyFile.toPath(), updatedLines, StandardOpenOption.TRUNCATE_EXISTING);
                appController.updateHistoryListView(history, true);
                System.out.println("Record removed from history.txt: " + history.getFileName());
            } catch (IOException e) {
                System.out.println("Error updating history.txt: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.out.println("history.txt file does not exist.");
        }
    }
}
