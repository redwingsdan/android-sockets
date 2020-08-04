package com.vantageclient.decoder;

import com.org.ffmpeg.FFmpegDemo.CodecID;

public class VideoTypeHelper {

	static byte[] MJPEGTag = { (byte) 0xff, (byte) 0xd8 };

	static byte[] MPEG4Tag1 = { 0x00, 0x00, 0x01, (byte) 0xb0 };
	static byte[] MPEG4Tag2 = { 0x00, 0x00, 0x01, (byte) 0xb6 };
	static byte[] MPEG4Tag3 = { 0x00, 0x00, 0x00, 0x00 };
	static byte[] MPEG4Tag4 = { 0x00, 0x00, 0x01, (byte) 0xb2 };

	static byte[] H264Tag1 = { 0x00, 0x00, 0x01, 0X67 };
	static byte[] H264Tag2 = { 0x00, 0x00, 0x01, 0X65 };
	static byte[] H264Tag3 = { 0x00, 0x00, 0x01, 0X41 };
	static byte[] H264Tag4 = { 0x00, 0x00, 0x00, 0X01 };
	static byte[] H264Tag5 = { 0x00, 0x00, 0x01, 0X27 };
	static byte[] H264Tag6 = { 0x00, 0x00, 0x01, (byte) 0XBA };

	static byte[] HikTag = { 0x00, 0x00, 0x01, 0X27 };

	public static CodecID GetVideoType(byte[] buffer) {
		CodecID videoType = CodecID.CODEC_ID_NONE;

		if (buffer.length < 4)
			return videoType;

		byte[] temp = new byte[4];

		System.arraycopy(buffer, 0, temp, 0, 4);

		if (buffer[0] == 0x00) {
			if (CompareByteArray(MPEG4Tag1, temp)
					|| CompareByteArray(MPEG4Tag2, temp)
					|| CompareByteArray(MPEG4Tag3, temp)
					|| CompareByteArray(MPEG4Tag4, temp)) {
				videoType = CodecID.CODEC_ID_MPEG4;
			} else if (CompareByteArray(H264Tag1, temp)
					|| CompareByteArray(H264Tag2, temp)
					|| CompareByteArray(H264Tag3, temp)
					|| CompareByteArray(H264Tag4, temp)
					|| CompareByteArray(H264Tag5, temp)
					|| CompareByteArray(H264Tag6, temp)) {
				videoType = CodecID.CODEC_ID_H264;
			}
		} else {
			if (temp[0] == MJPEGTag[0] && temp[1] == MJPEGTag[1]) {
				videoType = CodecID.CODEC_ID_MJPEG;
			}
		}

		return videoType;
	}

	private static boolean CompareByteArray(byte[] buffer1, byte[] buffer2) {
		return buffer1[0] == buffer2[0] && buffer1[1] == buffer2[1]
				&& buffer1[2] == buffer2[2] && buffer1[3] == buffer2[3];
	}
}
