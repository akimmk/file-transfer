package org.example.controllers;

import org.example.models.History;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class HistoryCellController {

    @FXML
    private ImageView statusIcon;

    @FXML
    private Label fileNameLabel;

    @FXML
    private Label detailsLabel;

    public void setData(History history) {
        // Set file name
        fileNameLabel.setText(history.getFileName());

        // Set details (transfer type and timestamp)
        detailsLabel.setText(history.getTransferType() + " | " + history.getTimestamp());

        // Set status icon based on transfer type
        String iconPath = history.getTransferType().equals("Sent")
                ? "/images/upload-icon.png"
                : "/images/download-icon.png";
        statusIcon.setImage(new Image(getClass().getResource(iconPath).toExternalForm()));
    }
}
