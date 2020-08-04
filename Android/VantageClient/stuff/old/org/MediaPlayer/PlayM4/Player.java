package com.org.MediaPlayer.PlayM4;

import android.os.Build;
import android.os.Build.VERSION;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;

public class Player {
private static Player mPlayer = null;
private static final String TAG = "PlayerSDK";
/*     */   private static final int CPU_NEON = 3;
/*     */   private static final int CPU_ARMv7 = 2;
/*     */   private static final int CPU_NOT_ARMv7 = 1;
/*     */   private static final int CPU_NOT_ARM = 0;
/*     */   public static final int STREAM_REALTIME = 0;
/*     */   public static final int STREAM_FILE = 1;
/*     */   
/*     */   public static class MPInteger { public int value; }
/*     */   
/*     */   public static class MPRect { public int left;
/*     */     public int right;
/*     */     public int top;
/*     */     public int bottom; }
/*     */   
/*     */   public static class MPSystemTime { public int year;
/*     */     public int month;
/*     */     public int day;
/*     */     public int hour;
/*     */     public int min;
/*     */     public int sec;
/*     */     public int ms; }
/*     */   
/*  56 */   public static class MP_DECODE_TYPE { public static int DECODE_ALL = 0;
/*  57 */     public static int DECODE_VIDEO_KEYFRAME = 1;
/*  58 */     public static int DECODE_NONE = 2;
/*     */   }

/*     */   private Player()
/*     */   {
/* 104 */     int cpuFeature = GetCpuFeatures();
/*     */     
/*     */ 
/* 107 */     if (3 == cpuFeature) {
/* 108 */       System.loadLibrary("PlayCtrl");
/*     */     }
/* 110 */     else if (2 == cpuFeature) {
/* 111 */       System.loadLibrary("PlayCtrl_v7");
/*     */     }
/* 113 */     else if (1 == cpuFeature) {
/* 114 */       System.loadLibrary("PlayCtrl_v5");
/*     */     }
/*     */     else {
/* 117 */       Log.i("PlayerSDK", "Not a arm CPU! FAIL to load PlayCtrl!");
/*     */     }
/*     */   }

/*     */   public static Player getInstance()
/*     */   {
/* 132 */     if (VERSION.SDK_INT < 9) {
/* 133 */       Log.e("PlayerSDK", "Android Level Lower than 2.3!");
/* 134 */       return null;
/*     */     }
/*     */     
/* 137 */     if (mPlayer == null) {
/*     */       try {
/* 139 */         mPlayer = new Player();
/*     */       } catch (Exception e) {
/* 141 */         e.printStackTrace();
/*     */       }
/*     */     }
/*     */     
/* 145 */     return mPlayer;
/*     */   }

/*     */   public int getPort()
/*     */   {
/* 155 */     return GetPort();
/*     */   }

/*     */   public boolean freePort(int nPort)
/*     */   {
/* 167 */     if (FreePort(nPort) == 0) {
/* 168 */       return false;
/*     */     }
/*     */     
/* 171 */     return true;
/*     */   }

/*     */   public boolean openFile(int nPort, String filePath)
/*     */   {
/* 184 */     byte[] newPath = null;

/* 187 */     if (filePath != null) {
/* 188 */       byte[] path = filePath.getBytes();
/*     */       
/* 190 */       if (path == null) {
/* 191 */         return false;
/*     */       }
/*     */       try
/*     */       {
/* 195 */         newPath = new byte[path.length + 1];
/*     */       } catch (Exception e) {
/* 197 */         e.printStackTrace();
/*     */       }
/*     */       
/* 200 */       for (int i = 0; i < path.length; i++) {
/* 201 */         newPath[i] = path[i];
/*     */       }
/*     */       
/* 204 */       newPath[(newPath.length - 1)] = 0;
/*     */     }
/*     */     
/* 207 */     if (OpenFile(nPort, newPath) == 0) {
/* 208 */       return false;
/*     */     }
/*     */     
/* 211 */     return true;
/*     */   }

/*     */   public boolean closeFile(int nPort)
/*     */   {
/* 223 */     if (CloseFile(nPort) == 0) {
/* 224 */       return false;
/*     */     }
/*     */     
/* 227 */     return true;
/*     */   }

/*     */   public boolean play(int nPort, SurfaceHolder holder)
/*     */   {
/* 239 */     Surface surface = null;
/*     */     
/* 241 */     if (holder != null) {
/* 242 */       surface = holder.getSurface();
/*     */       
/* 244 */       if (surface == null) {
/* 245 */         return false;
/*     */       }
/*     */       
/* 248 */       if (!surface.isValid()) {
/* 249 */         Log.e("PlayerSDK", "Surface Invalid!");
/* 250 */         return false;
/*     */       }
/*     */     }
/*     */     
/* 254 */     if (Play(nPort, surface) == 0) {
/* 255 */       Log.e("PlayerSDK", "Play false!");
/* 256 */       return false;
/*     */     }
/*     */     
/* 259 */     return true;
/*     */   }

/*     */   public boolean stop(int nPort)
/*     */   {
/* 270 */     if (Stop(nPort) == 0) {
/* 271 */       return false;
/*     */     }
/*     */     
/* 274 */     return true;
/*     */   }

/*     */   public boolean pause(int nPort, int nPause)
/*     */   {
/* 286 */     if (Pause(nPort, nPause) == 0) {
/* 287 */       return false;
/*     */     }
/*     */     
/* 290 */     return true;
/*     */   }

/*     */   public int getSdkVersion()
/*     */   {
/* 300 */     return GetSdkVersion();
/*     */   }

/*     */   public int getLastError(int nPort)
/*     */   {
/* 311 */     return GetLastError(nPort);
/*     */   }

/*     */   public boolean openStream(int nPort, byte[] pFileHeadBuf, int nSize, int nBufPoolSize)
/*     */   {
/* 326 */     if (OpenStream(nPort, pFileHeadBuf, nSize, nBufPoolSize) == 0) {
/* 327 */       return false;
/*     */     }
/*     */     
/* 330 */     return true;
/*     */   }

/*     */   public boolean closeStream(int nPort)
/*     */   {
/* 341 */     if (CloseStream(nPort) == 0) {
/* 342 */       return false;
/*     */     }
/*     */     
/* 345 */     return true;
/*     */   }

/*     */   public boolean setSecretKey(int nPort, int nKeyType, byte[] pSecretKey, int nKeyLen)
/*     */   {
/* 360 */     if (SetSecretKey(nPort, nKeyType, pSecretKey, nKeyLen) == 0) {
/* 361 */       return false;
/*     */     }
/*     */     
/* 364 */     return true;
/*     */   }

/*     */   public boolean setVideoWindow(int nPort, int nRegionNum, SurfaceHolder holder)
/*     */   {
/* 378 */     Surface surface = null;
/*     */     
/* 380 */     if (holder != null) {
/* 381 */       surface = holder.getSurface();
/*     */       
/* 383 */       if (surface == null) {
/* 384 */         return false;
/*     */       }
/*     */       
/* 387 */       if (!surface.isValid()) {
/* 388 */         Log.e("PlayerSDK", "Surface Invalid!");
/* 389 */         return false;
/*     */       }
/*     */     }
/*     */     
/* 393 */     if (SetVideoWindow(nPort, nRegionNum, surface) == 0) {
/* 394 */       return false;
/*     */     }
/*     */     
/* 397 */     return true;
/*     */   }

/*     */   public boolean inputData(int nPort, byte[] pBuf, int nSize)
/*     */   {
/* 411 */     if (InputData(nPort, pBuf, nSize) == 0) {
/* 412 */       return false;
/*     */     }
/* 414 */     return true;
/*     */   }

/*     */   public boolean playSound(int nPort)
/*     */   {
/* 426 */     if (PlaySound(nPort) == 0) {
/* 427 */       return false;
/*     */     }
/*     */     
/* 430 */     return true;
/*     */   }

/*     */   public boolean stopSound()
/*     */   {
/* 441 */     if (StopSound() == 0) {
/* 442 */       return false;
/*     */     }
/*     */     
/* 445 */     return true;
/*     */   }

/*     */   public boolean setStreamOpenMode(int nPort, int nMode)
/*     */   {
/* 459 */     if (SetStreamOpenMode(nPort, nMode) == 0) {
/* 460 */       return false;
/*     */     }
/*     */     
/* 463 */     return true;
/*     */   }

/*     */   public int getCurrentFrameRate(int nPort)
/*     */   {
/* 475 */     return GetCurrentFrameRate(nPort);
/*     */   }

/*     */   public boolean getBMP(int nPort, byte[] pBitmap, int nBufSize, MPInteger stSize)
/*     */   {
/* 491 */     if (GetBMP(nPort, pBitmap, nBufSize, stSize) == 0) {
/* 492 */       return false;
/*     */     }
/*     */     
/* 495 */     return true;
/*     */   }

/*     */   public boolean getJPEG(int nPort, byte[] pJpeg, int nBufSize, MPInteger stSize)
/*     */   {
/* 510 */     if (GetJPEG(nPort, pJpeg, nBufSize, stSize) == 0) {
/* 511 */       return false;
/*     */     }
/*     */     
/* 514 */     return true;
/*     */   }

/*     */   public boolean getPictureSize(int nPort, MPInteger stWidth, MPInteger stHeight)
/*     */   {
/* 528 */     if (GetPictureSize(nPort, stWidth, stHeight) == 0) {
/* 529 */       return false;
/*     */     }
/*     */     
/* 532 */     return true;
/*     */   }

/*     */   public boolean fast(int nPort)
/*     */   {
/* 544 */     if (Fast(nPort) == 0) {
/* 545 */       return false;
/*     */     }
/*     */     
/* 548 */     return true;
/*     */   }

/*     */   public boolean slow(int nPort)
/*     */   {
/* 560 */     if (Slow(nPort) == 0) {
/* 561 */       return false;
/*     */     }
/*     */     
/* 564 */     return true;
/*     */   }

/*     */   public long getFileTime(int nPort)
/*     */   {
/* 575 */     return GetFileTime(nPort);
/*     */   }

/*     */   public int getFileTotalFrames(int nPort)
/*     */   {
/* 586 */     return GetFileTotalFrames(nPort);
/*     */   }

/*     */   public float getPlayPos(int nPort)
/*     */   {
/* 597 */     return GetPlayPos(nPort);
/*     */   }

/*     */   public boolean setPlayPos(int nPort, float fRelativePos)
/*     */   {
/* 610 */     if (SetPlayPos(nPort, fRelativePos) == 0) {
/* 611 */       return false;
/*     */     }
/*     */     
/* 614 */     return true;
/*     */   }

/*     */   public int getPlayedTime(int nPort)
/*     */   {
/* 625 */     return GetPlayedTime(nPort);
/*     */   }

/*     */   public int getPlayedTimeEx(int nPort)
/*     */   {
/* 636 */     return GetPlayedTimeEx(nPort);
/*     */   }

/*     */   public boolean setPlayedTimeEx(int nPort, int nTime)
/*     */   {
/* 649 */     if (nTime < 0) {
/* 650 */       Log.e("PlayerSDK", "nTime less than 0!");
/* 651 */       return false;
/*     */     }
/*     */     
/* 654 */     if (SetPlayedTimeEx(nPort, nTime) == 0) {
/* 655 */       return false;
/*     */     }
/*     */     
/* 658 */     return true;
/*     */   }

/*     */   public int getPlayedFrames(int nPort)
/*     */   {
/* 669 */     return GetPlayedFrames(nPort);
/*     */   }

/*     */   public int getCurrentFrameNum(int nPort)
/*     */   {
/* 680 */     return GetCurrentFrameNum(nPort);
/*     */   }

/*     */   public boolean setCurrentFrameNum(int nPort, int nFrameNum)
/*     */   {
/* 693 */     if (SetCurrentFrameNum(nPort, nFrameNum) == 0) {
/* 694 */       return false;
/*     */     }
/*     */     
/* 697 */     return true;
/*     */   }

/*     */   public int getSourceBufferRemain(int nPort)
/*     */   {
/* 708 */     return GetSourceBufferRemain(nPort);
/*     */   }

/*     */   public boolean resetSourceBuffer(int nPort)
/*     */   {
/* 720 */     if (ResetSourceBuffer(nPort) == 0) {
/* 721 */       return false;
/*     */     }
/*     */     
/* 724 */     return true;
/*     */   }

/*     */   public int getDisplayBuf(int nPort)
/*     */   {
/* 735 */     return GetDisplayBuf(nPort);
/*     */   }

/*     */   public boolean setDisplayBuf(int nPort, int nNum)
/*     */   {
/* 748 */     if (SetDisplayBuf(nPort, nNum) == 0) {
/* 749 */       return false;
/*     */     }
/*     */     
/* 752 */     return true;
/*     */   }

/*     */   public boolean setDisplayRegion(int nPort, int nRegionNum, MPRect stSrcRect, SurfaceHolder holder, int bEnable)
/*     */   {
/* 767 */     Surface surface = null;
/*     */     
/* 769 */     if (holder != null) {
/* 770 */       surface = holder.getSurface();
/*     */       
/* 772 */       if (surface == null) {
/* 773 */         return false;
/*     */       }
/*     */       
/* 776 */       if (!surface.isValid()) {
/* 777 */         Log.e("PlayerSDK", "Surface Invalid!");
/* 778 */         return false;
/*     */       }
/*     */     }
/*     */     
/* 782 */     if (SetDisplayRegion(nPort, nRegionNum, stSrcRect, surface, bEnable) == 0) {
/* 783 */       return false;
/*     */     }
/*     */     
/* 786 */     return true;
/*     */   }

/*     */   public boolean resetBuffer(int nPort, int nBufType)
/*     */   {
/* 799 */     if (ResetBuffer(nPort, nBufType) == 0) {
/* 800 */       return false;
/*     */     }
/*     */     
/* 803 */     return true;
/*     */   }

/*     */   public int getBufferValue(int nPort, int nBufType)
/*     */   {
/* 815 */     return GetBufferValue(nPort, nBufType);
/*     */   }

/*     */   public boolean setDecodeFrameType(int nPort, int nFrameType)
/*     */   {
/* 828 */     if (SetDecodeFrameType(nPort, nFrameType) == 0) {
/* 829 */       return false;
/*     */     }
/*     */     
/* 832 */     return true;
/*     */   }

/*     */   public boolean getSystemTime(int nPort, MPSystemTime stSystemTime)
/*     */   {
/* 845 */     if (GetSystemTime(nPort, stSystemTime) == 0) {
/* 846 */       return false;
/*     */     }
/*     */     
/* 849 */     return true;
/*     */   }

/*     */   public boolean setFileEndCB(int nPort, PlayerCallBack.PlayerPlayEndCB playEndCB)
/*     */   {
/* 863 */     if (SetFileEndCallback(nPort, playEndCB) == 0) {
/* 864 */       return false;
/*     */     }
/*     */     
/* 867 */     return true;
/*     */   }

/*     */   public boolean setDisplayCB(int nPort, PlayerCallBack.PlayerDisplayCB displayCB)
/*     */   {
/* 880 */     if (SetDisplayCallback(nPort, displayCB) == 0) {
/* 881 */       return false;
/*     */     }
/*     */     
/* 884 */     return true;
/*     */   }

/*     */   public boolean setDecodeCB(int nPort, PlayerCallBack.PlayerDecodeCB decodeCB)
/*     */   {
/* 897 */     if (SetDecodeCallback(nPort, decodeCB) == 0) {
/* 898 */       return false;
/*     */     }
/*     */     
/* 901 */     return true;
/*     */   }

/*     */   public boolean setFileRefCB(int nPort, PlayerCallBack.PlayerFileRefCB fileRefCB)
/*     */   {
/* 914 */     if (SetFileRefCallBack(nPort, fileRefCB) == 0) {
/* 915 */       return false;
/*     */     }
/*     */     
/* 918 */     return true;
/*     */   }
/*     */   
/*     */   public boolean verticalFlip(int nPort, int nFlag) {
/* 922 */     return verticalFlip(nPort, nFlag);
/*     */   }
/*     */   
/*     */   public boolean setImageCorrection(int nPort, int bEnable) {
/* 926 */     if (SetImageCorrection(nPort, bEnable) == 0) {
/* 927 */       return false;
/*     */     }
/*     */     
/* 930 */     return true;
/*     */   }

/*     */   public static final int VOLUME_MAX = 65535;

/*     */   public static final int VOLUME_DEFAULT = 32767;

/*     */   public static final int VOLUME_MUTE = 0;

/*     */   public static final int MAX_PORT = 16;

/*     */   public static final int MAX_REGION_NUM = 4;

/*     */   public static final int PLAYM4_OK = 1;

/*     */   public static final int PLAYM4_FAIL = 0;

/*     */   static
/*     */   {
/* 988 */     System.loadLibrary("CpuFeatures");
/*     */   }
/*     */   
/*     */   private native int GetPort();
/*     */   
/*     */   private native int FreePort(int paramInt);
/*     */   
/*     */   private native int OpenFile(int paramInt, byte[] paramArrayOfByte);
/*     */   
/*     */   private native int CloseFile(int paramInt);
/*     */   
/*     */   private native int GetLastError(int paramInt);
/*     */   
/*     */   private native int GetSdkVersion();
/*     */   
/*     */   private native int Play(int paramInt, Surface paramSurface);
/*     */   
/*     */   private native int Stop(int paramInt);
/*     */   
/*     */   private native int Pause(int paramInt1, int paramInt2);
/*     */   
/*     */   private native int Fast(int paramInt);
/*     */   
/*     */   private native int Slow(int paramInt);
/*     */   
/*     */   private native int PlaySound(int paramInt);
/*     */   
/*     */   private native int StopSound();
/*     */   
/*     */   private native int SetStreamOpenMode(int paramInt1, int paramInt2);
/*     */   
/*     */   private native int OpenStream(int paramInt1, byte[] paramArrayOfByte, int paramInt2, int paramInt3);
/*     */   
/*     */   private native int CloseStream(int paramInt);
/*     */   
/*     */   private native int InputData(int paramInt1, byte[] paramArrayOfByte, int paramInt2);
/*     */   
/*     */   private native long GetFileTime(int paramInt);
/*     */   
/*     */   private native int GetFileTotalFrames(int paramInt);
/*     */   
/*     */   private native float GetPlayPos(int paramInt);
/*     */   
/*     */   private native int SetPlayPos(int paramInt, float paramFloat);
/*     */   
/*     */   private native int GetPlayedTime(int paramInt);
/*     */   
/*     */   private native int GetPlayedTimeEx(int paramInt);
/*     */   
/*     */   private native int SetPlayedTimeEx(int paramInt1, int paramInt2);
/*     */   
/*     */   private native int GetPlayedFrames(int paramInt);
/*     */   
/*     */   private native int GetCurrentFrameNum(int paramInt);
/*     */   
/*     */   private native int SetCurrentFrameNum(int paramInt1, int paramInt2);
/*     */   
/*     */   private native int SetVideoWindow(int paramInt1, int paramInt2, Surface paramSurface);
/*     */   
/*     */   private native int GetCurrentFrameRate(int paramInt);
/*     */   
/*     */   private native int GetPictureSize(int paramInt, MPInteger paramMPInteger1, MPInteger paramMPInteger2);
/*     */   
/*     */   private native int GetSourceBufferRemain(int paramInt);
/*     */   
/*     */   private native int ResetSourceBuffer(int paramInt);
/*     */   
/*     */   private native int ResetSourceBufFlag(int paramInt);
/*     */   
/*     */   private native int GetDisplayBuf(int paramInt);
/*     */   
/*     */   private native int SetDisplayBuf(int paramInt1, int paramInt2);
/*     */   
/*     */   private native int SetDisplayRegion(int paramInt1, int paramInt2, MPRect paramMPRect, Surface paramSurface, int paramInt3);
/*     */   
/*     */   private native int ResetBuffer(int paramInt1, int paramInt2);
/*     */   
/*     */   private native int GetBufferValue(int paramInt1, int paramInt2);
/*     */   
/*     */   private native int SetDecodeFrameType(int paramInt1, int paramInt2);
/*     */   
/*     */   private native int GetBMP(int paramInt1, byte[] paramArrayOfByte, int paramInt2, MPInteger paramMPInteger);
/*     */   
/*     */   private native int GetJPEG(int paramInt1, byte[] paramArrayOfByte, int paramInt2, MPInteger paramMPInteger);
/*     */   
/*     */   private native int SetFileEndCallback(int paramInt, PlayerCallBack.PlayerPlayEndCB paramPlayerPlayEndCB);
/*     */   
/*     */   private native int SetDisplayCallback(int paramInt, PlayerCallBack.PlayerDisplayCB paramPlayerDisplayCB);
/*     */   
/*     */   private native int SetDecodeCallback(int paramInt, PlayerCallBack.PlayerDecodeCB paramPlayerDecodeCB);
/*     */   
/*     */   private native int SetSecretKey(int paramInt1, int paramInt2, byte[] paramArrayOfByte, int paramInt3);
/*     */   
/*     */   private native int GetSystemTime(int paramInt, MPSystemTime paramMPSystemTime);
/*     */   
/*     */   private native int GetCpuFeatures();
/*     */   
/*     */   private native int SetFileRefCallBack(int paramInt, PlayerCallBack.PlayerFileRefCB paramPlayerFileRefCB);
/*     */   
/*     */   private native int VerticalFlip(int paramInt1, int paramInt2);
/*     */   
/*     */   private native void SetAndroidSDKVersion(int paramInt);
/*     */   
/*     */   private native int SetImageCorrection(int paramInt1, int paramInt2);
/*     */ }