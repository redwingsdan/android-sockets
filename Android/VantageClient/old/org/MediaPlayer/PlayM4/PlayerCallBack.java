package com.org.MediaPlayer.PlayM4;

public class PlayerCallBack
{
  public static abstract interface PlayerDecodeCB
  {
    public abstract void onDecode(int paramInt1, byte[] paramArrayOfByte, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7);
  }
  
  public static abstract interface PlayerDisplayCB
  {
    public abstract void onDisplay(int paramInt1, byte[] paramArrayOfByte, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7);
  }
  
  public static abstract interface PlayerFileRefCB
  {
    public abstract void onFileRefDone(int paramInt);
  }
  
  public static abstract interface PlayerPlayEndCB
  {
    public abstract void onPlayEnd(int paramInt);
  }
}


/* Location:              C:\Users\jburmeister\Desktop\SimpleDemo\PlayerSDK.jar!\org\MediaPlayer\PlayM4\PlayerCallBack.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */