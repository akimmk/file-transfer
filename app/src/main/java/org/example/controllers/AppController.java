package org.example.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class AppController {
    @FXML
    private Button sendButton;

    @FXML
    private Button receiveButton;

    @FXML
    public void initialize() {
        Image sendImage = new Image(getClass().getResource("/images/up-arrow.png").toExternalForm());
        Image receiveImage = new Image(getClass().getResource("/images/down-arrow.png").toExternalForm());

        // Create ImageView for each image
        ImageView sendImageView = new ImageView(sendImage);
        ImageView receiveImageView = new ImageView(receiveImage);

        // Set the size of the images if needed
        sendImageView.setFitWidth(150);
        sendImageView.setFitHeight(150);
        receiveImageView.setFitWidth(150);
        receiveImageView.setFitHeight(150);

        // Attach images to the buttons
        sendButton.setGraphic(sendImageView);
        receiveButton.setGraphic(receiveImageView);
    }

    public void send(ActionEvent e) {
        System.out.println("helllllllo");
    }
}
