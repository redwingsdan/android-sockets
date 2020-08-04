package com.vantageclient.decoder;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.util.Log;
import android.view.SurfaceHolder;

import com.vantageclient.net.IMjpegCallBack;
import com.vantageclient.net.NetHelper;
import com.org.ffmpeg.FFmpegDemo;
import com.org.ffmpeg.FFmpegDemo.CodecID;
import com.vantageclient.Interfaces.IVideoDecoder;

public class FFMpegDecoder implements IVideoDecoder {

	public static final String TAG = "FFMpegDecoder";
	private boolean _streaming = false;

	private FFmpegDemo _ffMpegDecoder;
	private InputStream _inputStream;
	private IMjpegCallBack _callBack;

	private Bitmap _videoBit;
	private byte[] _bmpdata;
	private int _width = 640;
	private int _heigth = 480;
	public int _viewNumber = -1;

	@Override
	public boolean start(CodecID videoTpe, InputStream inputStream,
			byte[] hikHeader, IMjpegCallBack callBack) {

		_streaming = true;
		_ffMpegDecoder = new FFmpegDemo();
		_ffMpegDecoder.setCodec(videoTpe.value());
		_ffMpegDecoder.init(videoTpe.value());

		_inputStream = inputStream;
		_callBack = callBack;
		_videoBit = Bitmap.createBitmap(640, 480, Config.RGB_565);

		if (hikHeader != null && hikHeader.length > 0) {
			byte[] head = new byte[40];
			System.arraycopy(hikHeader, 72, head, 0, 40);

			_ffMpegDecoder.decode(head, head.length, _bmpdata);
		}
		readFromStream();

		return true;
	}

	@Override
	public boolean stop() {
		_streaming = false;
		return true;
	}

	public void setViewNumber(int i)
	{
		this._viewNumber = i;
	}
	public void readFromStream() {
		byte[] packetHeader = new byte[28];
		byte[] buffer = new byte[1024 * 100];
		byte[] bLength = new byte[4];

		int length;
		int decodeout = 0;

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

					_bmpdata = new byte[1024 * 1024];

					//long begin = System.currentTimeMillis();
					decodeout = _ffMpegDecoder.decode(videoData,
							videoData.length, _bmpdata);

					//long time = System.currentTimeMillis() - begin;

					//Log.i("DecodeTime", String.format("%d", time));

					if (decodeout > 0) {
						_width = _ffMpegDecoder.getwidth();
						_heigth = _ffMpegDecoder.getheight();

						ByteBuffer buffer1 = ByteBuffer.wrap(_bmpdata);
						_videoBit = Bitmap.createBitmap(_width, _heigth,
								Config.RGB_565);
						_videoBit.copyPixelsFromBuffer(buffer1);

						if (_callBack != null)
						{
							_callBack.mjpegDataReceived(_videoBit,_viewNumber);
						}
					}

					_bmpdata = null;
				} else {
					Log.i(TAG, "Read Stream Error.");
					break;
				}

			} catch (Exception e) {
				Log.e("StreamTester", e.getMessage() + "\n"
						+ e.getClass().toString());
			}
		}

		_ffMpegDecoder.uninit();
	}

	@Override
	public boolean setSurfaceHolder(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		return false;
	}

}
