package almacen;

public class Item {
	
	private String barcode;
	private String name;
	private int units;
	private double price;
	
	public Item(String barcode, String name, int units, double price) {
		super();
		this.barcode = barcode;
		this.name = name;
		this.units = units;
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

	public int getUnits() {
		return units;
	}

	public void setUnits(int units) {
		this.units = units;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	

}
