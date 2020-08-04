package com.vantageclient.decoder;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import android.util.Log;
import android.view.SurfaceHolder;

import com.org.ffmpeg.FFmpegDemo.CodecID;
import com.vantageclient.Interfaces.IVideoDecoder;
import com.vantageclient.net.IMjpegCallBack;
import com.vantageclient.net.NetHelper;

import org.MediaPlayer.PlayM4.*;

public class HikDecoderEx implements IVideoDecoder {

	public static final String TAG = "HikDecoderEx";
	private boolean _streaming = false;
	private IMjpegCallBack _callBack;

	private Player _player;
	private int _port;

	private InputStream _inputStream;

	private SurfaceHolder _holder;

	@Override
	public boolean setSurfaceHolder(SurfaceHolder holder) {

		_holder = holder;
		return true;
	}

	@Override
	public boolean start(CodecID videoTpe, InputStream inputStream,
						 byte[] hikHeader, IMjpegCallBack callBack) {

		byte[] head = new byte[40];
		System.arraycopy(hikHeader, 72, head, 0, 40);

		_callBack = callBack;
		_player = Player.getInstance();
		_port = _player.getPort();

		_player.setStreamOpenMode(_port, Constants.STREAME_REALTIME);
		_player.openStream(_port, head, 40, 1024 * 1024);
		_player.setDisplayBuf(_port, 1024 * 1024);
		boolean ret = _player.play(_port, _holder);

		_streaming = true;
		_inputStream = inputStream;
		readFromStream();

		return ret;
	}

	public void readFromStream() {
		Log.d(TAG, "ReadFromStream");
		byte[] packetHeader = new byte[28];
		byte[] buffer = new byte[1024 * 100];
		byte[] bLength = new byte[4];
		int length;

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
					_player.inputData(_port, videoData, length - 108);

				} else {
					Log.i(TAG, "Read Stream Error.");
					break;
				}

			} catch (Exception e) {
				Log.e("StreamTester", e.getMessage() + "\n"
						+ e.getClass().toString());
			}
		}

		_player.stop(_port);
		_player.closeStream(_port);
		_player.freePort(_port);
	}

	@Override
	public boolean stop() {
		_streaming = false;

		_player.stop(_port);
		_player.closeStream(_port);
		_player.freePort(_port);

		return false;
	}

	public void decodeYUV420SP(byte[] rgb, byte[] yuv420sp, int width,
							   int height) {
		final int frameSize = width * height;

		for (int j = 0, yp = 0; j < height; j++) {
			int uvp = frameSize + (j >> 1) * width, u = 0, v = 0;
			for (int i = 0; i < width; i++, yp++) {
				int y = (0xff & ((int) yuv420sp[yp])) - 16;
				if (y < 0)
					y = 0;
				if ((i & 1) == 0) {
					v = (0xff & yuv420sp[uvp++]) - 128;
					u = (0xff & yuv420sp[uvp++]) - 128;
				}

				int y1192 = 1192 * y;
				int r = (y1192 + 1634 * v);
				int g = (y1192 - 833 * v - 400 * u);
				int b = (y1192 + 2066 * u);

				if (r < 0)
					r = 0;
				else if (r > 262143)
					r = 262143;
				if (g < 0)
					g = 0;
				else if (g > 262143)
					g = 262143;
				if (b < 0)
					b = 0;
				else if (b > 262143)
					b = 262143;

				rgb[yp] = (byte) (0xff000000 | ((r << 6) & 0xff0000)
						| ((g >> 2) & 0xff00) | ((b >> 10) & 0xff));
			}
		}
	}

}
