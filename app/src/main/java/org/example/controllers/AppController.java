package org.example.controllers;

import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;

import org.example.models.History;
import org.example.models.HistoryManager;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.DirectoryChooser;

public class AppController {

    @FXML
    private Button sendButton;

    @FXML
    private Button receiveButton;

    @FXML
    private Button browseButton;

    @FXML
    private TextField pathTextField;

    @FXML
    private Label statusLabel; // Label to show connection status

    @FXML
    private ListView<String> historyListView;

    private String localIpAddress;
    private int port = 5000;

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

        List<History> historyList = HistoryManager.loadHistory();
        for (History history : historyList) {
            historyListView.getItems().add(history.toString());
        }
    }

    private String getLocalIPAddress() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface networkInterface = interfaces.nextElement();
                Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();

                while (addresses.hasMoreElements()) {
                    InetAddress address = addresses.nextElement();
                    if (!address.isLoopbackAddress() && address.isSiteLocalAddress()) {
                        return address.getHostAddress(); // Return local IP
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error retrieving IP address: " + e.getMessage());
        }
        return null;
    }

    public void browse(ActionEvent e) {
        Stage stage = (Stage) browseButton.getScene().getWindow();
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            pathTextField.setText(file.getPath());
        }
    }

    public void send(ActionEvent e) {
        if (pathTextField.getText().isEmpty()) {
            statusLabel.setText("Please select a file first.");
            browse(e);
            return;
        }

        File file = new File(pathTextField.getText());
        if (!file.exists() || !file.isFile()) {
            statusLabel.setText("Invalid file selected.");
            return;
        }

        // Get the local IP address
        localIpAddress = getLocalIPAddress();
        if (localIpAddress == null) {
            statusLabel.setText("Could not determine the local IP address.");
            return;
        }

        // Update the status label with the sender's IP address
        statusLabel.setText("Sender IP: " + localIpAddress + " | Waiting for receiver to connect...");

        // Wait for the receiver's connection
        new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(port)) {
                System.out.println("Server is listening on port " + port);

                Socket socket = serverSocket.accept(); // Block until receiver connects
                System.out.println("Receiver connected from " + socket.getInetAddress().getHostAddress());

                Platform.runLater(() -> statusLabel.setText("Receiver connected. Sending file..."));

                try (FileInputStream fileInputStream = new FileInputStream(file);
                        BufferedOutputStream outputStream = new BufferedOutputStream(socket.getOutputStream());
                        DataOutputStream dataOutputStream = new DataOutputStream(outputStream)) {

                    // Send file name and length
                    dataOutputStream.writeUTF(file.getName());
                    dataOutputStream.writeLong(file.length());

                    // Send the file data
                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }

                    Platform.runLater(() -> statusLabel.setText("File sent successfully!"));

                    Platform.runLater(() -> {
                        History history = new History(file.getName(), file.getPath(), "Sent",
                                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                        HistoryManager.saveHistory(history);
                        statusLabel.setText("File sent successfully!");
                        historyListView.getItems().add(history.toString());
                    });
                } catch (IOException ioException) {
                    Platform.runLater(() -> statusLabel.setText("Error sending file: " + ioException.getMessage()));
                }
            } catch (IOException ioException) {
                Platform.runLater(() -> statusLabel.setText("Error setting up server: " + ioException.getMessage()));
            }
        }).start();
    }

    public void receive(ActionEvent e) {
        Platform.runLater(() -> {
            // Prompt the user to select the save directory
            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setTitle("Select Save Location");
            Stage stage = (Stage) receiveButton.getScene().getWindow();
            File selectedDirectory = directoryChooser.showDialog(stage);

            if (selectedDirectory == null) {
                Platform.runLater(() -> statusLabel.setText("No save location selected."));
                return;
            }

            // Prompt the user to enter the sender's IP address
            TextInputDialog ipDialog = new TextInputDialog();
            ipDialog.setTitle("Enter Sender's IP");
            ipDialog.setHeaderText("Connection Setup");
            ipDialog.setContentText("Please enter the sender's IP address:");

            ipDialog.showAndWait().ifPresent(senderIp -> {
                new Thread(() -> {
                    try (Socket socket = new Socket(senderIp, port)) {
                        Platform.runLater(() -> statusLabel.setText("Connected to sender. Receiving file..."));

                        try (DataInputStream inputStream = new DataInputStream(socket.getInputStream())) {
                            // Read the file name
                            String fileName = inputStream.readUTF();

                            // Prepare the full path to save the file
                            File outputFile = new File(selectedDirectory, fileName);

                            try (BufferedOutputStream fileOutputStream = new BufferedOutputStream(
                                    new FileOutputStream(outputFile))) {

                                long fileSize = inputStream.readLong();
                                byte[] buffer = new byte[4096];
                                int bytesRead;
                                long totalRead = 0;

                                while (totalRead < fileSize && (bytesRead = inputStream.read(buffer)) != -1) {
                                    fileOutputStream.write(buffer, 0, bytesRead);
                                    totalRead += bytesRead;
                                }

                                Platform.runLater(() -> statusLabel
                                        .setText("File received successfully! Saved to: " + outputFile.getPath()));
                                Platform.runLater(() -> {
                                    History history = new History(fileName, outputFile.getPath(), "Received",
                                            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                                    HistoryManager.saveHistory(history);
                                    historyListView.getItems().add(history.toString());
                                });
                            }
                        } catch (IOException ex) {
                            Platform.runLater(() -> statusLabel.setText("Error receiving file: " + ex.getMessage()));
                        }
                    } catch (IOException ex) {
                        Platform.runLater(() -> statusLabel.setText("Could not connect to sender: " + ex.getMessage()));
                    }
                }).start();
            });
        });
    }

}
