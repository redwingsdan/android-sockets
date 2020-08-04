package com.android.SimplePlayer;

import java.nio.ByteBuffer;

public class BMPImage {
	private byte[] bfType = { 66, 77 };
	private int bfSize = 0;
	private int bfReserved1 = 0;
	private int bfReserved2 = 0;
	private int bfOffBits = 54;

	private int biSize = 40;
	private int biWidth = 352;
	private int biHeight = 288;
	private int biPlanes = 1;
	private int biBitCount = 24;
	private int biCompression = 0;
	private int biSizeImage = this.biWidth * this.biHeight * 3;
	private int biXPelsPerMeter = 0;
	private int biYPelsPerMeter = 0;
	private int biClrUsed = 0;
	private int biClrImportant = 0;
	private ByteBuffer bmpBuffer = null;

	public BMPImage(byte[] Data, int Width, int Height) {
		this.biWidth = Width;
		this.biHeight = Height;
		this.bfSize = (54 + this.biWidth * this.biHeight * 3);
		this.bmpBuffer = ByteBuffer.allocate(54 + this.biWidth * this.biHeight
				* 3);
		writeBitmapFileHeader();
		writeBitmapInfoHeader();
		this.bmpBuffer.put(Data, 0, this.biWidth * this.biHeight * 3);
	}

	public byte[] getByte() {
		return this.bmpBuffer.array();
	}

	private byte[] intToWord(int parValue) {
		byte[] retValue = new byte[2];
		retValue[0] = (byte) (parValue & 0xFF);
		retValue[1] = (byte) (parValue >> 8 & 0xFF);
		return retValue;
	}

	private byte[] intToDWord(int parValue) {
		byte[] retValue = new byte[4];
		retValue[0] = (byte) (parValue & 0xFF);
		retValue[1] = (byte) (parValue >> 8 & 0xFF);
		retValue[2] = (byte) (parValue >> 16 & 0xFF);
		retValue[3] = (byte) (parValue >> 24 & 0xFF);
		return retValue;
	}

	private void writeBitmapFileHeader() {
		this.bmpBuffer.put(this.bfType);
		this.bmpBuffer.put(intToDWord(this.bfSize));
		this.bmpBuffer.put(intToWord(this.bfReserved1));
		this.bmpBuffer.put(intToWord(this.bfReserved2));
		this.bmpBuffer.put(intToDWord(this.bfOffBits));
	}

	private void writeBitmapInfoHeader() {
		this.bmpBuffer.put(intToDWord(this.biSize));
		this.bmpBuffer.put(intToDWord(this.biWidth));
		this.bmpBuffer.put(intToDWord(this.biHeight));
		this.bmpBuffer.put(intToWord(this.biPlanes));
		this.bmpBuffer.put(intToWord(this.biBitCount));
		this.bmpBuffer.put(intToDWord(this.biCompression));
		this.bmpBuffer.put(intToDWord(this.biSizeImage));
		this.bmpBuffer.put(intToDWord(this.biXPelsPerMeter));
		this.bmpBuffer.put(intToDWord(this.biYPelsPerMeter));
		this.bmpBuffer.put(intToDWord(this.biClrUsed));
		this.bmpBuffer.put(intToDWord(this.biClrImportant));
	}
}
