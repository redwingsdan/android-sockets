package com.android.SimplePlayer;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.SurfaceView;
import java.util.HashMap;
import java.util.Map;

import com.vantageclient.net.IMjpegCallBack;

public class SimplePlayer {
	private static final int DefaultValue = 1;
	private static SimplePlayer mPlayer = null;
	private Map<Integer, SurfaceView> mViewChannelPair = new HashMap<Integer, SurfaceView>();

	private final String TAG = "Player";

	static {
		System.loadLibrary("SingleDecode");
		System.loadLibrary("SimplePlayer");
	}

	public static SimplePlayer newInstance() {
		return newInstance(1);
	}

	public static SimplePlayer newInstance(int channelNum) {
		if (channelNum < 1) {
			channelNum = 1;
		} else if (channelNum > 4) {
			channelNum = 4;
		}

		setChannelNumber(channelNum);

		if (mPlayer == null) {
			mPlayer = new SimplePlayer();
		}

		return mPlayer;
	}

	public void setPlayviewChannel(SurfaceView view, int channelNum) {
		this.mViewChannelPair.put(Integer.valueOf(channelNum), view);

	}

	public void playChannel(int channelID) {
		playerPlay(this, channelID);
	}

	private void PlayerSDK_RGBDataCallBack(byte[] rgbOutput, int iWidth,
			int iHeight, int viewID) {

		byte[] bitmapByteArray = new BMPImage(rgbOutput, iWidth, iHeight)
				.getByte();
		Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapByteArray, 0,
				bitmapByteArray.length);

		if (callBack != null)
			callBack.mjpegDataReceived(bitmap,-1);
	}

	private IMjpegCallBack callBack;

	public void setCallBack(IMjpegCallBack call) {
		callBack = call;
	}

	private static native boolean setChannelNumber(int paramInt);

	public native boolean playerInitialize();

	public native void playerRelease();

	public native boolean playerSetDecodeSecretKey(long paramLong1,
			byte[] paramArrayOfByte, long paramLong2);

	public native boolean playerInputData(byte[] paramArrayOfByte,
			int paramInt1, int paramInt2);

	private native boolean playerPlay(SimplePlayer paramSimplePlayer,
			int paramInt);

	public native boolean playerStop(int paramInt);

	private native int playerGetLastError(int paramInt);

	private native boolean playerCaptureJpeg(int paramInt);
}
