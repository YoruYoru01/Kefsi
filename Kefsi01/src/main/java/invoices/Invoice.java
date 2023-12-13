package invoices;

import java.util.ArrayList;
import java.util.Date;

import almacen.Item;

public class Invoice {
	
	private int id;
    private ArrayList<Item> items;
    private Date date;
    private double total;
    
    
    
    public Invoice(int id) {
		super();
		this.id = id;
		this.items = new ArrayList<>();
		this.date = new Date();
		this.total = 0.0;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public ArrayList<Item> getItems() {
		return items;
	}

	public void setItems(ArrayList<Item> items) {
		this.items = items;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public double getTotal() {
		return total;
	}

	public void setTotal(double total) {
		this.total = total;
	}

	public void addItem(Item item) {
        this.items.add(item);
        recalculateTotal();
    }

    public void removeItem(Item item) {
        this.items.remove(item);
        recalculateTotal();
    }

    private void recalculateTotal() {
        this.total = 0.0;
        for (Item item : items) {
            this.total += (item.getPrice() * item.getUnits());
        }
    }

}
