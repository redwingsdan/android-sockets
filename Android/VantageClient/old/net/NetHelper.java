package com.vantageclient.net;

import java.io.IOException;
import java.io.InputStream;

public class NetHelper {

	public static boolean ReadData(InputStream steram, int length, byte[] buffer)
			throws IOException {

		int size = 0;
		int count = 0;

		while (length - size > 0) {

			count = steram.read(buffer, size, length - size);

			if (count == -1)
				break;

			size += count;
		}

		return size >= length;
	}
}
