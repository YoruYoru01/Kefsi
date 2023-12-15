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

	public Invoice(int id, String date) {
		super();
		this.id = id;
		this.date = date;
		this.items = new ArrayList<>();
		this.total = 0.0;
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

	public double getTotal() {
        BigDecimal total = BigDecimal.ZERO;
        for (Item item : items) {
            BigDecimal price = BigDecimal.valueOf(item.getPrice());
            BigDecimal quantity = BigDecimal.valueOf(item.getUnits());
            total = total.add(price.multiply(quantity));
        }
        return total.setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

	public void setTotal(double total) {
		this.total = total;
	}

	/*public void addItem(Item item) {
		items.add(item);
		total += item.getPrice() * item.getUnits();

	} */
	
	 public void addItem(Item item) {
	        if (item != null) {
	            this.items.add(item);
	            updateTotal();
	        }
	    }

	    public void addItems(ObservableList<Item> items) {
	        if (items != null) {
	            this.items.addAll(items);
	            updateTotal();
	        }
	    }

	    private void updateTotal() {
	        total = 0.0;
	        for (Item item : this.items) {
	            total += item.getPrice() * item.getUnits();
	        }
	    }
    
    
}