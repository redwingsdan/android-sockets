package com.vantageclient.activity;

import java.net.InetAddress;

import com.android.SimplePlayer.SimplePlayer;
import com.vantageclient.data.ByteUtils;
import com.vantageclient.net.CGIHelper;
import com.vantageclient.net.IMjpegCallBack;
import com.vantageclient.net.StreamReader;
import com.vantageclient.net.TcpCgiHelper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ProgressBar;

public class CameraView extends LiveViewFragment {

    private SimplePlayer _player = null;
    private TcpCgiHelper _cgihelper = null;

    private SurfaceView _view = null;

    private ProgressBar _progressBar = null;

    private ShowPreview _preview = null;
    private StreamReader _reader = null;
    private SurfaceHolder _surfaceHolder;

    private String _session;
    private String _serverIp;
    private int _serverPort;
    private int channelNumber;

    public boolean start() {

        paint = new Paint();
        try {
            _progressBar.setVisibility(View.VISIBLE);
            _preview = new ShowPreview();
            _preview.execute();
        } catch (Exception e) {
            _preview.cancel(true);
            getActivity().finish();
        }
        return true;
    }

    public boolean setValues(SurfaceHolder holder, ProgressBar bar, int position, String _IP, int _Port, String _Session) {
        _surfaceHolder = holder;
        _progressBar = bar;
        channelNumber = position;
        _serverIp = _IP;
        _serverPort = _Port;
        _session = _Session;
        return true;
    }

    public boolean setProgressBar(ProgressBar bar) {
        _progressBar = bar;
        return true;
    }

    private void initPlayer() {
        try {
            String[] info = CGIHelper.getCameraStreamString(_serverIp,
                    _serverPort, _session, channelNumber).split("\r\n");

            boolean canContinue = true;
            if (info.length < 2) {
                //finish();
                //	return;
                canContinue = false;
            }

            if (canContinue) {
                int streamPort;
                byte[] headerBytes;
                if (info.length > 4) {
                    if ("ListenPort".equalsIgnoreCase(info[2].substring(0, 10))) {
                        streamPort = Integer.parseInt(info[2].substring(11).trim());
                        headerBytes = ByteUtils.hexToBytes(info[3].substring(11));
                    } else {
                        streamPort = Integer.parseInt(info[5].substring(11).trim());
                        headerBytes = ByteUtils.hexToBytes(info[6].substring(11));
                    }
                } else {
                    streamPort = Integer.parseInt(info[2].substring(11).trim());
                    headerBytes = new byte[0];
                }

                _reader = new StreamReader(InetAddress.getByName(_serverIp),
                        streamPort, channelNumber, headerBytes, new CallBackClass(), 1);
                _reader.setSurfaceHolder(_surfaceHolder);
                _reader.start();
            }

            _cgihelper = new TcpCgiHelper(_serverIp, 5100);
            _cgihelper.init();

        } catch (Exception e) {
            Log.e("CameraView", e.getMessage());
            getActivity().finish();
        }
    }

    class CallBackClass implements IMjpegCallBack {
        @Override
        public void mjpegDataReceived(Bitmap image, int viewNumber) {
            if (image != null)
                drawCanvas(image, viewNumber);
        }
    }

    public void drawCanvas(Bitmap bitmap, int viewNumber) {

        if (bitmap.getWidth() < 0 || bitmap.getHeight() < 0)
            return;

        SurfaceHolder thisSurface = this._surfaceHolder;

        if (thisSurface != null) {
            Canvas canvas = thisSurface.lockCanvas();

            destRect = destRect(bitmap.getWidth(), bitmap.getHeight(), viewNumber);
            canvas.drawColor(Color.BLACK);
            canvas.drawBitmap(bitmap, null, destRect, paint);
            thisSurface.unlockCanvasAndPost(canvas);
        }
    }

    Paint paint;
    Rect destRect;

    private Rect destRect(int bmw, int bmh, int viewNumber) {

        SurfaceView thisView = this._view;

        if (thisView != null) {
            return new Rect(thisView.getLeft(), thisView.getTop(), thisView.getRight(), thisView.getBottom());

        }
        return null;
        //return null;
    }

    private class ShowPreview extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... arg0) {
            initPlayer();
            return null;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();

            try {
                if (_reader != null) {
                    _reader.stop();
                }
            } catch (Exception e) {
            }

            try {
                if (_player != null) {
                    _player.playerStop(1);
                    _player.playerRelease();
                }
                if (_cgihelper != null) {
                    _cgihelper.destroy();
                }
            } catch (Exception e) {

            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            try {
                _progressBar.setVisibility(View.INVISIBLE);
            } catch (Exception e) {
                Log.e("CameraView", e.getMessage());
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();


    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            _reader.stop();
        } catch (Exception e) {
        }
        getActivity().finish();
    }

    public void ptzCommand(String sessionID, int channel, String command) {

        _cgihelper.ptzCommand(sessionID, channel, command);

    }

    public void closeChannel() {
        try {
            if (_reader != null) {
                _reader.stop();
            }
        } catch (Exception e) {
        }

        try {
            if (_player != null) {
                _player.playerStop(1);
                _player.playerRelease();
            }
            if (_cgihelper != null) {
                _cgihelper.destroy();
            }
        } catch (Exception e) {

        }

    }
}
