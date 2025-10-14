package product;

import java.util.ArrayList;
import java.util.List;

public class ProductsManager {
    private final List<Product> products = new ArrayList<>();

    public void addProduct(Product p) { products.add(p); }

    public boolean removeProduct(int id) {
        return products.removeIf(p -> p.getId() == id);
    }

    public Product getProductByName(String name) {
        for (Product p : products) if (p.getName().equals(name)) return p;
        return null;
    }

    public List<Product> getAll() { return products; }
}