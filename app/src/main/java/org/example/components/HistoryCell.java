package org.example.components;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.example.models.History;

public class HistoryCell extends ListCell<History> {

    private ImageView typeIcon;
    private Label fileNameLabel;
    private Label detailsLabel;
    private Label statusLabel;
    private static final Map<String, String> ICON_MAP = new HashMap<>();

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
        } else {
            try {
                // Load custom FXML for the cell
                HBox hBox = FXMLLoader.load(getClass().getResource("/components/HistoryCell.fxml"));
                // Set data to the controller
                typeIcon = (ImageView) hBox.getChildren().getFirst();
                fileNameLabel = (Label) (((VBox) ((HBox) hBox.getChildren().getLast()).getChildren().getFirst())
                        .getChildren()
                        .getFirst());

                detailsLabel = (Label) (((VBox) ((HBox) hBox.getChildren().getLast()).getChildren().getFirst())
                        .getChildren()
                        .getLast());

                statusLabel = (Label) ((HBox) hBox.getChildren().getLast()).getChildren().getLast();
                statusLabel.setText(history.getTransferType());
                fileNameLabel.setText(history.getFileName());

                // Set details (transfer type and timestamp)
                detailsLabel.setText(history.getFilePath() + " | " + history.getTimestamp());
                String fileType = getFileType(history.getFileName());
                String iconPath = ICON_MAP.getOrDefault(fileType, ICON_MAP.get("default"));

                typeIcon.setImage(new Image(getClass().getResource(iconPath).toExternalForm()));
                typeIcon.setFitWidth(40);
                setGraphic(hBox);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
