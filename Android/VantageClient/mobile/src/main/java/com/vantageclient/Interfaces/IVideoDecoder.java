package com.vantageclient.Interfaces;

import android.view.SurfaceHolder;

import java.io.InputStream;

import com.vantageclient.net.IMjpegCallBack;
import com.org.ffmpeg.FFmpegDemo.CodecID;

public interface IVideoDecoder {

    public boolean start(CodecID videoTpe, InputStream inputStream,
                         byte[] hikHeader, IMjpegCallBack callBack);

    public boolean setSurfaceHolder(SurfaceHolder holder);

    public boolean stop();
}
