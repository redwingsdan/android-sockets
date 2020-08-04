package com.vantageclient.data;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class RequestItem {
	public static final int StartVideo = 1;
	public static final int KBPS = -1;
	private int _command;
	private int _channel;
	private boolean _mainStream;

	public void setCommand(int value) {
		_command = value;
	}

	public void setChannel(int value) {
		_channel = value;
	}

	public void setMainStream(boolean value) {
		_mainStream = value;
	}

	public byte[] getBytes() {
		ByteBuffer buff = ByteBuffer.allocate(16);
		buff.order(ByteOrder.LITTLE_ENDIAN);
		buff.putInt(_command);
		buff.putInt(_channel);
		buff.putInt(_mainStream ? 1 : 0);
		buff.putInt(KBPS);

		return buff.array();
	}
}