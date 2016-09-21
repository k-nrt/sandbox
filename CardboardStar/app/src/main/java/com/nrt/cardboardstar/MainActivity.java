package com.nrt.cardboardstar;

import java.nio.Buffer; 
import java.nio.ByteBuffer; 
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.app.Activity;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import java.util.HashMap;
import android.text.style.*;
import android.opengl.*;
import java.nio.*;
import java.util.*;
import java.util.zip.*;

import android.view.MotionEvent;
import android.util.*;
import android.nfc.tech.*;

import android.graphics.Bitmap;
//import android.graphics.*;
import java.nio.charset.*;

import android.widget.TextView;
import android.content.res.*;
//public class TryGLES2002Activity extends Activity

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.*;

import java.util.Date;
import java.lang.reflect.*;

import com.nrt.basic.*;
import com.nrt.render.*;
import com.nrt.model.*;
import com.nrt.collision.*;
import com.nrt.font.*;
import com.nrt.input.*;
import com.nrt.ui.*;
import com.nrt.framework.*;

import com.nrt.math.Float3;
import com.nrt.math.Float4x4;
import java.util.logging.*;
import android.view.*;
import com.nrt.math.*;

import com.google.vrtoolkit.cardboard.CardboardActivity;
import com.google.vrtoolkit.cardboard.CardboardView;
import com.google.vrtoolkit.cardboard.Eye;
import com.google.vrtoolkit.cardboard.HeadTransform;
import com.google.vrtoolkit.cardboard.Viewport;
import com.google.vrtoolkit.cardboard.audio.CardboardAudioEngine;
import com.google.vrtoolkit.cardboard.*;
import com.google.vrtoolkit.cardboard.proto.nano.*;

public class MainActivity extends CardboardActivity
{
	CardboardStarStereoRenderer m_steteoRenderer = new CardboardStarStereoRenderer();
	CardboardAudioEngine m_audioEngine = null;
	CardboardOverlayView m_overlayView = null;
	private android.os.Handler m_handler = new android.os.Handler();

	private static AppContext s_appContext = new CardboardStarAppContext();//GameMain s_gameMain = new GameMain();
	private int m_nbOnCreated = 0;
	/**
	 * Sets the view to our CardboardView and initializes the transformation matrices we will use
	 * to render our scene.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.cardboard_main);
		CardboardStarView cardboardView = (CardboardStarView) findViewById(R.id.cardboard_view);
		cardboardView.setRestoreGLStateEnabled(false);
		cardboardView.setRenderer(m_steteoRenderer);
		setCardboardView(cardboardView);

		DisplayMetrics metrics = new DisplayMetrics();  
		getWindowManager().getDefaultDisplay().getMetrics(metrics);  
		//int dpi1 = metrics.densityDpi;



		SubSystem.Initialize
		(
			getResources().getAssets(), 
			null,//(TextView) findViewById(R.id.logview),
			metrics.densityDpi,
			m_handler,
			s_appContext
		);
		//SubSystem.AppFrame.AppEnableUpdate.set(false);


		if (m_nbOnCreated <= 0)
		{
			//. Initialize minimum rendering resources.
			//SubSystem.OnCreate(m_renderView.m_surfaceRenderer);
			//DelayResourceQueue drq = SubSystem.DelayResourceQueue;

			//m_gameMain.OnCreate(drq);


		}
		else
		{
		 	SubSystem.DelayResourceQueue.ReloadResources();
		}

		SubSystem.Log.WriteLine("MainActivity.onCreate() " + m_nbOnCreated);
		m_nbOnCreated++;
		/*
		 modelCube = new float[16];
		 camera = new float[16];
		 view = new float[16];
		 modelViewProjection = new float[16];
		 modelView = new float[16];
		 modelFloor = new float[16];
		 // Model first appears directly in front of user.
		 modelPosition = new float[] {0.0f, 0.0f, -MAX_MODEL_DISTANCE / 2.0f};
		 headRotation = new float[4];
		 headView = new float[16];
		 vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		 */
		m_overlayView = (CardboardOverlayView) findViewById(R.id.overlay);
		m_overlayView.show3DToast("Pull the magnet when you find an object.");


		// Initialize 3D audio engine.
		//m_audioEngine = new CardboardAudioEngine(getAssets(), CardboardAudioEngine.RenderingQuality.HIGH);
	}
	/*
	@Override
	public void onCardboardTrigger()
	{

		if(SubSystem.AppFrame.AppCreated.get())
		{
			((CardboardAppContext)SubSystem.AppFrame.AppContext).OnCardboardTrigger();
		}
		// TODO: Implement this method
		super.onCardboardTrigger();
	}
	*/
	
}





//public class MainActivity extends Activity
class MainActivity0 extends Activity
{
	private RenderSurfaceView m_renderView;

	private android.os.Handler m_handler = new android.os.Handler();

	private static AppContext s_appContext = new CardboardStarAppContext();//GameMain s_gameMain = new GameMain();
	//private static AppContext s_appContext = new GameMain();

	static int m_nbOnCreated = 0;

	// Called when the activity is first created.
	@Override public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// WindowManager から取得する (要 Activity)
		DisplayMetrics metrics = new DisplayMetrics();  
		getWindowManager().getDefaultDisplay().getMetrics(metrics);  
		//int dpi1 = metrics.densityDpi;

		//. Initialize minimum subsystem instances.		

		SubSystem.Initialize
		(
			getResources().getAssets(), 
			(TextView) findViewById(R.id.logview),
			metrics.densityDpi,
			m_handler,
			s_appContext
		);

		//. Initialize render surface view.
		m_renderView = (RenderSurfaceView)this.findViewById(R.id.glview);

		//m_renderView.Initialize();


		if (m_nbOnCreated <= 0)
		{
			//. Initialize minimum rendering resources.
			//SubSystem.OnCreate(m_renderView.m_surfaceRenderer);
			//DelayResourceQueue drq = SubSystem.DelayResourceQueue;

			//m_gameMain.OnCreate(drq);


		}
		else
		{
		 	SubSystem.DelayResourceQueue.ReloadResources();
		}

		SubSystem.Log.WriteLine("MainActivity.onCreate() " + m_nbOnCreated);
		m_nbOnCreated++;
	}

	@Override public void onResume() 
	{
		super.onResume();
		//m_renderView.onResume();
		SubSystem.Log.WriteLine("MainActivity.onResume()");
	}

	@Override public void onPause()
	{
		super.onPause();
		//m_renderView.onPause();
		SubSystem.Log.WriteLine("MainActivity.onPause()");
	}

	@Override
	protected void onStart()
	{
		// TODO: Implement this method
		super.onStart();
		SubSystem.Log.WriteLine("MainActivity.onStart()");

	}

	@Override
	protected void onStop()
	{
		// TODO: Implement this method
		super.onStop();
		SubSystem.Log.WriteLine("MainActivity.onStart()");

	}

	@Override
	protected void onDestroy()
	{
		// TODO: Implement this method
		super.onDestroy();
		//m_renderView.onDestroy();
		SubSystem.Log.WriteLine("MainActivity.onDestroy()");
	}
}




//////////////////////////////////////////////////////////////////////////
///GLSurfaceViewの拡張 

class RenderSurfaceView extends GLSurfaceView
{
	public SurfaceRenderer m_surfaceRenderer = null;

	public RenderSurfaceView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		Initialize();
	}

	public RenderSurfaceView(Context context) 
	{ 
		super(context);
		Initialize();
	}

	public void Initialize()
	{
		this.setEGLContextClientVersion(2);
		m_surfaceRenderer = new SurfaceRenderer();
		this.setRenderer(m_surfaceRenderer);
	}

	public boolean onTouchEvent(MotionEvent me)
	{

		DevicePointer.OnTouchEvent(me);
		return true;
	}

	@Override
	public void onResume()
	{
		// TODO: Implement this method
		//SubSystem.Log.Write( "onResume()" );
		super.onResume();
	}

	@Override
	public void onPause()
	{
		// TODO: Implement this method
		//SubSystem.Log.Write( "onPause()" );
		super.onPause();
	}

	@Override
	protected void onDetachedFromWindow()
	{
		// TODO: Implement this method
		super.onDetachedFromWindow();

		if (SubSystem.Log != null)
		{
			SubSystem.Log.WriteLine("onDetachedFromWindow");
		}
	}



	public void onDestroy()
	{
		//super.onDestroy();
	}
}


//////////////////////////////////////////////////////////////////////////
///ビュー用のレンダラ―
class SurfaceRenderer implements GLSurfaceView.Renderer 
{
	private int SurfaceWidth = 0;
	private int SurfaceHeight = 0;

	public UiForm m_form = new UiForm();

	public UiRectButton m_buttonDebug = null;
	public boolean m_isDispError = true;

	//public GameMain m_gameMain = null;//new GameMain();


	int m_nbOnSurfaceCreated = 0;

	public SurfaceRenderer()
	{
		//m_gameMain = gameMain;
	}

	///////////////////////////////////////////////////////////////////////////
	// 最初に呼ばれる
	public void onSurfaceCreated(GL10 gl, EGLConfig config)
	{

		SubSystem.Log.WriteLine(String.format("onSurfaceCreated() %d", m_nbOnSurfaceCreated));
		OnSurfaceCreated();

	}

	private synchronized void OnSurfaceCreated()
	{
		if (m_nbOnSurfaceCreated <= 0)
		{			

			m_form = new UiForm();
			m_form.Add((m_buttonDebug = new UiRectButton(new Rect(10, 30, 50, 50))));

			//DebugLog.Error.WriteLines(Shader.Error);
		}
		else
		{
			//m_gameMain.m_fStartTime = SubSystem.Timer.FrameTime;
			SubSystem. DelayResourceQueue.ReloadResources();
		}
		m_nbOnSurfaceCreated++;
	}

	///////////////////////////////////////////////////////////////////////////
	// サーフェイスのサイズ変更時とかに呼ばれる
	public void onSurfaceChanged(GL10 gl, int width, int height) 
	{
		SubSystem.AppFrame.OnSurfaceChanged(width, height);
		/*                                          
		 SubSystem.Log.WriteLine("onSurfaceChanged()");
		 SurfaceWidth = width;
		 SurfaceHeight = height;
		 SubSystem.Render.ScanOutWidth = width;
		 SubSystem.Render.ScanOutHeight = height;

		 m_gameMain.OnSurfaceChanged(width, height);
		 */
	}

	public int m_mem = 0;
	///////////////////////////////////////////////////////////////////////////
	// 毎フレーム呼ばれるやつ
	public void onDrawFrame(GL10 gl)
	{
		//SubSystem.AppFrame.OnRender(true);
	}

	public void onDrawFrame0(GL10 gl)
	{
		SubSystem.Timer.Update();
		float fElapsedTime = SubSystem.Timer.SafeFrameElapsedTime;



		if (SubSystem.MinimumMarker.Done)
		{
			SubSystem.FramePointer.Update(fElapsedTime);	


			m_form.Update(SubSystem.FramePointer, fElapsedTime);
			if (m_buttonDebug.IsPush())
			{

				m_isDispError = !m_isDispError;
			}

			//m_gameMain.OnUpdate();

			//m_gameMain.OnRender();

			if (SubSystem.DebugFontReadyMarker.Done)
			{
				FontRender fr = SubSystem.DebugFont;
				BasicRender br = SubSystem.BasicRender;
				MatrixCache mc = br.GetMatrixCache();

				//GLES20.glClearColor(0.0f, 0.0f, 0.1f, 1.f); 
				//GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);


				Float4x4 matrixOrtho = Float4x4.Local();
				Matrix.orthoM(matrixOrtho.Values, 0, 0, SurfaceWidth, SurfaceHeight, 0, -1.0f, 1.0f);

				mc.SetView(Float4x4.Identity(Float4x4.Local()));
				mc.SetProjection(matrixOrtho);
				mc.SetWorld(Float4x4.Identity(Float4x4.Local()));
				//mc.Update();

				GLES20.glDisable(GLES20.GL_CULL_FACE);

				if (m_isDispError && fr.m_font.IsReady)
				{
					fr.Begin();
					Float3 f3Position = new Float3(
						0.0f,
						SurfaceHeight - fr.m_font.m_nFontSize * DebugLog.Error.Buffers.length,
						0.0f);
					for (int i = 0 ; i < DebugLog.Error.Buffers.length - 1 ; i++)
					{
						int rp = (DebugLog.Error.RenderPosition < 0 ? 0 : DebugLog.Error.RenderPosition);
						int ii = (rp + i) % DebugLog.Error.Buffers.length;

						fr.Draw(f3Position, DebugLog.Error.Buffers[ii]);
						f3Position.Y += fr.m_font.m_nFontSize;
					}
					fr.End();

				}

				int nScanOutWidth = SubSystem.Render.ScanOutWidth;
				int nScanOutHeight = SubSystem.Render.ScanOutHeight;

				fr.Begin();
				fr.Draw(0.0f, 0.0f, 0.0f, String.format("FPS:%3.3f", 1.0f / fElapsedTime));
				long freeMem = Runtime.getRuntime().freeMemory();
				long totalMem = Runtime.getRuntime().totalMemory();
				fr.Draw(0.0f, fr.m_fSize, 0.0f, String.format("Mem:%d/%d", (int) (totalMem - freeMem), (int) totalMem));
				fr.Draw(0.0f, fr.m_fSize * 2.0f, 0.0f, String.format("ScanOut:%dx%d", 
																	 nScanOutWidth, nScanOutHeight));

				/*												 
				 fr.Draw(0.0f, fr.m_fSize * 3, 0.0f, String.format("BackBuffer:%dx%d %dx%d", 
				 (int) m_gameMain.m_frameBuffer.Width,
				 (int) m_gameMain.m_frameBuffer.Height,
				 (int) ((RenderTexture) m_gameMain.m_frameBuffer.ColorRenderTexture).PotWidth,
				 (int) ((RenderTexture) m_gameMain.m_frameBuffer.ColorRenderTexture).PotHeight));
				 */

				long usemem = totalMem - freeMem;
				if (m_mem <= 0)
				{
					m_mem = (int) usemem;
				}
				else
				{
					if ((usemem - m_mem) > 65536 * 16)
					{
						//java.lang.System.gc();
					}
				}

				//sfr.Draw( 0.0f, fr.m_fSize*2.0f, 0.0f, String.format( "%f %f %f", m_f3Euler.X, m_f3Euler.Y, m_f3Euler.Z ) );
				fr.End();

				m_form.Render(br);

				for (FramePointer.Pointer pointer : SubSystem.FramePointer.Pointers)
				{
					if (pointer.Push)
					{
						br.SetColor(1.0f, 0.0f, 0.0f, 1.0f);
					}
					else if (pointer.Release)
					{
						br.SetColor(0.0f, 0.0f, 1.0f, 1.0f);
					}
					else if (pointer.Down)
					{
						br.SetColor(0.0f, 1.0f, 0.0f, 1.0f);
					}
					else if (pointer.Up)
					{
						continue;
					}

					br.Arc(pointer.Position.X, pointer.Position.Y, 64.0f, 16);
				}
			}
		}

		SubSystem.DelayResourceQueue.Update(fElapsedTime);
	}
}


