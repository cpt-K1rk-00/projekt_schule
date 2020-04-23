package client;

public class Vigenere {

	public static String vigenere(String pText, String pSchluessel) {
		String ausgabe = "";
		char zeichen;
		int zahlenwert;
		char schluesselzeichen;
		int schluesselwert;

		int keyAt = 0;
		for (int i = 0; i < pText.length(); i++) {
			zeichen = pText.charAt(i);
			if (zeichen != ' ') {
				zahlenwert = (int) zeichen;

				schluesselzeichen = pSchluessel.charAt(keyAt);
				schluesselwert = (int) schluesselzeichen - 97;

				zahlenwert = zahlenwert + schluesselwert;
				if (zahlenwert > 122)
					zahlenwert = zahlenwert - 26;

				ausgabe += (char) zahlenwert;

				keyAt = (keyAt + 1) % pSchluessel.length();
			} else {
				ausgabe += zeichen;
			}
		}

		return ausgabe;
	}

	public static String decode(String text, String key) {
		String ausgabe = "";
		char zeichen;
		int zahlenwert;
		char schluesselzeichen;
		int schluesselwert;

		int keyAt = 0;
		for (int i = 0; i < text.length(); i++) {
			zeichen = text.charAt(i);
			if (zeichen != ' ') {
				zahlenwert = (int) zeichen;

				schluesselzeichen = key.charAt(keyAt);
				schluesselwert = (int) schluesselzeichen - 97;

				zahlenwert = zahlenwert - schluesselwert;
				if (zahlenwert < 97)
					zahlenwert = zahlenwert + 26;

				ausgabe += (char) zahlenwert;

				keyAt = (keyAt + 1) % key.length();
			}else {
				ausgabe += zeichen;
			}
		}
		return ausgabe;
	}

	public static void main(String[] args) {
		String tmp = "hello world";
		String t = vigenere(tmp, "cola");
		String tm = decode(t, "cola");
		System.out.println(tmp);
		System.out.println(t);
		System.out.println(tm);
	}

}
