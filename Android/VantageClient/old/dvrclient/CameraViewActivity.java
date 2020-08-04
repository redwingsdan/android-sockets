package com.vantageclient.dvrclient;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Timer;
import java.util.TimerTask;

import com.android.SimplePlayer.SimplePlayer;
import com.vantageclient.R;
import com.vantageclient.data.ByteUtils;
import com.vantageclient.data.MVCApplication;
import com.vantageclient.net.CGIHelper;
import com.vantageclient.net.IMjpegCallBack;
import com.vantageclient.net.StreamReader;
import com.vantageclient.net.TcpCgiHelper;

//import android.R;
import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.FloatMath;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.widget.LinearLayout;
import android.widget.Button;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class CameraViewActivity extends Activity implements
		OnSeekBarChangeListener, OnTouchListener {

	private static final String TILT_UP = "Up";
	private static final String TILT_DOWN = "Down";
	private static final String TURN_RIGHT = "Right";
	private static final String TURN_LEFT = "Left";
	private static final String LEFT_UP = "LeftUp";
	private static final String RIGHT_UP = "RightUp";
	private static final String LEFT_DOWN = "LeftDown";
	private static final String RIGHT_DOWN = "RightDown";
	private static final String STOP = "StopAction";
	private static final String FOCUS_NEAR = "FocusNear";
	private static final String FOCUS_FAR = "FocusFar";
	private static final String IRIS_OPEN = "IrisOpen";
	private static final String IRIS_CLOSE = "IrisClose";

	private float _oldX = 0f;
	private float _oldY = 0f;
	private float _oldDist = 0f;

	private boolean _isZoom = false;
	private boolean _isPan = false;
	private boolean _exit = false;

	
	private int zoomState = -1;
	private String _currentCommand = "";

	private SimplePlayer _player = null;
	private TcpCgiHelper _cgihelper = null;

	private SurfaceView _view = null;
	///
	private SurfaceView _view2 = null;
	private SurfaceView _view3 = null;
	private SurfaceView _view4 = null;
	
	private SeekBar _seekBar = null;
	private ProgressBar _progressBar = null;

	private ShowPreview _preview = null;
	private StreamReader _reader = null;
	private SurfaceHolder _surfaceHolder = null;
	
	
	
	///
	private StreamReader _reader2 = null;
	private SurfaceHolder _surfaceHolder2 = null;
	private StreamReader _reader3 = null;
	private SurfaceHolder _surfaceHolder3 = null;
	private StreamReader _reader4 = null;
	private SurfaceHolder _surfaceHolder4 = null;
	
	
	
	private int[] cameraNumbers = null;

	

	private String _session;
	private String _serverIp;
	private int _channel;
	private int _serverPort;
	private int NumberOfTotalCameras = 0;

	private Button smallerbiggerbutton;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		initUI();
	}

	private void initUI() {
		setContentView(R.layout.dvr_window_cameraview2);

		ButtonTouchListener listener = new ButtonTouchListener();
		((Button) findViewById(R.id.btn_focus_up)).setOnTouchListener(listener);
		((Button) findViewById(R.id.btn_focus_down))
				.setOnTouchListener(listener);
		((Button) findViewById(R.id.dvr_iris_up)).setOnTouchListener(listener);
		((Button) findViewById(R.id.dvr_iris_down))
				.setOnTouchListener(listener);
		//((Button) findViewById(R.id.ptzrefresh))
	//	.setOnTouchListener(listener);

		smallerbiggerbutton = (Button)findViewById(R.id.BiggerSmallerButton);
		smallerbiggerbutton.setOnTouchListener(listener);
		smallerbiggerbutton.setText("<");
		 
		_view = (SurfaceView) findViewById(R.id.SurfaceView01);
		_view.setOnTouchListener(this);
		
		//
		_view2 = (SurfaceView) findViewById(R.id.SurfaceView02);
		_view2.setOnTouchListener(this);
		_view3 = (SurfaceView) findViewById(R.id.SurfaceView03);
		_view3.setOnTouchListener(this);
		_view4 = (SurfaceView) findViewById(R.id.SurfaceView04);
		_view4.setOnTouchListener(this);
		
		_view.setOnLongClickListener(new OnLongClickListener() { 
	        @Override
	        public boolean onLongClick(View v) {
	            // TODO Auto-generated method stub
	        	int a = 5;
	            return true;
	        }
	    });
		
		_seekBar = (SeekBar) findViewById(R.id.dvr_seekbar);
		_seekBar.setOnSeekBarChangeListener(this);
		_seekBar.setMax(99);

		_progressBar = (ProgressBar) findViewById(R.id.pgbLoding);

		_surfaceHolder = _view.getHolder();
		
		//
		
		_surfaceHolder2 = _view2.getHolder();
		_surfaceHolder3 = _view3.getHolder();
		_surfaceHolder4 = _view4.getHolder();
		
		paint = new Paint();
		displayMode = SIZE_FULLSCREEN;
		
		this.cameraNumbers = new int[4];
		cameraNumbers[0] = -1;
		cameraNumbers[1] = -1;
		cameraNumbers[2] = -1;
		cameraNumbers[3] = -1;
		
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			cameraNumbers = extras.getIntArray("CamNums");
			NumberOfTotalCameras = extras.getInt("NumberOfCams");
			
		} else {
			finish();
		}
		
		if(cameraNumbers[0] != -1)
		{
			_view.setVisibility(View.VISIBLE);
		}
		if(cameraNumbers[1] != -1)
		{
			_view2.setVisibility(View.VISIBLE);
		}
		if(cameraNumbers[2] != -1)
		{
			_view3.setVisibility(View.VISIBLE);
		}
		if(cameraNumbers[3] != -1)
		{
			_view4.setVisibility(View.VISIBLE);
		}
		if(NumberOfTotalCameras == 1)
		{
			makeFullScreen(1);
		}
		
		
	}

	private void initPlayer() {
		try {
			
		
			
			_serverIp = ((MVCApplication) getApplicationContext())
					.getServerIp();
			_serverPort = ((MVCApplication) getApplicationContext())
					.getServerPort();
			_session = ((MVCApplication) getApplicationContext()).getSession();

		
			//Channel1
			if(cameraNumbers[0] != -1)
			{
				String[] info = CGIHelper.getCameraStreamString(_serverIp,
						_serverPort, _session, cameraNumbers[0] ).split("\r\n");
				
				boolean canContinue = true;
				if (info.length < 2) {
					//finish();
				//	return;
					canContinue = false;
				}
				
				if(canContinue)
				{
					int streamPort = 5100;
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
								streamPort, cameraNumbers[0], headerBytes, new CallBackClass(),1);
						_reader.setSurfaceHolder(_surfaceHolder);
						_reader.start();
				}
			}
			
			
			//Channel2
			if(cameraNumbers[1] != -1)
			{
				String[] info = CGIHelper.getCameraStreamString(_serverIp,
						_serverPort, _session, cameraNumbers[1] ).split("\r\n");
				
				boolean canContinue = true;
				if (info.length < 2) {
					//finish();
				//	return;
					canContinue = false;
				}
				
				if(canContinue)
				{
					int streamPort = 5100;
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
		
					
						_reader2 = new StreamReader(InetAddress.getByName(_serverIp),
								streamPort, cameraNumbers[1], headerBytes, new CallBackClass(),2);
						_reader2.setSurfaceHolder(_surfaceHolder2);
						_reader2.start();
				}
			}
			
			//Channel3
			if(cameraNumbers[2] != -1)
			{
				String[] info = CGIHelper.getCameraStreamString(_serverIp,
						_serverPort, _session, cameraNumbers[2] ).split("\r\n");
				
				boolean canContinue = true;
				if (info.length < 2) {
					//finish();
				//	return;
					canContinue = false;
				}
				
				if(canContinue)
				{
					int streamPort = 5100;
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
		
					
						_reader3 = new StreamReader(InetAddress.getByName(_serverIp),
								streamPort, cameraNumbers[2], headerBytes, new CallBackClass(),3);
						_reader3.setSurfaceHolder(_surfaceHolder3);
						_reader3.start();
				}
			}
			
			
			//Channel4
			if(cameraNumbers[3] != -1)
			{
				String[] info = CGIHelper.getCameraStreamString(_serverIp,
						_serverPort, _session, cameraNumbers[3] ).split("\r\n");
				
				boolean canContinue = true;
				if (info.length < 2) {
					//finish();
				//	return;
					canContinue = false;
				}
				
				if(canContinue)
				{
					int streamPort = 5100;
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
		
					
		
						_reader4 = new StreamReader(InetAddress.getByName(_serverIp),
								streamPort, cameraNumbers[3], headerBytes, new CallBackClass(),4);
						_reader4.setSurfaceHolder(_surfaceHolder4);
						_reader4.start();
						
						
				}
			}
			

			_cgihelper = new TcpCgiHelper(_serverIp, 5100);

			
			_cgihelper.init();
			//if (_cgihelper.init())
				//_cgihelper.ptzSpeed(_session, _channel, _seekBar.getProgress());

		} catch (Exception e) {
			Log.e("CameraView", e.getMessage());
			finish();
		}
	}

	  @Override
	    public void onConfigurationChanged(Configuration newConfig) {
	        super.onConfigurationChanged(newConfig);

	        // Checks the orientation of the screen for landscape and portrait
	        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
	          
	        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
	           
	        }
	    }
	  
	class CallBackClass implements IMjpegCallBack {
		@Override
		public void mjpegDataReceived(Bitmap image,int viewNumber) {
			if (image != null)
				drawCavas(image,viewNumber);
		}
	}

	public void drawCavas(Bitmap bitmap, int viewNumber) {

		if (bitmap.getWidth() < 0 || bitmap.getHeight() < 0)
			return;
		
		SurfaceHolder thisSurface = null;
		if(viewNumber ==1)
		{
			thisSurface = this._surfaceHolder;
		}
		else if(viewNumber ==2)
		{
			thisSurface = this._surfaceHolder2;
		}
		else if(viewNumber ==3)
		{
			thisSurface = this._surfaceHolder3;
	
		}
		else if(viewNumber ==4)
		{
			thisSurface = this._surfaceHolder4;
		}
		if(thisSurface != null)
		{
			Canvas cavas = thisSurface.lockCanvas();
	     
			// (_surfaceHolder) {
	
			destRect = destRect(bitmap.getWidth(), bitmap.getHeight(), viewNumber);
			cavas.drawColor(Color.BLACK);
			cavas.drawBitmap(bitmap, null, destRect, paint);
			// }
			
	
			if (cavas != null)
				thisSurface.unlockCanvasAndPost(cavas);
		}
	}

	public final static int SIZE_STANDARD = 1;
	public final static int SIZE_BEST_FIT = 4;
	public final static int SIZE_FULLSCREEN = 8;
	private Paint paint;
	private Rect destRect;
	private int dispWidth;
	private int dispHeight;
	private int displayMode;

	private Rect destRect(int bmw, int bmh,int viewNumber) {
		
		SurfaceView thisView = null;
		if(viewNumber ==1)
		{
			thisView = this._view;
		}
		else if(viewNumber ==2)
		{
			thisView = this._view2;
		}
		else if(viewNumber ==3)
		{
			thisView = this._view3;
	
		}
		else if(viewNumber ==4)
		{
			thisView = this._view4;
		}
		
		if(thisView != null)
		{
			return new  Rect(thisView.getLeft(), thisView.getTop(),thisView.getRight(),thisView.getBottom());
			
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
				if (_reader != null)
				{
					_reader.stop();
				}
			}
			catch (Exception e) 
			{
			}
			
			try {
				if (_reader2 != null)
				{
					_reader2.stop();
				}
			}
			catch (Exception e) 
			{
			}
			
			try {
				if (_reader3 != null)
				{
					_reader3.stop();
				}
			}
			catch (Exception e) 
			{
			}
			
			try {
				if (_reader4 != null)
				{
					_reader4.stop();
				}
			}
			catch (Exception e) 
			{
			}
			
			try {
				if (_player != null) {
					_player.playerStop(1);
					_player.playerRelease();
				}
				if (_cgihelper != null) {
					_cgihelper.destory();
				}
			} catch (Exception e) {

			}
		}

		@Override
		protected void onPostExecute(Boolean result) {
			try {
				_progressBar.setVisibility(-1);
			} catch (Exception e) {
				Log.e("CameraView", e.getMessage());
			}
		}
	}

	@Override
	protected void onStart() {
		super.onStart();

		try {
			_progressBar.setVisibility(1);
			_preview = new ShowPreview();
			_preview.execute();
		} catch (Exception e) {
			_preview.cancel(true);
			finish();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		try {
			_reader.stop();
		} catch (Exception e) {
		}
		
		try {
			_reader2.stop();
		} catch (Exception e) {
		}
		
		try {
			_reader3.stop();
		} catch (Exception e) {
		}
		
		try {
			_reader4.stop();
		} catch (Exception e) {
		}
		finish();
	}

	private float spacing(MotionEvent event) {
		float x = event.getX(0) - event.getX(1);
		float y = event.getY(0) - event.getY(1);
		return (float)Math.sqrt(x * x + y * y);
	}

	
	boolean heldDown = false;

	public boolean onTouch(View v, MotionEvent event) {
		
		
		if (_cgihelper == null)
			return false;
		
		int viewnumber = -1;
		switch (v.getId()) {
		case R.id.SurfaceView01:
			if(this.cameraNumbers[0] != -1)
			{
				viewnumber = 1;
				this._channel = this.cameraNumbers[0];
			}
			break;
		case R.id.SurfaceView02:
			if(this.cameraNumbers[1] != -1)
			{
				viewnumber = 2;
				this._channel = this.cameraNumbers[1];
			}
			break;
		case R.id.SurfaceView03:
			if(this.cameraNumbers[2] != -1)
			{
				viewnumber = 3;
				this._channel = this.cameraNumbers[2];
			}
			break;
		case R.id.SurfaceView04:
			if(this.cameraNumbers[3] != -1)
			{
				viewnumber = 4;
				this._channel = this.cameraNumbers[3];
			}
			break;
			
		default:
			viewnumber = -1;
			this._channel =0;
		}
		
		
		
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:
			_isPan = true;
			_oldX = event.getX();
			_oldY = event.getY();
			
			break;
		case MotionEvent.ACTION_UP:
			if (zoomState != -1 || !"".equalsIgnoreCase(_currentCommand)) {
				heldDown = false;
				_cgihelper.ptzCommand(_session, _channel, STOP);
			}
			else if (zoomState == -1 && "".equalsIgnoreCase(_currentCommand))
			{
				boolean doubletapped = false;
				if(heldDown)
				{
					if(!isFullScreen)
					{
						makeFullScreen(viewnumber); 
					}
					else
					{
						this.make4split();
					}
					//Toast.makeText(getApplicationContext(),"ENLARGE CAMERA  " + _channel, Toast.LENGTH_SHORT).show();
					heldDown = false;
					doubletapped = true;
				}
				if(!doubletapped)
				{
					heldDown = true;
				}
				
			}
			
			_currentCommand = "";
			_isZoom = false;
			zoomState = -1;
			break;
		case MotionEvent.ACTION_POINTER_UP:
			// if (zoomState != -1 || !"".equalsIgnoreCase(_currentCommand)) {
			heldDown = false;
			_cgihelper.ptzCommand(_session, _channel, STOP);
			// }
			_isPan = false;
			_isZoom = false;
			zoomState = -1;
			break;
		case MotionEvent.ACTION_POINTER_DOWN:
			_oldDist = spacing(event);
			_isPan = false;
			_isZoom = true;
			_currentCommand = "";
			break;
		case MotionEvent.ACTION_MOVE:
			if (_isZoom) {
				float newDist = spacing(event);
				if (newDist - 10 > _oldDist) {
					if (zoomState != 0) {
						zoomState = 0;
						heldDown = false;
						_cgihelper.ptzCommand(_session, _channel, "ZoomIn");
					}
				} else if (newDist + 10 < _oldDist) {
					if (zoomState != 1) {
						zoomState = 1;
						heldDown = false;
						_cgihelper.ptzCommand(_session, _channel, "ZoomOut");
					}
				}
			} else if (_isPan) {
				float x = event.getX();
				float y = event.getY();

				float disX = x - _oldX;
				float disY = y - _oldY;

				if (Math.abs(disX) > 20 || Math.abs(disY) > 20) {
					String command;

					double radians = Math.atan((disY * disY) / (disX * disX));
					double degrees = Math.toDegrees(radians);

					int degreeType = -1;
					if (degrees > 20 && degrees < 70) {
						degreeType = 1;
					} else if (degrees <= 20) {
						degreeType = 0;
					} else {
						degreeType = 2;
					}

					if (disX > 0) {
						if (degreeType == 0) {
							command = TURN_RIGHT;
						} else if (degreeType == 2) {
							command = disY < 0 ? TILT_UP : TILT_DOWN;
						} else {
							command = disY < 0 ? RIGHT_UP : RIGHT_DOWN;
						}
					} else {
						if (degreeType == 0) {
							command = TURN_LEFT;
						} else if (degreeType == 2) {
							command = disY < 0 ? TILT_UP : TILT_DOWN;
						} else {
							command = disY < 0 ? LEFT_UP : LEFT_DOWN;
						}
					}

					if (!command.equalsIgnoreCase(_currentCommand)) {
						heldDown = false;
						_cgihelper.ptzCommand(_session, _channel, command);
						_currentCommand = command;
					}
				}
				
			}
		

		}
		return true;
	}
	
	boolean isFullScreen = false;
	

	public void makeFullScreen(int viewNumber) {

		((LinearLayout)findViewById(R.id.LayoutView1)).setVisibility(View.INVISIBLE);
		((LinearLayout)findViewById(R.id.LayoutView2)).setVisibility(View.INVISIBLE);
		((LinearLayout)findViewById(R.id.LayoutView3)).setVisibility(View.INVISIBLE);
		((LinearLayout)findViewById(R.id.LayoutView4)).setVisibility(View.INVISIBLE);
		((LinearLayout)findViewById(R.id.LinearLayoutLeftCams)).setVisibility(View.INVISIBLE);
		((LinearLayout)findViewById(R.id.LinearLayoutRightCams)).setVisibility(View.INVISIBLE);
		

		
		if(viewNumber == 1)
		{
			((LinearLayout)findViewById(R.id.LinearLayoutLeftCams)).setLayoutParams(
					new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,   
                    LayoutParams.FILL_PARENT,
                    0));
			
			((LinearLayout)findViewById(R.id.LayoutView1)).setLayoutParams(
					new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,   
                    LayoutParams.FILL_PARENT,
                    0));
			((LinearLayout)findViewById(R.id.LinearLayoutLeftCams)).setVisibility(View.VISIBLE);
			
			final Handler handler = new Handler();
			Timer timer = new Timer(false);
			TimerTask timerTask = new TimerTask() {
			    @Override
			    public void run() {
			        handler.post(new Runnable() {
			            @Override
			            public void run() {
			            	
			            	isFullScreen = true;
			            	((LinearLayout)findViewById(R.id.LayoutView1)).setVisibility(View.VISIBLE);
			                // Do whatever you want
			            }
			        });
			    }
			};
			timer.schedule(timerTask, 150); // 1000 = 1 second.
			isFullScreen = true;
		}

		if(viewNumber == 2)
		{
			
			((LinearLayout)findViewById(R.id.LinearLayoutRightCams)).setLayoutParams(
					new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,   
                    LayoutParams.FILL_PARENT,
                    0));
			
			((LinearLayout)findViewById(R.id.LayoutView2)).setLayoutParams(
					new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,   
                    LayoutParams.FILL_PARENT,
                    0));
			((LinearLayout)findViewById(R.id.LinearLayoutRightCams)).setVisibility(View.VISIBLE);
			
			final Handler handler = new Handler();
			Timer timer = new Timer(false);
			TimerTask timerTask = new TimerTask() {
			    @Override
			    public void run() {
			        handler.post(new Runnable() {
			            @Override
			            public void run() {
			            	
			            	isFullScreen = true;
			            	((LinearLayout)findViewById(R.id.LayoutView2)).setVisibility(View.VISIBLE);
			                // Do whatever you want
			            }
			        });
			    }
			};
			timer.schedule(timerTask, 150); // 1000 = 1 second.

			isFullScreen = true;
		}
		if(viewNumber == 3)
		{
			((LinearLayout)findViewById(R.id.LinearLayoutLeftCams)).setLayoutParams(
					new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,   
                    LayoutParams.FILL_PARENT,
                    0));
			
			((LinearLayout)findViewById(R.id.LayoutView3)).setLayoutParams(
					new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,   
                    LayoutParams.FILL_PARENT,
                    0));
			((LinearLayout)findViewById(R.id.LinearLayoutLeftCams)).setVisibility(View.VISIBLE);
			
			final Handler handler = new Handler();
			Timer timer = new Timer(false);
			TimerTask timerTask = new TimerTask() {
			    @Override
			    public void run() {
			        handler.post(new Runnable() {
			            @Override
			            public void run() {
			            	
			            	isFullScreen = true;
			            	((LinearLayout)findViewById(R.id.LayoutView3)).setVisibility(View.VISIBLE);
			                // Do whatever you want
			            }
			        });
			    }
			};
			timer.schedule(timerTask, 150); // 1000 = 1 second.
			isFullScreen = true;
		}
		if(viewNumber == 4)
		{
			((LinearLayout)findViewById(R.id.LinearLayoutRightCams)).setLayoutParams(
					new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,   
                    LayoutParams.FILL_PARENT,
                    0));
			
			((LinearLayout)findViewById(R.id.LayoutView4)).setLayoutParams(
					new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,   
                    LayoutParams.FILL_PARENT,
                    0));
			((LinearLayout)findViewById(R.id.LinearLayoutRightCams)).setVisibility(View.VISIBLE);
			
			final Handler handler = new Handler();
			Timer timer = new Timer(false);
			TimerTask timerTask = new TimerTask() {
			    @Override
			    public void run() {
			        handler.post(new Runnable() {
			            @Override
			            public void run() {
			            	
			            	isFullScreen = true;
			            	((LinearLayout)findViewById(R.id.LayoutView4)).setVisibility(View.VISIBLE);
			                // Do whatever you want
			            }
			        });
			    }
			};
			timer.schedule(timerTask, 150); // 1000 = 1 second.
			isFullScreen = true;
		}

	}
	
	public void make4split() {
	
		if(this.NumberOfTotalCameras > 1)
		{
			((LinearLayout)findViewById(R.id.LinearLayoutLeftCams)).setVisibility(View.VISIBLE);
			((LinearLayout)findViewById(R.id.LinearLayoutLeftCams)).setLayoutParams(
					new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,   
                    LayoutParams.FILL_PARENT,
                    .5f));
			
			((LinearLayout)findViewById(R.id.LinearLayoutRightCams)).setVisibility(View.VISIBLE);
			((LinearLayout)findViewById(R.id.LinearLayoutRightCams)).setLayoutParams(
					new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,   
                    LayoutParams.FILL_PARENT,
                    .5f));
			
			((LinearLayout)findViewById(R.id.LayoutView1)).setVisibility(View.VISIBLE);
			((LinearLayout)findViewById(R.id.LayoutView1)).setLayoutParams(
					new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,   
                    LayoutParams.FILL_PARENT,
                    .5f));
			
			((LinearLayout)findViewById(R.id.LayoutView2)).setVisibility(View.VISIBLE);
			((LinearLayout)findViewById(R.id.LayoutView2)).setLayoutParams(
					new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,   
                    LayoutParams.FILL_PARENT,
                    .5f));
			((LinearLayout)findViewById(R.id.LayoutView3)).setVisibility(View.VISIBLE);
			((LinearLayout)findViewById(R.id.LayoutView3)).setLayoutParams(
					new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,   
                    LayoutParams.FILL_PARENT,
                    .5f));
			
			((LinearLayout)findViewById(R.id.LayoutView4)).setVisibility(View.VISIBLE);
			((LinearLayout)findViewById(R.id.LayoutView4)).setLayoutParams(
					new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,   
                    LayoutParams.FILL_PARENT,
                    .5f));
			
			isFullScreen = false;
			

		 
		 
		
		}
	}
	

	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {

	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {

	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		if (_cgihelper != null)
			_cgihelper.ptzSpeed(_session, _channel, seekBar.getProgress());
	}

	boolean canEnlargeOrHide = true;
	class ButtonTouchListener implements OnTouchListener {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			String command = "";

			switch (v.getId()) {
			case R.id.dvr_iris_down:
				command = IRIS_CLOSE;
				break;
			case R.id.dvr_iris_up:
				command = IRIS_OPEN;
				break;
			case R.id.btn_focus_down:
				command = FOCUS_FAR;
				break;
			case R.id.btn_focus_up:
				command = FOCUS_NEAR;
				break;
		//	case R.id.ptzrefresh:
		//		_cgihelper.destory();
		//		_cgihelper = new TcpCgiHelper(_serverIp, 5100);
		//		_cgihelper.init();
		//		break;
			case R.id.BiggerSmallerButton:
				 if(canEnlargeOrHide)
				 {
					 if(((LinearLayout)findViewById(R.id.linearLayout2)).getVisibility() == View.VISIBLE)
					 {
						 
						 ((LinearLayout)findViewById(R.id.linearLayout2)).setVisibility(View.GONE);
						 smallerbiggerbutton.setText("<");
					 }
					 else
					 {
						 ((LinearLayout)findViewById(R.id.linearLayout2)).setVisibility(View.VISIBLE);
						 smallerbiggerbutton.setText(">");
					 }
					 canEnlargeOrHide = false;
					 final Handler handler = new Handler();
						Timer timer = new Timer(false);
						TimerTask timerTask = new TimerTask() {
						    @Override
						    public void run() {
						        handler.post(new Runnable() {
						            @Override
						            public void run() {
						            	
						            	canEnlargeOrHide = true;
						                // Do whatever you want
						            }
						        });
						    }
						};
						timer.schedule(timerTask, 500); // 1000 = 1 second.
				 }
				
			}
		

			if (event.getAction() == MotionEvent.ACTION_UP)
				command = STOP;

			if (!"".equalsIgnoreCase(command) && _cgihelper != null) {
				_cgihelper.ptzCommand(_session, _channel, command);
			}
			return false;
		}
	}

}
