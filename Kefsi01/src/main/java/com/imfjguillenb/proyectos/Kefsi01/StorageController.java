package com.imfjguillenb.proyectos.Kefsi01;



import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import almacen.Item;
import database.DataBaseHandler;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Dialog;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class StorageController {

    @FXML
    private TextField searchBar;
    @FXML
    private ListView<Item> itemList;
    
    private DataBaseHandler dbHandler;
   
    private ObservableList<Item> items;
    
    private FilteredList<Item> filteredItems;

    public void initialize() {
    	dbHandler = new DataBaseHandler();
    	items = dbHandler.getItems(); 
    	
    	// Crear un FilteredList envolviendo la lista observable
        filteredItems = new FilteredList<>(items, p -> true);
        
        
        itemList.setItems(filteredItems);
    	
    	
    	/*
    	// Crear algunos ítems de ejemplo
        items = FXCollections.observableArrayList(
            new Item(1, "Producto A", 10, 10.23),
            new Item(2, "Producto B", 20, 23.12),
            new Item(3, "Producto C", 30, 43.34)
        );

        itemList.setItems(items); */
        
        
        // Añadir un listener al campo de búsqueda
        searchBar.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredItems.setPredicate(item -> {
                // Si el campo de búsqueda está vacío, muestra todos los ítems
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                // Compara el nombre del ítem con el texto de búsqueda
                String lowerCaseFilter = newValue.toLowerCase();
                return item.getName().toLowerCase().contains(lowerCaseFilter);
            });
        });

        // Configurar la celda personalizada
        itemList.setCellFactory(param -> new ListCell<Item>() {
            @Override
            protected void updateItem(Item item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("ItemView.fxml"));
                        HBox hbox = loader.load();
                        ItemViewController controller = loader.getController();
                        controller.setItemDetails(item.getName(), item.getUnits(),item.getPrice(),item.getBarcode());
                        setGraphic(hbox);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        
        //Para abrir el item es clickeado dos veces
        
        itemList.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {  // Doble clic
                Item selectedItem = itemList.getSelectionModel().getSelectedItem();
                if (selectedItem != null) {
                    openItemDetailsDialog(selectedItem);
                }
            }
        });
    }
    
    
    private void openItemDetailsDialog(Item selectedItem) {
    	//Lógica para abrir la nueva pestaña
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ItemDetails.fxml"));
            VBox dialogRoot = loader.load();
            ItemDetailsController controller = loader.getController();
            controller.setDetails(selectedItem);

            //En Dialogo para que se vea mas estético
            
            Dialog<Item> dialog = new Dialog<>();
            dialog.setTitle("Detalles del Ítem");
            dialog.getDialogPane().setContent(dialogRoot);
            dialog.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
          
    
    @FXML
    private TextField nameField; // Campo de texto para el nombre del ítem
    @FXML
    private TextField unitsField; // Campo de texto para las unidades
    @FXML
    private TextField priceField; // Campo de texto para el precio
    
 // Campos de la interfaz de usuario para editar un ítem
    @FXML
    private TextField editNameField;
    @FXML
    private TextField editUnitsField;
    @FXML
    private TextField editPriceField;
    
    private Item selectedItem;


    
    @FXML
    private void openAddItemForm() {
        openItemForm(null); // null significa que estamos agregando un nuevo ítem
    }

    @FXML
    private void openEditItemForm() {
        Item selectedItem = itemList.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            openItemForm(selectedItem); // Pasamos el ítem seleccionado para editarlo
        }
    }

    private void openItemForm(Item itemToEdit) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/imfjguillenb/proyectos/Kefsi01/ItemForm.fxml")); 
            Parent root = loader.load();

            ItemFormController controller = loader.getController();
            controller.setItem(itemToEdit); // Pasa null para añadir, o un ítem para editar
            controller.setSaveHandler(this::saveItem);

            Stage stage = new Stage();
            stage.setTitle(itemToEdit == null ? "Añadir Ítem" : "Editar Ítem");
            stage.setScene(new Scene(root));
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveItem(Item item) {
        if (item == null) {
            // Manejar el caso de un ítem nulo
            return;
        }

        // Verifica si el ítem ya existe en la base de datos
        boolean itemExists = dbHandler.itemExists(item.getBarcode());

        if (itemExists) {
            // Actualizar ítem existente en la base de datos
            dbHandler.updateItem(item);

            // Buscar y actualizar el ítem en la lista observable
            for (int i = 0; i < items.size(); i++) {
                if (items.get(i).getBarcode().equals(item.getBarcode())) {
                    items.set(i, item); // Actualiza el ítem existente
                    itemList.refresh(); // Refrescar la vista del ListView si es necesario
                    return; // Salir del método después de actualizar
                }
            }
        } else {
            // Agregar nuevo ítem en la base de datos y en la lista observable
            dbHandler.addItem(item);
            items.add(item);
        }
    }

    /*@FXML
    private void addItem() {
    	
    	 String name = nameField.getText();
    	    int units;
    	    double price;

    	    try {
    	        units = Integer.parseInt(unitsField.getText());
    	        price = Double.parseDouble(priceField.getText());
    	    } catch (NumberFormatException e) {
    	        // Manejar el error si los números no son válidos
    	        return;
    	    }
    	    
    	    Item newItem = new Item(0, name, units, price); // 0 como ID, ya que la base de datos asignará el ID
    	    dbHandler.addItem(newItem); // Añadir el item a la base de datos
    	    items.add(newItem); // Añadir a la lista observable para actualizar la UI

    	    // Limpiar los campos después de añadir
    	    nameField.setText("");
    	    unitsField.setText("");
    	    priceField.setText("");
      
    } */
    
 // Método para establecer el ítem seleccionado desde la interfaz de usuario
    public void setSelectedItem(Item item) {
        this.selectedItem = item;
        editNameField.setText(item.getName());
        editUnitsField.setText(String.valueOf(item.getUnits()));
        editPriceField.setText(String.format("%.2f", item.getPrice()));
    }
    
  /* 
 // Método que se llama cuando se confirman las ediciones
    @FXML
    private void updateItem() {
        if (selectedItem == null) {
            // Manejar el caso de no tener un ítem seleccionado
            return;
        }

        String newName = editNameField.getText();
        int newUnits;
        double newPrice;

        try {
            newUnits = Integer.parseInt(editUnitsField.getText());
            newPrice = Double.parseDouble(editPriceField.getText());
        } catch (NumberFormatException e) {
            // Manejar el error si los números no son válidos
            return;
        }

        selectedItem.setName(newName);
        selectedItem.setUnits(newUnits);
        selectedItem.setPrice(newPrice);

        dbHandler.updateItem(selectedItem); // Actualizar el ítem en la base de datos

        // Actualizar la lista observable
        itemList.refresh();

        // Limpiar los campos o cerrar el diálogo de edición
    } */
    
    @FXML
    private void removeItem() {
        // para eliminar un item
    	Item selectedItem = itemList.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            // Eliminar el ítem de la base de datos
            dbHandler.deleteItem(selectedItem.getBarcode());

            // Eliminar el ítem de la lista observable
            items.remove(selectedItem);
        }
    }

    @FXML
    private void importExcel() {
    	// para importar a Excel
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(null);

        if (file != null) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                boolean firstLine = true; // Para detectar la primera línea
                while ((line = reader.readLine()) != null) {
                    if (firstLine) {
                        firstLine = false; // Omitir la primera línea (encabezados)
                        continue;
                    }
                    String[] data = line.split(",");
                    
                    Item item = new Item(data[0], data[1], Integer.parseInt(data[2]), Double.parseDouble(data[3]));
                    dbHandler.addItem(item); // Añadir a la base de datos
                    items.add(item); // Añadir a la lista observable
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void exportExcel() {
        // para exportar a Excel
    	FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialFileName("items.csv");
        File file = fileChooser.showSaveDialog(null);

        if (file != null) {
            try (PrintWriter writer = new PrintWriter(file)) {
                for (Item item : items) {
                    writer.println(item.getBarcode() + "," + item.getName() + "," + item.getUnits() + "," + item.getPrice());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
