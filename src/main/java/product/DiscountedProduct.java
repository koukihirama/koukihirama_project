package product;

public class DiscountedProduct extends Product {
    private double discountRate;

    public DiscountedProduct(int id, String name, int price, int stock, double discountRate) {
        super(id, name, price, stock);
        this.discountRate = discountRate;
    }

    public double calculateDiscountedPrice() {
        return getPrice() * (1 - discountRate);
    }
}