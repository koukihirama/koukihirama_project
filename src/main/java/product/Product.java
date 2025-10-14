package product;

public class Product {
	private int id;
	private String name;
	private int price;
	private int stock;

	public Product(int id, String name, int price, int stock) {
		this.id = id;
		this.name = name;
		this.price = price;
		this.stock = stock;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public int getPrice() {
		return price;
	}

	public int getStock() {
		return stock;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	public void setStock(int stock) {
		this.stock = stock;
	}

	@Override
	public String toString() {
		return String.format("Product: id=%d, name=%s, price=%d, stock=%d",
				id, name, price, stock);
	}
}