// このクラスは「db」パッケージに所属する
package db;

import java.sql.Connection;          // DB接続そのものを表すクラス
import java.sql.DriverManager;        // 接続(URL/USER/PASS)からConnectionを作るユーティリティ
import java.sql.PreparedStatement;    // プレースホルダ(?)付きの安全なSQLを表す
import java.sql.ResultSet;            // SELECTの結果を1行ずつ読む入れ物
import java.sql.SQLException;         // JDBCで発生する例外の型
import java.sql.Types;                // setNullで使うSQL型定数
import java.util.Scanner;             // コンソール入力用

public class ProductDB {

    // ====== 接続設定 ======
    private static final String URL =
        // 接続先のJDBC URL（DB: product_management / 文字コードやタイムゾーンを指定）
        "jdbc:mysql://127.0.0.1:3306/product_management?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Tokyo";
    private static final String USER = "java"; // DBログインユーザ
    private static final String PASS = "pass"; // そのパスワード

    // 接続を取得する共通メソッド（毎回これを呼ぶと新しいConnectionが返る）
    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }

    // ====== 1) 接続テスト ======
    public static void testConnection() {
        // try-with-resources：処理が終わると自動的にcon.close()してくれる
        try (Connection con = getConnection()) {
            System.out.println("DB接続成功"); // 成功メッセージ
        } catch (SQLException e) {
            System.out.println("DB接続失敗"); // 失敗メッセージ
            e.printStackTrace();               // 失敗の詳細を表示（原因の追跡用）
        }
    }

    // ====== 2) 一覧表示 ======
    public static void printAllProducts() {
        System.out.println("ーーproductsテーブルの全ての商品情報を表示ーー");

        // 取得したい列を明示したSELECT（並びはid昇順）
        final String sql =
            "SELECT id, name, price, stock, category_id FROM products ORDER BY id";

        // Connection → PreparedStatement → 実行 → ResultSet の基本パターン
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            boolean any = false; // 1件でもデータが来たかどうかのフラグ
            while (rs.next()) {  // 次の行があればtrue
                any = true;      // 1行目が来た時点でtrueにする

                int id = rs.getInt("id");                 // id列（int）
                String name = rs.getString("name");       // name列（文字列）
                int price = rs.getInt("price");           // price列（int）
                int stock = rs.getInt("stock");           // stock列（int）
                Object catObj = rs.getObject("category_id"); // category_idはnullの可能性
                String catStr = (catObj == null) ? "null" : String.valueOf(catObj);

                System.out.println();                     // 見やすさの空行
                System.out.println("id: " + id);
                System.out.println("name: " + name);
                System.out.println("price: " + price);
                System.out.println("stock: " + stock);
                System.out.println("category_id: " + catStr);
            }
            if (!any) System.out.println("データがありません。"); // 0件時の表示

        } catch (SQLException e) {
            System.out.println("データ取得中にエラーが発生しました。"); // 失敗時メッセ
            e.printStackTrace(); // 詳細
        }
    }

    // ====== 3) 追加 ======
    public static int insertProduct(String name, int price, int stock, Integer categoryId) {
        // idはAUTO_INCREMENTなのでINSERT対象に含めない
        final String sql =
            "INSERT INTO products (name, price, stock, category_id) VALUES (?, ?, ?, ?)";

        // 実行して影響行数(通常1)を返す
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, name);           // 1番目の? にname
            ps.setInt(2, price);             // 2番目の? にprice
            ps.setInt(3, stock);             // 3番目の? にstock
            if (categoryId == null) {
                ps.setNull(4, Types.INTEGER);// nullは型を明示してsetNull
            } else {
                ps.setInt(4, categoryId);    // 4番目の? にcategoryId
            }

            return ps.executeUpdate();       // INSERTの影響行数（成功なら1）
        } catch (SQLException e) {
            System.out.println("データ追加中にエラーが発生しました。");
            e.printStackTrace();
            return 0;                        // 失敗時は0を返す
        }
    }

    // ====== 4) 更新（価格・在庫） ======
    public static int updatePriceAndStock(int id, int newPrice, int newStock) {
        // 指定idの行のpriceとstockを上書き
        final String sql = "UPDATE products SET price = ?, stock = ? WHERE id = ?";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, newPrice); // 1番目: 新しい価格
            ps.setInt(2, newStock); // 2番目: 新しい在庫
            ps.setInt(3, id);       // 3番目: 対象のid

            return ps.executeUpdate(); // ヒット件数（0なら該当なし）
        } catch (SQLException e) {
            System.out.println("データ更新中にエラーが発生しました。");
            e.printStackTrace();
            return 0;
        }
    }

    // ====== 5) 削除（カテゴリIDで一括） ======
    public static int deleteByCategoryId(int categoryId) {
        final String sql = "DELETE FROM products WHERE category_id = ?";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, categoryId); // 対象カテゴリIDを?に設定
            return ps.executeUpdate(); // 削除件数を返す
        } catch (SQLException e) {
            System.out.println("データ削除中にエラーが発生しました。");
            e.printStackTrace();
            return 0;
        }
    }

    // ====== 6) 入力ユーティリティ ======
    // 数値を安全に読む（数字でない場合は再入力を促す）
    private static int readInt(Scanner sc, String prompt) {
        while (true) {
            System.out.println(prompt);    // 案内文表示
            String s = sc.nextLine().trim(); // 入力を読み、前後の空白を除去
            try {
                return Integer.parseInt(s); // 数字に変換して返す
            } catch (NumberFormatException e) {
                System.out.println("数値で入力してね。"); // エラーメッセージ
            }
        }
    }

    // 空欄ならnull、数字ならIntegerにして返す（再入力もサポート）
    private static Integer readNullableInt(Scanner sc, String prompt) {
        System.out.println(prompt);            // 案内文表示
        String s = sc.nextLine().trim();      // 入力取得
        if (s.isEmpty()) return null;         // 何も入ってなければnull
        try {
            return Integer.valueOf(s);        // 数字ならIntegerで返す
        } catch (NumberFormatException e) {
            System.out.println("数値で入力してね（空欄ならnull）。もう一度。");
            return readNullableInt(sc, prompt); // 再帰的に再入力
        }
    }

    // ただの1行入力（そのまま返す）
    private static String readLine(Scanner sc, String prompt) {
        System.out.println(prompt);
        return sc.nextLine();
    }

    // ====== 7) メニュー（スクショの文言に合わせて表示） ======
    public static void main(String[] args) {
        // 明示ドライバロード（新しめの環境は省略可だが、互換のため残す）
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); // ドライバクラスをロード
        } catch (ClassNotFoundException e) {
            // クラスパスにJARが無いとここで落ちる
            throw new RuntimeException("MySQL JDBC Driver not found on classpath", e);
        }

        testConnection(); // まず接続だけ確認

        // メニュー開始（Scannerはtry-with-resourcesで自動クローズ）
        try (Scanner sc = new Scanner(System.in)) {
            while (true) { // 永久ループ（0でreturnして終了）
                System.out.println();
                System.out.println("1) 追加   2) 更新   3) 削除(カテゴリ)   4) 一覧   0) 終了");
                System.out.print("> ");              // 入力プロンプト
                String sel = sc.nextLine().trim();   // 入力を読み取り

                switch (sel) { // 選択肢で分岐
                    case "1": { // 追加
                        System.out.println("ーー商品の登録ーー");
                        String name  = readLine(sc, "商品名を入力してください：");
                        int price    = readInt(sc, "価格を入力してください：");
                        int stock    = readInt(sc, "在庫数を入力してください：");
                        Integer cat  = readNullableInt(sc, "カテゴリーIDを入力してください：");

                        int n = insertProduct(name, price, stock, cat); // 追加実行

                        System.out.println();
                        System.out.println("登録成功件数： " + n + "件");
                        if (n > 0) {
                            System.out.println("登録内容：");
                            System.out.println("商品名： " + name + "、 価格： " + price + "、 在庫数： " + stock
                                    + "、 カテゴリーID： " + (cat == null ? "null" : cat));
                        } else {
                            System.out.println("登録失敗");
                        }
                        break;
                    }

                    case "2": { // 更新
                        System.out.println("ーー商品の価格と在庫を更新ーー");
                        int id    = readInt(sc, "商品IDを入力してください：");
                        int price = readInt(sc, "価格を入力してください：");
                        int stock = readInt(sc, "在庫数を入力してください：");

                        int n = updatePriceAndStock(id, price, stock); // 更新実行

                        System.out.println();
                        System.out.println("更新成功件数： " + n + "件");
                        if (n > 0) {
                            System.out.println("更新内容：");
                            System.out.println("商品ID: " + id + "、 価格： " + price + "、 在庫数： " + stock);
                        } else {
                            System.out.println("更新失敗");
                        }
                        break;
                    }

                    case "3": { // 削除（カテゴリID）
                        System.out.println("ーーカテゴリーIDで商品を削除ーー");
                        int cid = readInt(sc, "削除するカテゴリーIDを入力してください：");
                        int n = deleteByCategoryId(cid); // 削除実行

                        System.out.println();
                        System.out.println("削除成功件数： " + n + "件");
                        if (n == 0) System.out.println("削除対象がありませんでした");
                        break;
                    }

                    case "4": // 一覧
                        printAllProducts(); // 表示
                        break;

                    case "0": // 終了
                        return; // mainを抜ける＝アプリ終了

                    default: // 想定外入力
                        System.out.println("もう一回選んでください"); // 再入力促し
                }
            }
        }
    }
}