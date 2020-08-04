package com.vantageclient.data;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class ByteHelper {

	public static byte[] hexToBytes(String value) {
		int len = value.length();
		byte[] data = new byte[len / 2];

		for (int i = 0; i < len; i += 2) {
			data[i / 2] = (byte) ((Character.digit(value.charAt(i), 16) << 4) + Character
					.digit(value.charAt(i + 1), 16));
		}

		return data;
	}

	public static Bitmap bytes2Bimap(byte[] b) {
		if (b.length != 0) {
			return BitmapFactory.decodeByteArray(b, 0, b.length);
		} else {
			return null;
		}
	}
}
