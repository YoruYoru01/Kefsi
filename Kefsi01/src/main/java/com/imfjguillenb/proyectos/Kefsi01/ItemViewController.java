package com.imfjguillenb.proyectos.Kefsi01;





import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class ItemViewController {

    @FXML
    private Label itemName;
    @FXML
    private Label itemUnits;
    
    @FXML
    private Label itemPrice;

    private String itemBarcode;  
    

    public void initialize() {
    	
    	
       
    }
    
    

    public void setItemDetails(String name, int units, double price, String barcode) {
        itemName.setText(name);
        itemUnits.setText(String.valueOf(units));
        itemPrice.setText(String.valueOf(price));
        this.itemBarcode = barcode;
    }

 
}
