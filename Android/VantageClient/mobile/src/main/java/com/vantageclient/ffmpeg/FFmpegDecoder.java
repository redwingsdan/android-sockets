package com.vantageclient.ffmpeg;

public class FFmpegDecoder {
	static {
		System.loadLibrary("ffmpeg");
	}

	public native int init(int codec);
}
