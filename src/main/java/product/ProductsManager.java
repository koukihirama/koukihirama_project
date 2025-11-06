package product;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ProductsManager implements Searchable { 
    private final List<Product> products;

    public ProductsManager() {
        this.products = new ArrayList<>();
    }

    public ProductsManager(List<Product> initialProducts) {
        this.products = new ArrayList<>(initialProducts);
    }

    public void addProduct(Product p) {
        products.add(p);
    }

    public boolean removeProduct(int id) {
        return products.removeIf(p -> p.getId() == id);
    }

    public Product getProductByName(String name) {
        if (name == null || name.isBlank()) return null;
        String key = name.trim().toLowerCase();
        for (Product p : products) {
            String n = p.getName();
            if (n != null && n.trim().toLowerCase().equals(key)) { 
                return p;
            }
        }
        return null;
    }

    public List<Product> getAll() {
        return Collections.unmodifiableList(products);
    }

    @Override
    public List<Product> search(String keyword) { 
        List<Product> hits = new ArrayList<>();
        if (keyword == null || keyword.isBlank()) {
            return hits; 
        }
        String key = keyword.toLowerCase(); 
        for (Product p : products) {
            String name = p.getName();
            if (name != null && name.toLowerCase().contains(key)) { 
                hits.add(p);
            }
        }
        return hits;
    }
}