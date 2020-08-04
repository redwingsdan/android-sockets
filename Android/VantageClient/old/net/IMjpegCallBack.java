package com.vantageclient.net;

import android.graphics.Bitmap;

public interface IMjpegCallBack {
	void mjpegDataReceived(Bitmap image,int viewNumber);
}
