package com.meetrend.haopingdian.util;

public class Base64Util {
	private static final char[] ALPHABET = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K',
			'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b',
			'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's',
			't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
			'+', '/' };

	public static String encode(String s) {
		try {
			return encode(s.getBytes("utf-8"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	public static String encode(byte[] b) {
		int n = b.length;
		StringBuilder buf = new StringBuilder(1 + n * 4 / 3);

		for (int i = 0; i < n; i += 3) {
			int count = Math.min(3, n - i);
			int data = 0;

			for (int j = 0; j < count; j++) {
				data |= (b[(i + j)] & 0xFF) << 16 - 8 * j;
			}
			for (int j = 0; j <= count; j++) {
				buf.append(ALPHABET[(data >> 18 - 6 * j & 0x3F)]);
			}
		}

		while (buf.length() % 4 != 0) {
			buf.append('=');
		}

		return buf.toString();
	}

	public static byte[] decode(String s) {
		int n = s.length();

		while ((n > 0) && (s.charAt(n - 1) == '=')) {
			n--;
		}

		byte[] b = new byte[n * 3 / 4];

		int i = 0;
		for (int k = 0; i < n; i += 4) {
			int count = Math.min(3, n - i - 1);
			int data = 0;

			for (int j = 0; j <= count; j++) {
				data |= indexOf(s.charAt(i + j)) << 18 - 6 * j;
			}
			for (int j = 0; j < count; j++) {
				b[(k++)] = (byte) (data >> 16 - 8 * j);
			}
		}

		return b;
	}

	private static int indexOf(char c) {
		if ((c >= 'A') && (c <= 'Z')) {
			return c - 'A';
		}
		if ((c >= 'a') && (c <= 'z')) {
			return c - 'a' + 26;
		}
		if ((c >= '0') && (c <= '9')) {
			return c - '0' + 52;
		}
		if (c == '+') {
			return 62;
		}
		if (c == '/') {
			return 63;
		}

		return -1;
	}
}
