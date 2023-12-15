package com.imfjguillenb.proyectos.Kefsi01;


import java.io.IOException;

import almacen.Item;
import database.DataBaseHandler;
import invoices.Invoice;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class InvoiceController {
	
	 @FXML
	    private ListView<Invoice> invoiceListView;
	    @FXML
	    private Button addInvoiceButton;
	    @FXML
	    private Button deleteInvoiceButton;
	    @FXML
	    private TextField searchBar;
	    
	    private ObservableList<Invoice> invoices;
	    
	    private DataBaseHandler dbHandler;

	    @FXML
	    public void initialize() {
	    	
	    	
	    	//cargar las facturas desde bd
	    	
	    	dbHandler = new DataBaseHandler();
	        invoices = dbHandler.getInvoices();
	        invoiceListView.setItems(invoices);
	    	
	        /*
	        // Cargar las facturas en la ListView
	    	invoices = FXCollections.observableArrayList();
	        invoiceListView.setItems(invoices);
	        
	    	// Crear ítems de ejemplo
	        Item item1 = new Item(4, "Producto D", 2, 15.0);
	        Item item2 = new Item(5, "Producto E", 1, 20.0);
	        Item item3 = new Item(6, "Producto F", 3, 10.0); 

	        // Crear una factura de ejemplo y añadir ítems
	        Invoice invoiceExample = new Invoice(1);
	        invoiceExample.addItem(item1);
	        invoiceExample.addItem(item2);
	        invoiceExample.addItem(item3); 

	        // Añadir la factura de ejemplo a la lista observable
	        invoices.add(invoiceExample); */
	        
	        //Darle el formato para que muestre el contenido de nuestro objeto
	    	
	        invoiceListView.setCellFactory(param -> new ListCell<Invoice>() {
	            @Override
	            protected void updateItem(Invoice invoice, boolean empty) {
	                super.updateItem(invoice, empty);

	                if (empty || invoice == null) {
	                    setText(null);
	                } else {
	                    setText("Factura ID: " + invoice.getId() + ", Fecha: " + invoice.getDate());
	                }
	            }
	        });
	        
	        //Lógica para abrir nueva pestaña
	        
	        invoiceListView.setOnMouseClicked(event -> {
	            if (event.getClickCount() == 2 && !invoiceListView.getSelectionModel().isEmpty()) {
	                Invoice selectedInvoice = invoiceListView.getSelectionModel().getSelectedItem();
	                openInvoiceDetails(selectedInvoice);
	            }
	        });
	        
	    }
	    
	    private void openInvoiceDetails(Invoice selectedInvoice) {
	        try {
	            // Cargar el FXML para los detalles de la factura
	            FXMLLoader loader = new FXMLLoader(getClass().getResource("InvoiceDetails.fxml"));
	            Parent root = loader.load();

	            // Obtener el controlador y establecer la factura seleccionada
	            InvoiceDetailsController invoiceDetailsController = loader.getController();
	            invoiceDetailsController.setInvoice(selectedInvoice);

	            // Mostrar en una nueva ventana
	            Stage stage = new Stage();
	            stage.setTitle("Detalles de la Factura");
	            stage.setScene(new Scene(root));
	            stage.show();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }
	    
	    @FXML
	    private void openNewInvoiceWindow() {
	    	
	    	//Este nos abrirá para crear nuevas facturas
	    	try {
	            FXMLLoader loader = new FXMLLoader(getClass().getResource("NewInvoice.fxml"));
	            Parent root = loader.load();

	            NewInvoiceController newInvoiceController = loader.getController();
	            newInvoiceController.setOnNewInvoiceAdded(this::addInvoice);

	            Stage stage = new Stage();
	            stage.setTitle("Nueva Factura");
	            stage.setScene(new Scene(root));
	            stage.show();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }

	    @FXML
	    public void addInvoice(Invoice invoice) {
	        if (invoice != null) {
	        	dbHandler.addInvoice(invoice);
	            invoices.add(invoice);
	        }
	    }

	    @FXML
	    private void deleteInvoice() {
	        Invoice selectedInvoice = invoiceListView.getSelectionModel().getSelectedItem();
	        if (selectedInvoice != null) {
	            dbHandler.deleteInvoice(selectedInvoice.getId());
	            invoices.remove(selectedInvoice);
	        }
	    }

}
