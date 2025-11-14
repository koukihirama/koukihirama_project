//このクラスが product パッケージに属する宣言。
package product;

//リスト操作とコンソール入力のための標準クラスを読み込み。
import java.util.List;
import java.util.Scanner;

//エントリーポイントとなるクラス定義。
public class Main {
	//1つだけ共有する Scanner を作り、標準入力（キーボード）を読む。
    private static final Scanner sc = new Scanner(System.in);

    //Javaの実行開始地点。
    public static void main(String[] args) {
    	//商品を管理するクラスのインスタンスを用意（在庫の「倉庫」役）。
        ProductsManager mgr = new ProductsManager();

        //終了選ぶまでメニューを繰り返す無限ループ。
        while (true) {
        	//メニューを表示。
            printMenu();
            //操作番号を数値で読み取る（バリデーションつき）。
            int sel = readInt("メニューから操作を選択してください。");

            //入力番号に応じて分岐（拡張switch：-> で break不要）。
            switch (sel) {
                //0ならデモ実行（サンプル商品の追加〜検索〜割引を一気に流す）。
                //case 0 -> runDemo(mgr);                 
                //商品追加（例外を投げるバリデーション＋try-catch処理あり）。
                case 1 -> handleRegister(mgr);   
                //商品名を指定して情報を取得・表示。
                case 2 -> handleGetByName(mgr);    
                //商品名の部分一致検索を実行・表示。
                case 3 -> handleSearch(mgr);   
                //すべての商品を一覧表示。
                case 4 -> handleShowAll(mgr);  
                //ID指定で商品を削除。
                case 5 -> handleRemove(mgr); 
                //9なら終了メッセージを出して main を終わらせる（アプリ終了）。
                case 0 -> { System.out.println("終了します。"); return; }
                //それ以外の番号はエラーメッセージ。
                default -> System.out.println("不正な番号です。");
            }
            //各操作のあとに空行を入れて見やすくする。
            System.out.println();
        }//（main 終了）
    }

    //メニュー文面をまとめて出力するだけのヘルパー。
    private static void printMenu() {
        System.out.println("--商品メニュー--");
        //System.out.println("0: デモ実行（追加→削除→検索→割引表示）");
        System.out.println("1: 商品追加");
        System.out.println("2: 商品情報取得");
        System.out.println("3: 商品検索");
        System.out.println("4: 商品全て表示");
        System.out.println("5: 商品削除");
        System.out.println("0: 終了");
    }

    //指定の mgr に対して一連の動作をデモするメソッド。
    private static void runDemo(ProductsManager mgr) {

    	//サンプル商品を5件登録。
        mgr.addProduct(new Product(1, "冷蔵庫", 50000, 10));
        mgr.addProduct(new Product(2, "ソファ", 30000, 5));
        mgr.addProduct(new Product(3, "米", 2000, 3));
        mgr.addProduct(new Product(4, "小説", 1500, 4));
        mgr.addProduct(new Product(5, "Tシャツ", 1500, 5));

        //追加後の一覧を表示（同じロジックを再利用）。
        System.out.println("----商品を5つ追加して全てを表示する----");
        handleShowAll(mgr);

        //ID=1を削除して、再度一覧を表示。
        mgr.removeProduct(1);
        System.out.println("\n----商品を1つ削除して全てを表示する----");
        handleShowAll(mgr);

        //"米" を getProductByName で取得 → 三項演算子で存在チェックして表示。
        System.out.println("\n----商品名「米」の情報を表示する----");
        Product kome = mgr.getProductByName("米");
        System.out.println(kome != null ? kome : "「米」は見つかりませんでした。");

        //"ソファ" を取得できたら DiscountedProduct を作って30%引き価格を計算して整形出力。
        System.out.println("\n----商品名「ソファ」の情報と割引率30％の価格を表示する----");
        Product sofa = mgr.getProductByName("ソファ");
        if (sofa != null) {
            double rate = 0.3; 
            DiscountedProduct dp =
                new DiscountedProduct(sofa.getId(), sofa.getName(), sofa.getPrice(), sofa.getStock(), rate);
            int discounted = (int) Math.round(dp.calculateDiscountedPrice());
            System.out.printf(
                "Product: id=%d, name=%s, price=%d, stock=%d, 割引率=%.1f, 割引後価格=%d%n",
                dp.getId(), dp.getName(), dp.getPrice(), dp.getStock(), rate, discounted
            );
        }

        //"Tシャツ" を mgr.search("Tシャツ") で部分一致検索 → 見つからなければメッセージ、見つかれば1件ずつ表示。
        System.out.println("\n----商品名「Tシャツ」を検索して表示する----");
        List<Product> hits = mgr.search("Tシャツ");
        if (hits.isEmpty()) System.out.println("「Tシャツ」でヒットなし。");
        else hits.forEach(System.out::println);
    }//（runDemo 終了）

 // 1: 商品追加（スクショ準拠：IDも入力し、各入力をエコー表示）
    private static void handleRegister(ProductsManager mgr) { // 商品登録メニューの処理本体（mgrに登録していく）
        try { // ここから「普通にやってみる」箱。ダメならcatchへ
            int id      = readIntEcho("商品IDを入力してください：", "入力された商品ID："); // IDを入力→数値化→その値を即エコー表示
            String name = readLineEcho("商品名を入力してください：", "入力された商品名："); // 商品名を入力→その値を即エコー表示
            throwIf(name == null || name.isBlank(),
                "商品名", name, "無効な入力です。商品名を正しく入力してください。"); // 名前が空なら例外をスロー（後でcatch）

            int price = readIntEcho("価格を入力してください：", "入力された価格："); // 価格を入力→数値化→エコー
            throwIf(price < 0,
                "価格", String.valueOf(price), "無効な入力です。価格を正しく入力してください。"); // 価格が負なら例外をスロー

            int stock = readIntEcho("在庫数を入力してください：", "入力された在庫："); // 在庫を入力→数値化→エコー
            throwIf(stock < 0,
                "在庫数", String.valueOf(stock), "無効な入力です。在庫を正しく入力してください。"); // 在庫が負なら例外をスロー

            mgr.addProduct(new Product(id, name, price, stock)); // ここまでOKなら商品を生成して在庫リストへ追加
            System.out.printf("Product: id=%d, name=%s, price=%d, stock=%dを登録しました。%n",
                    id, name, price, stock); // 登録完了メッセージを整形表示

        } catch (IllegalArgumentException e) { // throwIf が投げた不正入力の例外をここで受け止める
            System.out.println("無効な入力です。入力された" + e.getMessage()); // 入力値のエコー部分を人間向けに表示
            Exception ex = new Exception(e.getCause() != null ? e.getCause().getMessage() : ""); // 詳細助言を新しい例外のメッセージに詰める
            ex.printStackTrace(System.out); // スタックトレースを出して「どこで何が起きたか」を一覧表示
        }
    }
    
    private static void throwIf(boolean condition, String label, String value, String advice) {
        if (!condition) return;
        // 例: label="商品名", value="" → getMessage() は "商品名："
        throw new IllegalArgumentException(label + "：" + value, new RuntimeException(advice));
    }
    
    private static String readLineEcho(String prompt, String echoLabel) {
        String v = readLine(prompt);         // 既存の readLine を利用
        System.out.println(echoLabel + v);   // 入力内容をそのまま表示
        return v;
    }

    private static int readIntEcho(String prompt, String echoLabel) {
        int v = readInt(prompt);             // 既存の readInt を利用
        System.out.println(echoLabel + v);   // 入力値をそのまま表示
        return v;
    }

    private static void handleGetByName(ProductsManager mgr) {
        String name = readLine("商品情報を取得する商品名を入力してください：");
        Product p = mgr.getProductByName(name);   // ← 完全一致で1件取得
        if (p == null) {
            System.out.println("該当する商品が見つかりません。");
        } else {
            System.out.println("取得した商品は、 " + p + " です。");
        }
    }

    private static void handleSearch(ProductsManager mgr) {
    	//キーワード取得。
        String keyword = readLine("検索する商品名を入力してください: ");
        //部分一致・大小文字無視の検索（ProductsManager#search の実装）。
        List<Product> hits = mgr.search(keyword);
        //結果が空なら「ヒットなし」、あれば forEach(System.out::println) で列挙。
        if (hits.isEmpty()) System.out.println("ヒットなし。");
        else hits.forEach(System.out::println);
    }

    private static void handleRemove(ProductsManager mgr) {
        // プロンプトは全角コロンで
        int id = readInt("削除する商品のIDを入力してください：");

        boolean removed = mgr.removeProduct(id);

        if (removed) {
            
            System.out.printf("商品IDが%dの商品を削除しました%n", id);
        } else {
            System.out.println("該当IDの商品がありません。");
        }
    }

    private static void handleShowAll(ProductsManager mgr) {
    	//1件もなければメッセージ、あれば全件 println。
        if (mgr.getAll().isEmpty()) System.out.println("商品はまだ登録されていません。");
        else mgr.getAll().forEach(System.out::println);
    }

    private static String readLine(String prompt) {
    	//プロンプトを出す。
        System.out.print(prompt);
        //1行読み、前後空白を削って返す。
        return sc.nextLine().trim();
    }

    private static int readInt(String prompt) {
    	//永遠ループで入力を受け続ける。正しい数値が来たら return で抜ける
        while (true) {
        	//プロンプトを改行なしで表示。
            System.out.print(prompt);
            try {
            	//入力を1行まるごと文字列で取得し、前後の空白を削る。
                return Integer.parseInt(sc.nextLine().trim());
            //文字→数値変換に失敗した時だけ捕まえる。
            } catch (NumberFormatException e) {  
                System.out.println("数値を入力してください。");
            }
        }
    }
}
