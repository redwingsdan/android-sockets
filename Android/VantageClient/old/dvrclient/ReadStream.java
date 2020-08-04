package com.vantageclient.dvrclient;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import android.util.Log;

import com.android.SimplePlayer.SimplePlayer;
import com.vantageclient.data.*;

public final class ReadStream {

	public static final String TAG = "ReadStream";

	private int _port;
	private int _channel;
	private byte[] _header;

	private SimplePlayer _player;
	private InetAddress _ipAddr;

	private Socket _socket;
	private InputStream _inStream;
	private OutputStream _outStream;

	private Thread _readThread;
	private boolean _streaming = false;
	private boolean _isExit = false;

	public ReadStream(InetAddress addr, int port, int channel, byte[] header,
			SimplePlayer player) {
		_ipAddr = addr;
		_port = port;
		_header = header;
		_player = player;
		_channel = channel;
	}

	public boolean start() throws IOException {
		_socket = new Socket(_ipAddr, _port);

		_inStream = _socket.getInputStream();
		_outStream = _socket.getOutputStream();

		// send request
		RequestItem item = new RequestItem();
		item.setCommand(RequestItem.StartVideo);
		item.setChannel(_channel);
		item.setMainStream(DvrMainActivity.useMainStreamBoolean);

		_outStream.write(item.getBytes());

		// receive response
		byte[] recieveBuffer = new byte[1024];
		int bytesRead = 0;
		int length = 8;
		int count = 0;
		while (length - bytesRead > 0) {
			count = _inStream
					.read(recieveBuffer, bytesRead, length - bytesRead);

			if (count == -1) {
				continue;
			}

			bytesRead += count;
		}

		ResponseItem response = new ResponseItem(recieveBuffer);
		boolean accepted = response.getCommand() == RequestItem.StartVideo;

		if (accepted) {

			_readThread = new Thread(new Runnable() {
				public void run() {
					if (!_isExit) {
						_streaming = true;
						readFromStream();
					}
				}
			});

			_readThread.start();

		} else {
			_socket.close();
		}

		return accepted;
	}

	public void stop() {
		_isExit = true;
		_streaming = false;
		try {
			_readThread.join(500);
		} catch (InterruptedException e) {
			Log.w(TAG, e.getMessage());
		}
		Destroy();
	}

	private void recevieData(int length, byte[] buffer) throws IOException {
		int receivedSize = 0;
		int count = 0;

		while (length - receivedSize > 0) {

			count = _inStream.read(buffer, receivedSize, length - receivedSize);

			if (count == -1) {
				_isExit = true;
				break;
			}

			receivedSize += count;
		}
	}

	public void readFromStream() {
		Log.d(TAG, "ReadFromStream");
		byte[] packetHeader = new byte[28]; // Packet Header
		byte[] buffer = new byte[1024 * 100];
		byte[] bLength = new byte[4];
		int length;

		byte[] head = new byte[40];
		if (_header.length > 0) {
			System.arraycopy(_header, 72, head, 0, 40);
			_player.playerInputData(head, 40, 1);
		}

		while (_streaming && !_isExit) {
			try {

				// Receive _DataItem
				recevieData(8, packetHeader);

				System.arraycopy(packetHeader, 4, bLength, 0, 4);

				ByteBuffer buff = ByteBuffer.wrap(bLength);
				buff.order(ByteOrder.LITTLE_ENDIAN);
				length = buff.getInt();

				recevieData(length, buffer);

				byte[] bytes2 = new byte[4];
				System.arraycopy(buffer, 20 + 16, bytes2, 0, 4);
				ByteBuffer buff1 = ByteBuffer.wrap(bytes2);
				buff1.order(ByteOrder.LITTLE_ENDIAN);

				// int frameEncryptFlag = buff1.getInt();
				//
				// int encryptFlagSize = 0;
				// switch (frameEncryptFlag) {
				// case 0:
				// encryptFlagSize = 0;
				// break;
				// case 1:
				// encryptFlagSize = 16;
				// break;
				// case 2:
				// encryptFlagSize = 20;
				// break;
				// }

				byte[] videoData = new byte[length - 108];
				System.arraycopy(buffer, 88, videoData, 0, length - 108);
				_player.playerInputData(videoData, length - 108, 1);

			} catch (Exception e) {
				Log.e("StreamTester", e.getMessage() + "\n"
						+ e.getClass().toString());
			}
		}
	}

	public void Destroy() {
		if (_socket != null) {
			try {
				_socket.close();
			} catch (Exception e) {
				Log.e(TAG, e.getMessage());
			}
		}
	}
}
