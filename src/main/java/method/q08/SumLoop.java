package method.q08;

public class SumLoop {
	public static void main(String[] args) {
		int min = 1;
		int max = 100;

		System.out.println("最小値：" + min);
		System.out.println("最大値：" + max);

		int result = sumLoop(min, max);
		System.out.println("加算結果：" + result);
	}

	public static int sumLoop(int min, int max) {
		int sum = 0;
		for (int i = min; i <= max; i++) {
			sum += i;
		}
		return sum;
	}
}