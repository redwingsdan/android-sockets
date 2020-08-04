package com.vantageclient.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import android.util.Log;
import android.view.SurfaceHolder;

import com.vantageclient.data.RequestItem;
import com.vantageclient.data.ResponseItem;
import com.vantageclient.decoder.FFMpegDecoder;
import com.vantageclient.decoder.HikDecoderEx;
import com.vantageclient.Interfaces.IVideoDecoder;
import com.vantageclient.decoder.JpegDecoder;
import com.vantageclient.decoder.VideoTypeHelper;
import com.org.ffmpeg.FFmpegDemo.CodecID;

public class StreamReader {

	public static final String TAG = "ReadStream";
	public static final String GETREQUEST = "Get Sub channel=%d";
	public static final String ENCODING = "US-ASCII";

	private InetAddress _ipAddr;
	private int _port;
	private int _channel;
	private byte[] _header;

	private Socket _socket;
	private InputStream _inStream;
	private OutputStream _outStream;

	private IMjpegCallBack _callBack;
	private Thread _readThread;
	private boolean _isExit = false;

	private SurfaceHolder _hoder;
	private IVideoDecoder _decoder;
	public int viewNumber;
	public StreamReader(InetAddress addr, int port, int channel, byte[] header,
			IMjpegCallBack call,int viewNu) {
		_ipAddr = addr;
		_port = port;
		_header = header;
		_callBack = call;
		_channel = channel;
		_header = header;
		viewNumber = viewNu;
	}

	public boolean setSurfaceHolder(SurfaceHolder holder) {
		_hoder = holder;
		return true;
	}

	public boolean start() throws IOException {
		_socket = new Socket(_ipAddr, _port);

		_inStream = _socket.getInputStream();
		_outStream = _socket.getOutputStream();

		// send request
		RequestItem item = new RequestItem();
		item.setCommand(RequestItem.StartVideo);
		item.setChannel(_channel);
		item.setMainStream(false);

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

	private void readFromStream() {
		Log.e(TAG, "Start Read.");
		byte[] packetHeader = new byte[28];
		byte[] buffer = new byte[1024 * 100];
		byte[] lengthBuffer = new byte[4];

		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			if (NetHelper.ReadData(_inStream, 8, packetHeader)) {

				System.arraycopy(packetHeader, 4, lengthBuffer, 0, 4);

				ByteBuffer buff = ByteBuffer.wrap(lengthBuffer);
				buff.order(ByteOrder.LITTLE_ENDIAN);
				int length = buff.getInt();

				if (NetHelper.ReadData(_inStream, length, buffer)) {

					if(length -108 < 0)
					{
						return;
					}
					else
					{
					}
					byte[] videoData = new byte[length - 108];
					System.arraycopy(buffer, 88, videoData, 0, length - 108);

					CodecID codec = VideoTypeHelper.GetVideoType(videoData);

					Log.e(TAG, "Start decode.");

					if (_header != null && _header.length > 0
							&& codec == CodecID.CODEC_ID_NONE) {

						_decoder = new HikDecoderEx();
						_decoder.setSurfaceHolder(_hoder);
						_decoder.start(codec, _inStream, _header, _callBack);
					} else if (codec == CodecID.CODEC_ID_MJPEG) {

						_decoder = new JpegDecoder();
						((JpegDecoder)_decoder).setViewNumber(this.viewNumber);
						_decoder.setSurfaceHolder(_hoder);
						_decoder.start(codec, _inStream, _header, _callBack);
					} else if (codec == CodecID.CODEC_ID_MPEG4
							|| codec == CodecID.CODEC_ID_H264) {

						_decoder = new FFMpegDecoder();
						((FFMpegDecoder)_decoder).setViewNumber(this.viewNumber);
						_decoder.setSurfaceHolder(_hoder);
						_decoder.start(codec, _inStream, _header, _callBack);
					}
				} else {
					Log.e(TAG, "Read data error");
				}
			} else {
				Log.e(TAG, "Read data error");
				this.stop();
			}
		} catch (IOException e) {
			Log.e(TAG, "Failed to get stream type.");
		}
	}

	public void stop() {
		_isExit = true;

		if (_decoder != null) {
			_decoder.stop();
		}

		Destroy();

		try {
			_readThread.join();
		} catch (InterruptedException e) {
		}
	}

	public void Destroy() {
		_isExit = true;

		if (_socket != null) {
			try {
				_socket.close();
			} catch (Exception e) {
			}
		}
	}
}
