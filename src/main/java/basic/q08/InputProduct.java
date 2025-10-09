package basic.q08;

import java.util.Scanner;

public class InputProduct {
	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);

		System.out.print("商品名を入力してください: ");

		String name = sc.nextLine();

		System.out.print("価格を入力してください: ");

		int price = sc.nextInt();
		System.out.println("商品名は" + name + "です。" + "価格は" + price + "円です。");

	}

}