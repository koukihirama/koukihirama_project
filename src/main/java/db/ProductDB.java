package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ProductDB {

    // ==== 接続設定（あなたの環境に合わせて書き換え）====
    // 例: DB名=java_basic, ユーザー=root, パスワード=pass
	private static final String URL =
		    "jdbc:mysql://127.0.0.1:3306/java_basic?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Tokyo";
		private static final String USER = "java";
		private static final String PASS = "pass";

    // JDBC 4.0 以降はドライバの Class.forName は基本不要だが、古い教材の場合は↓を有効化してOK
    // static {
    //     try { Class.forName("com.mysql.cj.jdbc.Driver"); }
    //     catch (ClassNotFoundException e) { throw new RuntimeException(e); }
    // }

    /** パート1：DB接続確認（成功/失敗メッセージ＋適切な例外処理） */
    public static void testConnection() {
        try (Connection con = DriverManager.getConnection(URL, USER, PASS)) {
            System.out.println("DB接続成功");
        } catch (SQLException e) {
            System.out.println("DB接続失敗");
            e.printStackTrace();
        }
    }

    /** パート2：全件取得してコンソール表示（products テーブル前提） */
    public static void printAllProducts() {
        System.out.println("ーーproductsテーブルの全ての商品情報を表示ーー");

        final String sql =
            "SELECT id, name, price, stock, category_id FROM products ORDER BY id";

        try (Connection con = DriverManager.getConnection(URL, USER, PASS);
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            boolean any = false;
            while (rs.next()) {
                any = true;

                int id      = rs.getInt("id");
                String name = rs.getString("name");
                int price   = rs.getInt("price");
                int stock   = rs.getInt("stock");

                // NULL対応：category_id が NULL なら "null" と表示
                Object catObj = rs.getObject("category_id"); // Integer or null
                String catStr = (catObj == null) ? "null" : String.valueOf(catObj);

                System.out.println(); // レコード間の空行
                System.out.println("id: " + id);
                System.out.println("name: " + name);
                System.out.println("price: " + price);
                System.out.println("stock: " + stock);
                System.out.println("category_id: " + catStr);
            }

            if (!any) {
                System.out.println("データがありません。");
            }

        } catch (SQLException e) {
            System.out.println("データ取得中にエラーが発生しました。");
            e.printStackTrace();
        }
    }


    /** 単体で動かしたい場合のエントリ（任意） */
    public static void main(String[] args) {
        testConnection();     // 「DB接続成功 / 失敗」を確認
        printAllProducts();   // 全件をコンソールに出す
    }
    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); // Connector/J 8/9 共通
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("MySQL JDBC Driver not found on classpath", e);
        }
    }
}