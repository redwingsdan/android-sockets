package com.vantageclient.net;

import java.io.IOException;
import java.io.InputStream;

public class NetHelper {

	public static boolean ReadData(InputStream stream, int length, byte[] buffer)
			throws IOException {

		int size = 0;
		int count;

		while (length - size > 0) {

			count = stream.read(buffer, size, length - size);

			if (count == -1)
				break;

			size += count;
		}

		return size >= length;
	}
}
