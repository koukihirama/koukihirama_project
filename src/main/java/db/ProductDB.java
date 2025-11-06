// このクラスは「db」パッケージに所属する
package db;

//DB接続を表すオブジェクト（接続そのもの）
import java.sql.Connection;
//接続を作るためのユーティリティ（URL/USER/PASSでConnectionを作る）
import java.sql.DriverManager;
//事前コンパイル済みSQL
import java.sql.PreparedStatement;
//SELECT の結果を行ごとに読むための入れ物
import java.sql.ResultSet;
//JDBC 操作中の例外（接続失敗/SQLエラーなど）
import java.sql.SQLException;

// DBアクセス専用のユーティリティクラス
public class ProductDB {

	 // JDBC接続文字列。どこのDBへ繋ぐか、各種オプション（SSL無効/タイムゾーン等）もここで指定
	private static final String URL =
		    "jdbc:mysql://127.0.0.1:3306/java_basic?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Tokyo";
	    // DBにログインするユーザ名
		private static final String USER = "java";
		// そのユーザのパスワード
		private static final String PASS = "pass";

	// パート1：接続テスト用のメソッド
    public static void testConnection() {
    	// URL/USER/PASSでDBへ接続を試みる。try-with-resourcesなので自動でcloseされる
        try (Connection con = DriverManager.getConnection(URL, USER, PASS)) {
        	// 接続できたら成功メッセージ
            System.out.println("DB接続成功");
        } catch (SQLException e) {
        	// 失敗時のメッセージ
            System.out.println("DB接続失敗");
            // どこで何が失敗したか原因を出力（デバッグ用）
            e.printStackTrace();
        }
    }

    // パート2：productsを全件取得して表示するメソッド
    public static void printAllProducts() {
        System.out.println("ーーproductsテーブルの全ての商品情報を表示ーー");

        // 実行したいSQLを文字列で定義（今回は全件select、id順）
        final String sql =
            "SELECT id, name, price, stock, category_id FROM products ORDER BY id";

        // まず接続を開く（try-with-resources なので自動クローズ）
        try (Connection con = DriverManager.getConnection(URL, USER, PASS);
        	// 用意したSQLからPreparedStatementを作成（? があれば後で値をセット）
             PreparedStatement ps = con.prepareStatement(sql);
        	// SELECT を実行して結果(ResultSet)を受け取る
             ResultSet rs = ps.executeQuery()) {

        	// 1行でも読めたかどうかのフラグ（空テーブル対応）
            boolean any = false;
            // 結果セットを1行ずつ前に進める（行があればtrue）
            while (rs.next()) {
            	// 1行でも来たので true にする
                any = true;

                // カラム名で値を取り出す（int）
                int id      = rs.getInt("id");
                // 文字列で取得
                String name = rs.getString("name");
                // 価格
                int price   = rs.getInt("price");
                // 在庫
                int stock   = rs.getInt("stock");

                // category_id はNULLの可能性があるので、まず Object として受ける
                Object catObj = rs.getObject("category_id"); 
                // nullなら "null" と表示、値があれば文字列化して表示用にする
                String catStr = (catObj == null) ? "null" : String.valueOf(catObj);

                // 見やすさ用に空行
                System.out.println(); 
                // 行の内容をラベル付きで出力
                System.out.println("id: " + id);
                System.out.println("name: " + name);
                System.out.println("price: " + price);
                System.out.println("stock: " + stock);
                System.out.println("category_id: " + catStr);
            }

            // 1件もなかった場合のメッセージ
            if (!any) {
                System.out.println("データがありません。");
            }

          // SQL関連の例外（接続/実行/結果処理の失敗）
        } catch (SQLException e) {
            System.out.println("データ取得中にエラーが発生しました。");
            // 詳細原因を出力
            e.printStackTrace();
        }
    }

    // このクラス単体で実行するための入口
    public static void main(String[] args) {
    	// まず接続できるか確認
        testConnection();    
        // その後、全件表示を実行
        printAllProducts();  
    }
    // クラスロード時に一度だけ実行される初期化ブロック
    static {
        try {
        	// MySQLドライバを明示ロード
            Class.forName("com.mysql.cj.jdbc.Driver"); 
        } catch (ClassNotFoundException e) {
        	// ドライバが見つからないと何も始まらないので、即座に実行中断
            throw new RuntimeException("MySQL JDBC Driver not found on classpath", e);
        }
    }
}