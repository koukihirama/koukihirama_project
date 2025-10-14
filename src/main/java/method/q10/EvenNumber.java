package method.q10;

import java.util.Arrays;

public class EvenNumber {
	public static void main(String[] args) {
		int[] numbers = { 3, 2, 5, 6, 7, 25, 10, 51, 88, 98 };

		int evenCount = getEvenNumbers(numbers);

		System.out.println(Arrays.toString(numbers) + "には、偶数が" + evenCount + "個あります。");
	}

	public static int getEvenNumbers(int[] numbers) {
		int evenCount = 0;
		for (int n : numbers) {
			if (n % 2 == 0) {
				evenCount++;
			}
		}
		return evenCount;
	}
}