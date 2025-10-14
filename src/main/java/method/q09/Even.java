package method.q09;

public class Even {
	public static void main(String[] args) {
		int a = 5;

		if (checkEven(a)) {
			System.out.println(a + "は偶数です。");
		} else {
			System.out.println(a + "は奇数です。");
		}

	}

	public static boolean checkEven(int n) {
		if (n % 2 == 0) {
			return true;
		} else {
			return false;
		}
	}
}