package com.org.MediaPlayer.PlayM4;

import android.graphics.Rect;
import android.os.Build.VERSION;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import java.util.HashMap;
import java.util.Map;
import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;

public class Display {
	private final int EGL_CONTEXT_CLIENT_VERSION = 12440;
	private static int EGL_OPENGL_ES2_BIT = 4;
	private static int EGL_BACK_BUFFER = 12420;
	private final String TAG = "Display";
	private final int EGL_DISPLAY = 0;
	private final int EGL_SURFACE = 1;
	private final int EGL_CONTEXT = 2;
	private SurfaceHolder mHolder = null;
	private Surface mViewSurface = null;
	private EGLDisplay mDisplay = null;
	private EGLConfig mConfig = null;
	private EGLContext mContext = null;
	private EGLSurface mSurface = null;
	private EGLContext mCurrentContext = null;
	private int mSurfaceChanged = 0;
	private int mContextReleased = 1;
	private static Map<String, String> mEglError = new HashMap() {
	};
	static int[] s_windowAttribs = { 12422, EGL_BACK_BUFFER, 12344 };
	private static int[] s_configAttribs = { 12339, 4, 12352,
			EGL_OPENGL_ES2_BIT, 12344 };

	public Display(SurfaceHolder holder) {
		this.mHolder = holder;
		this.mViewSurface = null;
	}

	public Display() {
	}

	public int updateSurface(SurfaceHolder holder) {
		int ret = 1;

		if (this.mHolder == holder) {
			return ret;
		}

		this.mSurfaceChanged = 1;

		this.mHolder = holder;

		return ret;
	}

	public int contextChanged() {
		return 1;
	}

	public int surfaceChanged() {
		return this.mSurfaceChanged;
	}

	public int getAPILevel() {
		return 1;
	}

	public int getSurfaceSize(int sizeType) {
		if (this.mHolder == null) {
			throw new IllegalArgumentException("SURFACE HOLDER NONE!");
		}

		Rect rect = this.mHolder.getSurfaceFrame();

		switch (sizeType) {
		case 0:
			return rect.width();
		case 1:
			return rect.height();
		}

		throw new IllegalArgumentException("UNSUPPORT SIZE VALUE TYPE!");
	}

	private void checkEglError(String methodName) {
		int errCode = eglGetError();

		if (12288 != errCode)
			Log.e("Display",
					methodName
							+ ": error "
							+ Integer.toHexString(errCode)
							+ " ("
							+ ((String) mEglError.get(Integer
									.toHexString(errCode))) + ")");
	}

	public Object getEglValue(int valueType) {
		switch (valueType) {
		case 0:
			return this.mDisplay;
		case 1:
			return this.mSurface;
		case 2:
			return this.mContext;
		}
		throw new IllegalArgumentException("UNSUPPORT EGL VALUE TYPE!");
	}

	public Surface getSurface() {
		if (this.mHolder != null)
			return this.mHolder.getSurface();

		return this.mViewSurface;
	}

	public int eglGetError() {
		EGL10 egl = (EGL10) EGLContext.getEGL();
		return egl.eglGetError();
	}

	public int eglCreateSurface() {
		EGL10 egl = (EGL10) EGLContext.getEGL();
		Log.i("Display", "Surface " + this.mHolder.getSurface().isValid());

		this.mSurface = egl.eglCreateWindowSurface(this.mDisplay, this.mConfig,
				this.mHolder, s_windowAttribs);
		checkEglError("eglCreateWindowSurface");

		return 1;
	}

	public int eglDestroySurface() {
		if (this.mSurface == null)
			return 0; 

		EGL10 egl = (EGL10) EGLContext.getEGL();
		egl.eglDestroySurface(this.mDisplay, this.mSurface);
		checkEglError("eglDestroySurface");
		this.mSurfaceChanged = 0;
		this.mSurface = null;
		return 1;
	}

	public int eglSwapBuffers() {
		if ((this.mSurface == null) && (this.mDisplay == null)) {
			return 0;
		}

		EGL10 egl = (EGL10) EGLContext.getEGL();

		int ret = 0;
		if (egl.eglSwapBuffers(this.mDisplay, this.mSurface))
			ret = 1;
		else {
			ret = 0;
		}

		checkEglError("eglSwapBuffers");
		return ret;
	}

	public int eglMakeCurrent() {
		long begin = System.currentTimeMillis();
		if ((this.mSurface == null) && (this.mDisplay == null)
				&& (this.mContext == null))
			return 0;

		EGL10 egl = (EGL10) EGLContext.getEGL();

		int ret = 0;
		if (egl.eglMakeCurrent(this.mDisplay, this.mSurface, this.mSurface,
				this.mContext)) {
			this.mContextReleased = 0;
			ret = 1;
		} else {
			ret = 0;
		}
		checkEglError("eglMakeCurrent");
		long time = System.currentTimeMillis() - begin;

		return ret;
	}

	public int eglReleaseCurrent() {
		EGL10 egl = (EGL10) EGLContext.getEGL();

		int ret = 0;
		long begin = System.currentTimeMillis();

		if (egl.eglMakeCurrent(this.mDisplay, EGL10.EGL_NO_SURFACE,
				EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_CONTEXT)) {
			this.mContextReleased = 1;
			this.mCurrentContext = null;
			ret = 1;
		} else {
			ret = 0;
		}
		long time = System.currentTimeMillis() - begin;

		checkEglError("eglReleaseCurrent");
		return ret;
	}

	public int eglDeInit() {
		Log.d("PlayerSDK >>>", "Stop rendering");
		EGL10 egl = (EGL10) EGLContext.getEGL();

		if ((this.mDisplay != null) && (this.mContext != null)) {
			egl.eglDestroyContext(this.mDisplay, this.mContext);
			checkEglError("eglDeInit:eglDestroyContext");
			this.mContext = null;
		}

		if ((this.mDisplay != null) && (this.mSurface != null)) {
			egl.eglDestroySurface(this.mDisplay, this.mSurface);
			checkEglError("eglDeInit:eglDestroySurface");
			this.mSurface = null;
		}

		if (this.mDisplay != null) {
			egl.eglTerminate(this.mDisplay);
			checkEglError("eglDeInit:eglTerminate");
			this.mDisplay = null;
		}

		return 1;
	}

	public int eglInit() {
		Log.d("PlayerSDK >>>", "Start rendering");
		EGL10 egl = (EGL10) EGLContext.getEGL();

		this.mDisplay = egl.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);
		checkEglError("eglGetDisplay");

		egl.eglInitialize(this.mDisplay, new int[2]);
		checkEglError("eglInitialize");

		int[] num_config = new int[1];
		egl.eglChooseConfig(this.mDisplay, s_configAttribs, null, 0, num_config);
		checkEglError("eglChooseConfig");

		EGLConfig[] configs = new EGLConfig[num_config[0]];

		egl.eglChooseConfig(this.mDisplay, s_configAttribs, configs,
				num_config[0], num_config);
		checkEglError("eglChooseConfig");
		this.mConfig = configs[0];
		int[] value = new int[1];

		int a = 0;
		for (EGLConfig eglConfig : configs) {
			if (getAPILevel() < 9) {
				Log.i("Display", "APILEVEL: 8");
				break;
			}
			egl.eglGetConfigAttrib(this.mDisplay, eglConfig, 12321, value);
			checkEglError("eglGetConfigAttrib");
			a = value[0];

			if (a == 8) {
				this.mConfig = eglConfig;
				break;
			}
		}

		this.mContext = egl.eglCreateContext(this.mDisplay, this.mConfig,
				EGL10.EGL_NO_CONTEXT, new int[] { 12440, 2, 12344 });
		checkEglError("eglCreateContext");
		return 1;
	}
}