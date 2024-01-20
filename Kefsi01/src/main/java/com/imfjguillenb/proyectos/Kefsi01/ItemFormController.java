package com.imfjguillenb.proyectos.Kefsi01;

import java.util.function.Consumer;

import almacen.Item;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class ItemFormController {
	
	@FXML
    private TextField barcodeField;

    @FXML
    private TextField nameField;
    @FXML
    private TextField unitsField;
    @FXML
    private TextField priceField;

    private Item currentItem;
    private Consumer<Item> saveHandler;

    public void initialize() {
        
    }

    public void setItem(Item item) {
    	
        this.currentItem = item;
        if (item != null) {
        	barcodeField.setText(item.getBarcode());
            nameField.setText(item.getName());
            unitsField.setText(String.valueOf(item.getUnits()));
            priceField.setText(String.valueOf(item.getPrice()));
            
            barcodeField.setDisable(true);
        } else {
        	
        	barcodeField.setDisable(false);
        }
    }

    public void setSaveHandler(Consumer<Item> saveHandler) {
        this.saveHandler = saveHandler;
    }

    @FXML
    private void saveItem() {
    	
    	try {

            // Lógica para guardar el ítem...
    		
            String barcode = barcodeField.getText();
            String name = nameField.getText();
            int units = Integer.parseInt(unitsField.getText());
            double price = Double.parseDouble(priceField.getText());
            
            //verifiaciones pertinentes
            
            // Verificar si el barcode está vacío
            
            if (barcode == null || barcode.trim().isEmpty()) {
                showAlert("Dato Faltante", "El código de barras no puede estar vacío.");
                return;
            }

            // Verificar si el nombre está vacío
            
            if (name == null || name.trim().isEmpty()) {
                showAlert("Dato Faltante", "El nombre del producto no puede estar vacío.");
                return;
            }

            // Verificar si las unidades o el precio son valores negativos o cero
            if (units < 0) {
                showAlert("Valor Inválido", "Las unidades deben ser un número positivo.");
                return;
            }

            if (price < 0) {
                showAlert("Valor Inválido", "El precio no puede ser negativo.");
                return;
            }
            
            //continuamos la creación o actualización del item

            if (currentItem == null) {
                currentItem = new Item(barcode, name, units, price);
            } else {
            	currentItem.setBarcode(barcode);
                currentItem.setName(name);
                currentItem.setUnits(units);
                currentItem.setPrice(price);
            }

            if (saveHandler != null) {
                saveHandler.accept(currentItem);
            }

            closeWindow();

        } catch (NumberFormatException e) {
            // Mostrar mensaje de error si la conversión de número falla
            showAlert("Datos Inválidos", "Por favor, verifique los datos introducidos y vuelva a intentarlo.");
        }
    	
    	
    }

    private void showAlert(String title, String content) {
    	//Lógica para abrir la alerta de error.
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

	@FXML
    private void cancel() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) nameField.getScene().getWindow();
        stage.close();
    }
}
