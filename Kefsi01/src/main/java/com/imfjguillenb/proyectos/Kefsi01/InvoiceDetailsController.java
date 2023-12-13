package com.imfjguillenb.proyectos.Kefsi01;

import almacen.Item;
import invoices.Invoice;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

public class InvoiceDetailsController {

    @FXML
    private ListView<Item> itemListView;
    @FXML
    private Label totalLabel;
    
    private Invoice currentInvoice;
    
    
    public void initialize() {
        // Configurar el formato de las celdas de la ListView
        itemListView.setCellFactory(new Callback<ListView<Item>, ListCell<Item>>() {
            @Override
            public ListCell<Item> call(ListView<Item> param) {
                return new ListCell<Item>() {
                    @Override
                    protected void updateItem(Item item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item != null && !empty) {
                            setText(item.getName() + " - " + item.getUnits() + " unidades - $" + item.getPrice());
                        } else {
                            setText(null);
                        }
                    }
                };
            }
        });
    }
    

    public void setInvoice(Invoice invoice) {
        // Devolvera lista Items
        ObservableList<Item> items = FXCollections.observableArrayList(invoice.getItems());
        itemListView.setItems(items);

        // Para luego calcular precio total.
        totalLabel.setText("Total: $" + invoice.getTotal());
        
        
    } 
}
