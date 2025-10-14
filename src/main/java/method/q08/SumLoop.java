package method.q08;

public class SumLoop {
	public static void main(String[] args) {
		int sum = 0;
		for (int i = 1; i <= 100; i++) {
			sum += i;
		}

		System.out.println("最小値: " + 1);
		System.out.println("最大値: " + 100);

		int result = calculateSum(sum);
		System.out.println("加算結果: " + result); // ← resultを出力
	}

	public static int calculateSum(int sum) {
		return sum;
	}
}