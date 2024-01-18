package invoices;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import almacen.Item;
import javafx.collections.ObservableList;

public class Invoice {

	private int id;
	private String date;
	private List<Item> items;
	private double total;
	
	private List<InvoiceItem> invoiceItems;

	public Invoice(int id, String date) {
		super();
		this.id = id;
		this.date = date;
		this.items = new ArrayList<>();
		this.total = 0.0;
		
		this.invoiceItems = new ArrayList<>();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public List<Item> getItems() {
		return items;
	}

	public void setItems(List<Item> items) {
		this.items = items;
	}
	
	// Actualiza el total cada vez que se llama
    public double getTotal() {
        calculateTotal();  // Calcula y actualiza el total
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public void addItem(Item item) {
        if (item != null) {
            this.items.add(item);
            calculateTotal();  
        }
    }

    public void addItems(ObservableList<Item> items) {
        if (items != null) {
            this.items.addAll(items);
            calculateTotal();  
        }
    }

    // Método para calcular el total de la factura
    private void calculateTotal() {
        BigDecimal total = BigDecimal.ZERO;
        for (InvoiceItem invoiceItem : this.invoiceItems) {
            BigDecimal price = BigDecimal.valueOf(invoiceItem.getPrice());
            BigDecimal quantity = BigDecimal.valueOf(invoiceItem.getQuantity());
            total = total.add(price.multiply(quantity));
        }
        this.total = total.setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

	
    //correlacionar las facturas con los items de facturas para que no actualice con los items de almacen.
 // Método para añadir múltiples InvoiceItems
    public void addInvoiceItem(InvoiceItem invoiceItem) {
        if (invoiceItem != null) {
            this.invoiceItems.add(invoiceItem);
        }
    }

    public List<InvoiceItem> getInvoiceItems() {
        return invoiceItems;
    }
    
}