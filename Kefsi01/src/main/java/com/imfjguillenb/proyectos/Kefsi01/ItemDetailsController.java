package com.imfjguillenb.proyectos.Kefsi01;


import almacen.Item;
import javafx.fxml.FXML;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ItemDetailsController {
    @FXML
    private Label idLabel;
    @FXML
    private Label nameLabel;
    @FXML
    private Label unitsLabel;
    @FXML
    private Label priceLabel;
    @FXML
    private VBox vbox;
    

    public void setDetails(Item item) {
        idLabel.setText("ID: " + item.getBarcode());
        nameLabel.setText("Nombre: " + item.getName());
        unitsLabel.setText("Unidades: " + item.getUnits());
    }

    @FXML
    public void closeDialog() {
        // Código para cerrar el diálogo
    	Stage stage = (Stage) vbox.getScene().getWindow();
        stage.close();
    }
}
