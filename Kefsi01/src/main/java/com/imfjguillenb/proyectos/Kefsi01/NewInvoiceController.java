package com.imfjguillenb.proyectos.Kefsi01;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import almacen.Item;
import database.DataBaseHandler;
import invoices.Invoice;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

public class NewInvoiceController {
	
	@FXML private TextField invoiceDateField;
	@FXML private TextField itemSearchField;
	@FXML private ListView<Item> searchResultsListView;
    @FXML private ListView<Item> selectedItemsListView;
    private Consumer<Invoice> onNewInvoiceAdded;
    
    private ObservableList<Item> allItems;
    private ObservableList<Item> selectedItems = FXCollections.observableArrayList();

    private DataBaseHandler dbHandler;
    
  

    @FXML
    public void initialize() {
    	dbHandler = new DataBaseHandler();
        allItems = dbHandler.getItems();
        searchResultsListView.setItems(allItems); // Muestra todos los items al inicio
        
        // Listener para el campo de búsqueda
        itemSearchField.textProperty().addListener((observable, oldValue, newValue) -> {
            searchItem(newValue);
        });

        // Configurar la fecha actual automáticamente
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        invoiceDateField.setText(today.format(formatter));

        // Configurar ListView para los ítems seleccionados
        selectedItemsListView.setItems(selectedItems);
    }
    private void searchItem(String searchText) {
        if (searchText == null || searchText.isEmpty()) {
            searchResultsListView.setItems(allItems); // Muestra todos los items si no hay búsqueda
            return;
        }

        ObservableList<Item> filteredItems = allItems.stream()
            .filter(item -> item.getName().toLowerCase().contains(searchText.toLowerCase()) ||
                            item.getBarcode().equals(searchText))
            .collect(Collectors.toCollection(FXCollections::observableArrayList));

        searchResultsListView.setItems(filteredItems);
    }
    
  
    
   /*nos encontramos el siguiente problema Cannot make a static reference to the non-static method addInvoice(Invoice) from the type InvoiceController, 
   * buscando he encontrado el metodo de "callback", así que lo he implementado.
   */
    
    public void setOnNewInvoiceAdded(@SuppressWarnings("exports") Consumer<Invoice> callback) {
        this.onNewInvoiceAdded = callback;
    }
    
    @FXML
    private void addItemToInvoice() {
        Item selectedItem = searchResultsListView.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            int quantity = 1; 
            Item itemForInvoice = new Item(selectedItem.getBarcode(), selectedItem.getName(), quantity, selectedItem.getPrice());
            selectedItems.add(itemForInvoice);
        }
    }
    
    
    @FXML
    private void saveInvoice() {
        String date = invoiceDateField.getText();
        Invoice newInvoice = new Invoice(0, date); // ID 0 como placeholder

        for (Item item : selectedItems) {
            newInvoice.addItem(item);

            // Obtener la cantidad actual del item en el almacen
            int currentUnitsInStorage = dbHandler.getItemUnits(item.getBarcode());
            int unitsToDeduct = item.getUnits(); // La cantidad a disminuir en el almacen

            // Calcular las nuevas unidades y actualizar en la base de datos
            int newUnits = currentUnitsInStorage - unitsToDeduct;
            if (newUnits <= 0) {
                // Manejar el caso donde se intenta vender más de lo disponible
            	showAlert("Sin stock", "No hay suficientes unidades en el almacén para el ítem: " + item.getName());
                System.out.println("No hay suficientes unidades en el almacén para el ítem: " + item.getName());
            }
            dbHandler.updateItemUnits(item.getBarcode(), newUnits);
        }

        //dbHandler.addInvoice(newInvoice);

        if (onNewInvoiceAdded != null) {
            onNewInvoiceAdded.accept(newInvoice);
            
        }
    }
    
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }   

}
