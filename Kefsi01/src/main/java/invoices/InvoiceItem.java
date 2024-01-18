package invoices;

import almacen.Item;

public class InvoiceItem {
    private String barcode;
    private String name;
    private int quantity;
    private double price;

    

	public InvoiceItem(String barcode, String name, int quantity, double price) {
		super();
		this.barcode = barcode;
		this.name = name;
		this.quantity = quantity;
		this.price = price;
	}



	public String getBarcode() {
		return barcode;
	}



	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}



	public String getName() {
		return name;
	}



	public void setName(String name) {
		this.name = name;
	}



	public int getQuantity() {
		return quantity;
	}



	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}



	public double getPrice() {
		return price;
	}



	public void setPrice(double price) {
		this.price = price;
	}



}
