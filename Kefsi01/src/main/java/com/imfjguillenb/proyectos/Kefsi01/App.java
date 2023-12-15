package com.imfjguillenb.proyectos.Kefsi01;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import database.DataBaseHandler;

/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;
    private static Stage primaryStage; // Referencia al escenario principal

    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage; // Guardar el escenario principal
        scene = new Scene(loadFXML("Login"), 400, 300);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    public static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
        if("Principal".equals(fxml)) {
            primaryStage.setTitle("Kefsi App");
        }
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }

    // Método para cambiar a la vista principal
    public static void showMainView() throws IOException {
        setRoot("Principal");
    }
}