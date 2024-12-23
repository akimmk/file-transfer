package org.example.controllers;

import java.io.File;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class AppController {
    @FXML
    private Button sendButton;

    @FXML
    private Button receiveButton;

    @FXML
    private Button browseButton;

    @FXML
    private TextField pathTextField;

    public String path;

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

    public void browse(ActionEvent e) {
        Stage stage = (Stage) browseButton.getScene().getWindow();
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(stage);
        pathTextField.setText(file.getPath());
    }
}
