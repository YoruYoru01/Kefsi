package com.imfjguillenb.proyectos.Kefsi01;

import database.DataBaseHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {

    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;

    private DataBaseHandler dbHandler;

    @FXML
    public void initialize() {
        dbHandler = new DataBaseHandler();
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (dbHandler.validateLogin(username, password)) {
            // Abrir la ventana principal de la aplicación
            openMainApplicationWindow();
            // Cerrar la ventana actual
            closeLoginWindow();
        } else {
            // Mostrar mensaje de error
            showAlert("Inicio de sesión fallido", "Usuario o contraseña incorrectos.");
        }
    }

    private void openMainApplicationWindow() {
        try {
            Stage primaryStage = new Stage();
            Parent root = FXMLLoader.load(getClass().getResource("Principal.fxml"));
            primaryStage.setTitle("Aplicación Principal");
            primaryStage.setScene(new Scene(root));
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void closeLoginWindow() {
        Stage stage = (Stage) usernameField.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
