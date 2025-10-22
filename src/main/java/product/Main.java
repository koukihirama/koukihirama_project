package product;

import java.util.List;

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

        // 1) 「米」を名前で取得して表示
        System.out.println("\n----商品名「米」の情報を表示する----");
        Product kome = mgr.getProductByName("米");
        if (kome != null) {
            System.out.println(kome);
        } else {
            System.out.println("「米」は見つかりませんでした。");
        }

        // 2) 「ソファ」を30%割引で表示（DiscountedProductを使う）
        System.out.println("\n----商品名「ソファ」の情報と割引率30％の価格を表示する----");
        Product sofa = mgr.getProductByName("ソファ");
        if (sofa != null) {
            double rate = 0.3; // 30%
            DiscountedProduct dp =
                new DiscountedProduct(sofa.getId(), sofa.getName(), sofa.getPrice(), sofa.getStock(), rate);

            int discounted = (int)Math.round(dp.calculateDiscountedPrice()); // 四捨五入で整数化

            System.out.printf(
                "Product: id=%d, name=%s, price=%d, stock=%d, 割引率=%.1f, 割引後価格=%d%n",
                dp.getId(), dp.getName(), dp.getPrice(), dp.getStock(), rate, discounted
            );
        }

        // 3) 「Tシャツ」を検索（部分一致）して表示（Searchable#search）
        System.out.println("\n----商品名「Tシャツ」を検索して表示する----");
        List<Product> hits = mgr.search("Tシャツ");
        if (hits.isEmpty()) {
            System.out.println("「Tシャツ」でヒットなし。");
        } else {
            for (Product p : hits) {
                System.out.println(p);
            }
        }
    }

    private static void printAll(ProductsManager mgr) {
        for (Product p : mgr.getAll()) {
            System.out.println(p);
        }
    }
}