package product;

public class Main {
	public static void main(String[] args) {
		ProductsManager mgr = new ProductsManager();

		mgr.addProduct(new Product(1, "冷蔵庫", 50000, 10));
		mgr.addProduct(new Product(2, "ソファ", 30000, 5));
		mgr.addProduct(new Product(3, "米", 2000, 3));
		mgr.addProduct(new Product(4, "小説", 1500, 4));
		mgr.addProduct(new Product(5, "Tシャツ", 1500, 5));

		System.out.println("----商品を5つ追加して全てを表示する----");
		printAll(mgr);

		mgr.removeProduct(1);

		System.out.println("\n----商品を1つ削除して全てを表示する----");
		printAll(mgr);

		System.out.println("\n----商品名「米」の情報を表示する----");
		Product found = mgr.getProductByName("米");
		if (found != null) {
			System.out.println(found);
		}
	}

	private static void printAll(ProductsManager mgr) {
		for (Product p : mgr.getAll()) {
			System.out.println(p);
		}
	}
}