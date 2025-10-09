package basic.q06;

public class ShowSeasons {
	public static void main(String[] args) {
		int month = 1;

		switch (month) {
		case 12:
		case 1:
		case 2:
			System.out.println(month + "月は冬です");
			break;
		case 3:
		case 4:
		case 5:
			System.out.println(month + "月は春です");
			break;
		case 6:
		case 7:
		case 8:
			System.out.println(month + "月は夏です");
			break;
		case 9:
		case 10:
		case 11:
			System.out.println(month + "月は秋です");
			break;
		default:
			System.out.println(month + "月は存在しません");
		}
	}
}