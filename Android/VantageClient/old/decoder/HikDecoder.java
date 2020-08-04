package com.vantageclient.decoder;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import android.util.Log;
import android.view.SurfaceHolder;

import com.android.SimplePlayer.SimplePlayer;
import com.vantageclient.net.IMjpegCallBack;
import com.vantageclient.net.NetHelper;
import com.org.ffmpeg.FFmpegDemo.CodecID;

public class HikDecoder implements IVideoDecoder {

	public static final String TAG = "HikDecoder";
	private boolean _streaming = false;

	private SimplePlayer _player;
	private InputStream _inputStream;
	private byte[] _header;

	@Override
	public boolean start(CodecID videoTpe, InputStream inputStream,
			byte[] hikHeader, IMjpegCallBack callBack) {

		_streaming = true;
		_inputStream = inputStream;
		_header = hikHeader;

		_player = SimplePlayer.newInstance();
		_player.setCallBack(callBack);
		_player.playerInitialize();
		_player.playChannel(1);

		readFromStream();

		return true;
	}

	@Override
	public boolean stop() {
		_streaming = false;
		_player.playerStop(1);
		_player.playerRelease();

		return true;
	}

	public void readFromStream() {
		Log.d(TAG, "ReadFromStream");
		byte[] packetHeader = new byte[28];
		byte[] buffer = new byte[1024 * 100];
		byte[] bLength = new byte[4];
		int length;

		byte[] head = new byte[40];
		if (_header.length > 0) {
			System.arraycopy(_header, 72, head, 0, 40);
			_player.playerInputData(head, 40, 1);
		}

		while (_streaming) {
			try {

				// Receive _DataItem
				if (NetHelper.ReadData(_inputStream, 8, packetHeader)) {
					System.arraycopy(packetHeader, 4, bLength, 0, 4);

					ByteBuffer buff = ByteBuffer.wrap(bLength);
					buff.order(ByteOrder.LITTLE_ENDIAN);
					length = buff.getInt();
				} else {
					Log.i(TAG, "Read Stream Error.");
					break;
				}

				if (NetHelper.ReadData(_inputStream, length, buffer)) {

					byte[] bytes2 = new byte[4];
					System.arraycopy(buffer, 20 + 16, bytes2, 0, 4);
					ByteBuffer buff1 = ByteBuffer.wrap(bytes2);
					buff1.order(ByteOrder.LITTLE_ENDIAN);

					byte[] videoData = new byte[length - 108];
					System.arraycopy(buffer, 88, videoData, 0, length - 108);
					_player.playerInputData(videoData, length - 108, 1);

				} else {
					Log.i(TAG, "Read Stream Error.");
					break;
				}

			} catch (Exception e) {
				Log.e("StreamTester", e.getMessage() + "\n"
						+ e.getClass().toString());
			}
		}

		_player.playerStop(1);
		_player.playerRelease();
	}

	@Override
	public boolean setSurfaceHolder(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		return false;
	}
}
