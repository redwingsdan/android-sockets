package com.vantageclient.decoder;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import android.util.Log;
import android.view.SurfaceHolder;

import com.vantageclient.data.ByteHelper;
import com.vantageclient.net.IMjpegCallBack;
import com.vantageclient.net.NetHelper;
import com.org.ffmpeg.FFmpegDemo.CodecID;

public class JpegDecoder implements IVideoDecoder {

	public static final String TAG = "JpegReader";
	private boolean _streaming = false;

	private InputStream _inputStream;
	private IMjpegCallBack _callBack;
	public int _viewNumber = -1;
	@Override
	public boolean start(CodecID videoTpe, InputStream inputStream,
			byte[] hikHeader, IMjpegCallBack callBack) {

		_streaming = true;

		_inputStream = inputStream;
		_callBack = callBack;

		readFromStream();

		return true;
	}

	public void setViewNumber(int vN)
	{
		_viewNumber =vN;
	}
	@Override
	public boolean stop() {
		_streaming = false;
		return false;
	}

	public void readFromStream() {
		byte[] packetHeader = new byte[28];
		byte[] buffer = new byte[1024 * 100];
		byte[] bLength = new byte[4];
		int length;

		while (_streaming) {
			try {

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

					byte[] videoData = new byte[length - 108];
					System.arraycopy(buffer, 88, videoData, 0, length - 108);

					if (_callBack != null)
						_callBack.mjpegDataReceived(ByteHelper
								.bytes2Bimap(videoData),this._viewNumber);
				} else {
					Log.i(TAG, "Read Stream Error.");
					break;
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public boolean setSurfaceHolder(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		return false;
	}
}
