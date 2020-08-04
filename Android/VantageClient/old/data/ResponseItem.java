package com.vantageclient.data;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class ResponseItem {
	private int _command;
	private int _response;

	public int getCommand() {
		return _command;
	}

	public int getResponse() {
		return _response;
	}

	public ResponseItem(byte[] bytes) {
		ByteBuffer buff = ByteBuffer.wrap(bytes);
		buff.order(ByteOrder.LITTLE_ENDIAN);
		_response = buff.getInt(4);
		_command = buff.getInt();
	}
}