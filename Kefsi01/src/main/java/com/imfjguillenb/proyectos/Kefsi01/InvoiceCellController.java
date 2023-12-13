package com.imfjguillenb.proyectos.Kefsi01;

import invoices.Invoice;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class InvoiceCellController {

    @FXML
    private Label facturaNumberLabel;
    @FXML
    private Label facturaDateLabel;

    public void setFactura(Invoice invoice) {
        facturaNumberLabel.setText("Factura No: " + invoice.getId());
        facturaDateLabel.setText("Fecha: " + invoice.getDate().toString());
    }
}
