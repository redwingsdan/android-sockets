package com.vantageclient.data;

import java.io.*;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class DvrPacketUtils {

	public static int getDataLength(InputStream in) {
		try {
			byte[] buffer = new byte[16];
			int size = 0;
			while (16 - size > 0) {
				int read;

				read = in.read(buffer, size, 16 - size);

				if (read < -1) {
					break;
				}
				size += read;
			}

			ByteBuffer buff1 = ByteBuffer.wrap(buffer, 0, 16);
			buff1.order(ByteOrder.LITTLE_ENDIAN);

			return buff1.getInt();
		} catch (IOException e) {
			e.printStackTrace();
			return 0;
		}
	}
}
