package com.imfjguillenb.proyectos.Kefsi01;

import almacen.Item;
import invoices.Invoice;
import invoices.InvoiceItem;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

public class InvoiceDetailsController {

	@FXML
	private ListView<InvoiceItem> itemListView;
	@FXML
	private Label totalLabel;

	private Invoice currentInvoice;

	public void initialize() {
		// Configurar el formato de las celdas de la ListView
		itemListView.setCellFactory(new Callback<ListView<InvoiceItem>, ListCell<InvoiceItem>>() {
			@Override
			public ListCell<InvoiceItem> call(ListView<InvoiceItem> param) {
				return new ListCell<InvoiceItem>() {
					@Override
					protected void updateItem(InvoiceItem invoiceItem, boolean empty) {
						super.updateItem(invoiceItem, empty);
						if (invoiceItem != null && !empty) {
							setText(invoiceItem.getName() + " - " + invoiceItem.getQuantity() + " unidades - $"
									+ invoiceItem.getPrice());
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
		ObservableList<InvoiceItem> invoiceItems = FXCollections.observableArrayList(invoice.getInvoiceItems());
		itemListView.setItems(invoiceItems);

		// Mostrar el precio total almacenado en la factura
		totalLabel.setText("Total: $" + invoice.getTotal());

	}
}
