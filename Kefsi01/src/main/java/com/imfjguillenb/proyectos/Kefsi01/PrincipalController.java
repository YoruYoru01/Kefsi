package com.imfjguillenb.proyectos.Kefsi01;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class PrincipalController {
	
	@FXML
    private VBox sideMenu;
    @FXML
    private ToggleButton toggleMenuButton;
    @FXML
    private Button storageButton;
    @FXML
    private Button invoiceButton;
    @FXML
    private StackPane contentArea;

    @FXML
    public void toggleMenu() {
        if (toggleMenuButton.isSelected()) {
            
            storageButton.setVisible(false);
            invoiceButton.setVisible(false);
        } else {
        	storageButton.setVisible(true);
            invoiceButton.setVisible(true);
        }
    }

    @FXML
    public void showStorage() throws Exception {
    	
    	// Cargar Storage.fxml
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Storage.fxml"));
        VBox newLoadedPane = loader.load();
    	
        // Cambiar el contenido a la vista de "Almacenaje"
        contentArea.getChildren().setAll(newLoadedPane);
    }

    @FXML
    public void showInvoices() throws Exception {
    	
    	// Cargar Storage.fxml
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Invoice.fxml"));
        VBox newLoadedPane = loader.load();
    	
        // Cambiar el contenido a la vista de "Facturas"
        contentArea.getChildren().setAll(newLoadedPane);
    }

}
