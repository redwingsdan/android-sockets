package org.MediaPlayer.PlayM4;

import android.os.Build.VERSION;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;

public class Player {

    static
    {
        System.loadLibrary("CpuFeatures");
    }

    private static Player mPlayer = null;

    public static class MPInteger { public int value; }

    private Player()
    {
        int cpuFeature = GetCpuFeatures();


     if (3 == cpuFeature) {
       System.loadLibrary("PlayCtrl");
     }
     else if (2 == cpuFeature) {
       System.loadLibrary("PlayCtrl_v7");
     }
     else if (1 == cpuFeature) {
       System.loadLibrary("PlayCtrl_v5");
     }
     else {
       Log.i("PlayerSDK", "Not a arm CPU! FAIL to load PlayCtrl!");
     }
   }

       public static Player getInstance()
   {
     if (VERSION.SDK_INT < 9) {
       Log.e("PlayerSDK", "Android Level Lower than 2.3!");
       return null;
     }

     if (mPlayer == null) {
       try {
         mPlayer = new Player();
       } catch (Exception e) {
         e.printStackTrace();
       }
     }

     return mPlayer;
   }

       public int getPort()
   {
     return GetPort();
   }

       public boolean freePort(int nPort)
   {
       return FreePort(nPort) != 0;

   }

       public boolean play(int nPort, SurfaceHolder holder)
   {
     Surface surface = null;

     if (holder != null) {
       surface = holder.getSurface();

       if (surface == null) {
         return false;
       }

       if (!surface.isValid()) {
         Log.e("PlayerSDK", "Surface Invalid!");
         return false;
       }
     }

     if (Play(nPort, surface) == 0) {
       Log.e("PlayerSDK", "Play false!");
       return false;
     }

     return true;
   }

       public boolean stop(int nPort)
   {
       return Stop(nPort) != 0;

   }

    public boolean setStreamOpenMode(int nPort, int nMode)
   {
       if (SetStreamOpenMode(nPort, nMode) == 0) {
           return false;
       }
       return true;
   }

    public boolean openStream(int nPort, byte[] pFileHeadBuf, int nSize, int nBufPoolSize)
    {
       if (OpenStream(nPort, pFileHeadBuf, nSize, nBufPoolSize) == 0) {
           return false;
       }
        return true;
    }

    public boolean setDisplayBuf(int nPort, int nNum)
   {
       if (SetDisplayBuf(nPort, nNum) == 0) {
           return false;
       }
       return true;
   }

       public boolean closeStream(int nPort)
   {
       return CloseStream(nPort) != 0;

   }

       public boolean inputData(int nPort, byte[] pBuf, int nSize)
   {
       return InputData(nPort, pBuf, nSize) != 0;
   }



   private native int GetPort();

   private native int FreePort(int paramInt);

   private native int OpenFile(int paramInt, byte[] paramArrayOfByte);

   private native int CloseFile(int paramInt);

   private native int GetLastError(int paramInt);

   private native int GetSdkVersion();

   private native int Play(int paramInt, Surface paramSurface);

   private native int Stop(int paramInt);

   private native int Pause(int paramInt1, int paramInt2);

   private native int Fast(int paramInt);

   private native int Slow(int paramInt);

   private native int PlaySound(int paramInt);

   private native int StopSound();

   private native int SetStreamOpenMode(int paramInt1, int paramInt2);

   private native int OpenStream(int paramInt1, byte[] paramArrayOfByte, int paramInt2, int paramInt3);

   private native int CloseStream(int paramInt);

   private native int InputData(int paramInt1, byte[] paramArrayOfByte, int paramInt2);

   private native long GetFileTime(int paramInt);

   private native int GetFileTotalFrames(int paramInt);

   private native float GetPlayPos(int paramInt);

   private native int SetPlayPos(int paramInt, float paramFloat);

   private native int GetPlayedTime(int paramInt);

   private native int GetPlayedTimeEx(int paramInt);

   private native int SetPlayedTimeEx(int paramInt1, int paramInt2);

   private native int GetPlayedFrames(int paramInt);

   private native int GetCurrentFrameNum(int paramInt);

   private native int SetCurrentFrameNum(int paramInt1, int paramInt2);

   private native int SetVideoWindow(int paramInt1, int paramInt2, Surface paramSurface);

   private native int GetCurrentFrameRate(int paramInt);

   private native int GetPictureSize(int paramInt, MPInteger paramMPInteger1, MPInteger paramMPInteger2);

   private native int GetSourceBufferRemain(int paramInt);

   private native int ResetSourceBuffer(int paramInt);

   private native int ResetSourceBufFlag(int paramInt);

   private native int GetDisplayBuf(int paramInt);

   private native int SetDisplayBuf(int paramInt1, int paramInt2);

   private native int ResetBuffer(int paramInt1, int paramInt2);

   private native int GetBufferValue(int paramInt1, int paramInt2);

   private native int SetDecodeFrameType(int paramInt1, int paramInt2);

   private native int GetBMP(int paramInt1, byte[] paramArrayOfByte, int paramInt2, MPInteger paramMPInteger);

   private native int GetJPEG(int paramInt1, byte[] paramArrayOfByte, int paramInt2, MPInteger paramMPInteger);

   private native int SetFileEndCallback(int paramInt, PlayerCallBack.PlayerPlayEndCB paramPlayerPlayEndCB);

   private native int SetDisplayCallback(int paramInt, PlayerCallBack.PlayerDisplayCB paramPlayerDisplayCB);

   private native int SetDecodeCallback(int paramInt, PlayerCallBack.PlayerDecodeCB paramPlayerDecodeCB);

   private native int SetSecretKey(int paramInt1, int paramInt2, byte[] paramArrayOfByte, int paramInt3);

   private native int GetCpuFeatures();

   private native int SetFileRefCallBack(int paramInt, PlayerCallBack.PlayerFileRefCB paramPlayerFileRefCB);

   private native int VerticalFlip(int paramInt1, int paramInt2);

   private native void SetAndroidSDKVersion(int paramInt);

   private native int SetImageCorrection(int paramInt1, int paramInt2);
 }