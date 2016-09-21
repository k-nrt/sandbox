package com.nrt.framework;

import java.util.concurrent.atomic.*;
import android.opengl.*;
import com.nrt.render.*;
import com.nrt.font.*;
public class AppFrame implements Runnable
{
	public AppContext AppContext = null;
	public AtomicLong UpdateFrame = new AtomicLong();
	public AtomicLong RenderFrame = new AtomicLong();

	public int ScanOutWidth = 0;
	public int ScanOutHeight = 0;
	public AtomicBoolean IsScanOutRready = new AtomicBoolean(false);
	public ThreadGroup m_threadGroup = new ThreadGroup("AppMain");
	public Thread m_thread = new Thread(m_threadGroup, this);

	public Object AppMutex = new Object();
	public AtomicBoolean AppCreated = new AtomicBoolean(false);

	public AtomicBoolean AppEnableUpdate = new AtomicBoolean(true);

	public void StartContext(AppContext appContext)
	{
		if (AppContext == null)
		{
			AppContext = appContext;
			m_thread.start();
		}
	}	

	@Override
	public void run()
	{
		while(SubSystem.IsInitialized==false)
		{
			Thread.yield();
		}
		SubSystem.Log.WriteLine("AppThread start");
		SubSystem.OnCreate();

		while (SubSystem.SubSystemReadyMarker.Done == false)
		{
			Thread.yield();
		}

		fr = 0.25f;
		while (IsScanOutRready.get() == false)
		{
			Thread.yield();
		}
		
		fr = 0.5f;

		if (AppContext != null)
		{
			synchronized (AppMutex)
			{
				AppContext.OnCreate(SubSystem.DelayResourceQueue);
				AppCreated.set(true);
				//OnSurfaceChangedInternal(ScanOutWidth, ScanOutHeight);
				//AppContext.OnSurfaceChanged(ScanOutWidth,ScanOutHeight);
				//SubSystem.DelayResourceQueue.ReloadResources();
			}
			//AppCreated.set(true);
		}

		fr = 1.0f;
		/*
		 if(!AppEnableUpdate.get())
		 {
		 return;
		 }
		 */
		//long prevFrame = SubSystem.Timer.Frame.get();
		for (;;)
		{
			/*
			 while(!AppEnableUpdate.get())
			 {
			 Thread.yield();
			 }
			 */
			OnUpdate();

			UpdateFrame.incrementAndGet();

			
			while (UpdateFrame.get() != RenderFrame.get())
			{
				Thread.yield();
				//fr += 0.1f;
			}
			
			//prevFrame = SubSystem.Timer.Frame.get();)
		}

		// TODO: Implement this meod
		//super.run();
	}
	
	float fg = 0.0f;

	float fr = 0.0f;
	public void OnUpdate()
	{
		synchronized (AppMutex)
		{
			SubSystem.Timer.Update();
			float fElapsedTime = SubSystem.Timer.FrameElapsedTime;
			SubSystem.FramePointer.Update(fElapsedTime);

			if (AppCreated.get())
			{
				AppContext.OnUpdate();
			}
			fr += 0.1f/60.0f;
			if(fr > 1.0f)
			{
				//fr = 1.0f;
			}
		}
	}

	public void OnSurfaceChanged(int width, int height)
	{
		synchronized (AppMutex)
		{
			OnSurfaceChangedInternal(width, height);
		}
	}


	public void OnSurfaceChangedInternal(int width, int height)
	{
		SubSystem.Log.WriteLine("onSurfaceChanged()");

		ScanOutWidth = width;
		ScanOutHeight = height;

		if (IsScanOutRready.getAndSet(true) == false)
		{
			return;
		}

		if (SubSystem.Render != null)
		{
			SubSystem.Render.ScanOutWidth = width;
			SubSystem.Render.ScanOutHeight = height;
		}

		if (AppContext != null)
		{
			if (AppCreated.get())
			{
				AppContext.OnSurfaceChanged(width, height);
			}
		}

		SubSystem.DelayResourceQueue.ReloadResources();

	}

	//boolean m_isUpdated = false;
	int m_nbNewFrames = 0;
	int m_nbFinishFrames = 0;

	int m_nbDrawEyes = 0;

	public void OnNewFrame()
	{
		m_nbDrawEyes = 0;

		if(SubSystem.SubSystemReadyMarker.Done)
		{
			if (AppCreated.get())
			{
			//fr = 1.0f;
			
				while (UpdateFrame.get() < RenderFrame.get())
				{
					Thread.yield();
				}
			
			}
		}
		m_nbNewFrames++;
	}

	public void OnDrawEye()
	{
		DelayResourceQueue drq = SubSystem.DelayResourceQueue;
		float fElapsedTime = 1.0f/60.0f;//SubSystem.Timer.FrameElapsedTime;
		if( m_nbDrawEyes == 0 )
		{
			drq.Update(fElapsedTime);
		}

		if(SubSystem.MinimumMarker.Done)
		{
			//SubSystem.BitmapFont.BeginFrame();
			//FlippedFrame++;
			//m_nbNewFrames = SubSystem.DelayResourceQueue.m_nbAppliedResources;
		}
		
		if (false)//AppCreated.get())
		{
			
		}
		else
		{
			if (m_nbDrawEyes == 0)
			{
				int nG = SubSystem.MinimumMarker.Done ? 50 : 100;
				
				//int nB = (0 < drq.m_nbMaxResources) ? (drq.m_nbAppliedResources*100)/drq.m_nbMaxResources : 0;
				int nB = m_nbFinishFrames%100;
				float r = com.nrt.math.FMath.Fraction(((float)m_nbNewFrames)/100.0f);
				float g = com.nrt.math.FMath.Fraction(((float)nG)/100.0f);
				//float b = com.nrt.math.FMath.Fraction(((float)RenderFrame.get())/100.0f);
				float b = com.nrt.math.FMath.Fraction(((float)nB)/100.0f);
				
				GLES20.glClearColor(r, g, b, 1.0f); 
			}
			else
			{
				GLES20.glClearColor(0.0f, 1.0f, 0.0f, 1.f); 
			}
			GLES20.glDepthMask(true);
			GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
		}
		
		//RenderFrame.set(UpdateFrame.get());
		m_nbDrawEyes++;
	}

	public void OnFinishFrame()
	{
		//synchronized (AppMutex)
		{
			//SubSystem.BasicRender.BeginFrame(FlippedFrame);
			//AppContext.OnRender();
			//FlippedFrame++;
		}

		fg += 1.0f/60.0f;
		if(1.0f < fg)
		{
			fg -= 1.0f;
		}
		
		m_nbFinishFrames++;
		RenderFrame.set(UpdateFrame.get());
	}
}

