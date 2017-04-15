package com.nrt.cardboardstar;

import javax.microedition.khronos.egl.*;
import android.opengl.GLES20;
import com.google.vrtoolkit.cardboard.*;
import com.nrt.framework.*;
import com.nrt.render.*;
import com.nrt.math.*;
import com.nrt.input.*;
import com.nrt.font.*;


import com.nrt.ui.*;
import com.nrt.basic.*;



public class CardboardStarStereoRenderer
implements CardboardView.StereoRenderer
{
	int m_width=0;
	int m_height =0;

	//UiForm m_form = new UiForm();
	//UiRectButton m_button = new UiRectButton(new Rect(0,0,100,100));
	BlendState m_blendStateTransparent = null;

	CardboardStarStereoRenderer()
	{
		super();
		//m_form.Add(m_button);
	}


	@Override
	public void onSurfaceCreated(EGLConfig p1)
	{

	}

	@Override
	public void onSurfaceChanged(int width, int height)
	{
		SubSystem.AppFrame.OnSurfaceChanged(width, height);
		m_width = width;
		m_height = height;


	}

	//int FlippedFrame = 0;


	@Override
	public void onNewFrame(HeadTransform headTransform)
	{

		SubSystem.AppFrame.OnNewFrame();
		if (SubSystem.AppFrame.AppCreated.get())
		{
			synchronized (SubSystem.AppFrame.AppMutex)
			{
				//SubSystem.AppFrame.OnNewFrame();
				((CardboardAppContext)SubSystem.AppFrame.AppContext).OnNewFrame(headTransform);
			}
		}
		else
		{
			//java.lang.Thread.yield();
		}

		SubSystem.BitmapFont.BeginFrame();
		//m_form.Update(SubSystem.FramePointer, SubSystem.Timer.FrameElapsedTime);

	}

	@Override
	public void onDrawEye(Eye eye)
	{
		SubSystem.AppFrame.OnDrawEye();


		if (SubSystem.MinimumMarker.Done)
		{
			GfxCommandContext gfxc = SubSystem.GfxCommandContext;
			MatrixCache mc = SubSystem.MatrixCache;
			GfxCommandBuffer cb = SubSystem.GetCommandBuffer();
			
			gfxc.BeginFrame(cb);

			if (eye.getType() == Eye.Type.MONOCULAR)
			{
				gfxc.SetViewport
				(
					eye.getViewport().x,
					eye.getViewport().y,
					eye.getViewport().width,
					eye.getViewport().height
				);
			}

			FrameLinearVertexBuffer vb = SubSystem.GetVertexBuffer();
			FrameLinearIndexBuffer ib = SubSystem.GetIndexBuffer();
			vb.BeginFrame();
			ib.BeginFrame();

			BasicRender br = SubSystem.BasicRender;
			br.SetCommandContext(gfxc, mc, vb, ib);

			BitmapFont bf = SubSystem.BitmapFont;
			bf.SetCommandContext(gfxc, mc, vb, ib);
			
			gfxc.SetClearColor(0.0f,1.0f,1.0f,0.0f);
			gfxc.Clear(EClearBuffer.ColorDepth);

			if (SubSystem.AppFrame.AppCreated.get() && SubSystem.SubSystemReadyMarker.Done )
			{
				synchronized (SubSystem.AppFrame.AppMutex)
				{
					((CardboardAppContext)SubSystem.AppFrame.AppContext).OnDrawEye(eye);
					//SubSystem.AppFrame.AppContext.OnRender();
				}
			}
			
			Render r = SubSystem.Render;
			
			gfxc.End();
			ib.EndFrame();
			vb.EndFrame();
			gfxc.ProcessCommands(r, 0, -1);
			SubSystem.FlipBuffers();
		}

		if (eye.getType() == Eye.Type.MONOCULAR)
		{
			onFinishFrame(eye.getViewport());	
		}

	}

	float m_fFpsRing[] = new float[60];
	int m_iFpsPosition = 0;
	float UpdateFps(float fElapsedTime)
	{
		m_fFpsRing[m_iFpsPosition] = 1.0f / fElapsedTime;

		float fFpsResult = 0.0f;
		for (float fFps : m_fFpsRing)
		{
			fFpsResult += fFps;
		}

		m_iFpsPosition++;
		m_iFpsPosition %= m_fFpsRing.length;

		return fFpsResult / (float)m_fFpsRing.length;
	}
	/*
	 Called before a frame is finished.

	 By the time of this call, the frame contents have been already drawn and, if enabled, distortion correction has been applied.

	 This method allows to perform additional overlay rendering over the frame results. For example, it could be used to draw markers that help users to properly center their displays in their GVR viewers.

	 Any rendering in this step is relative to the full surface, not to any single eye view. A viewport object describing the bounds of the full surface is provided and automatically set in the current GL context.
	 */
	@Override
	public void onFinishFrame(Viewport viewPort)
	{
		if (SubSystem.MinimumMarker.Done)
		{
			GfxCommandContext gfxc = SubSystem.GfxCommandContext;
			MatrixCache mc = SubSystem.MatrixCache;
			GfxCommandBuffer cb = SubSystem.GetCommandBuffer();
			gfxc.BeginFrame(cb);

			FrameLinearVertexBuffer vb = SubSystem.GetVertexBuffer();
			FrameLinearIndexBuffer ib = SubSystem.GetIndexBuffer();
			vb.BeginFrame();
			ib.BeginFrame();

			BasicRender br = SubSystem.BasicRender;
			br.SetCommandContext(gfxc, mc, vb, ib);

			BitmapFont bf = SubSystem.BitmapFont;
			bf.SetCommandContext(gfxc, mc, vb, ib);

			if (SubSystem.AppFrame.AppCreated.get())
			{
				((CardboardAppContext)SubSystem.AppFrame.AppContext).OnFinishFrame(viewPort);
			}

			RenderFinishFrame(gfxc, viewPort);

			gfxc.End();
			ib.EndFrame();
			vb.EndFrame();
			gfxc.ProcessCommands(SubSystem.Render, 0, -1);

			SubSystem.FlipBuffers();			

		}

		android.util.Log.d("FimishFrame"," "+SubSystem.AppFrame.UpdateFrame.get());

		SubSystem.AppFrame.OnFinishFrame();
	}

	private void RenderFinishFrame(GfxCommandContext gfxc, Viewport viewPort)
	{
		if (m_blendStateTransparent == null)
		{
			m_blendStateTransparent = new BlendState(true, BlendFunc.SrcAlpha, BlendFunc.OneMinusSrcAlpha);
		}
		//FontRender fr = SubSystem.DebugFont;
		BasicRender br = SubSystem.BasicRender;
		MatrixCache mc = br.GetMatrixCache();

		//GLES20.glClearColor(0.0f, 0.0f, 0.1f, 1.f); 
		//GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

		gfxc.SetViewport(viewPort.x,viewPort.y,viewPort.width,viewPort.height);

		//Float4x4 matrixOrtho = Float4x4.Local();
		//Matrix.orthoM(matrixOrtho.Values, 0, 0, viewPort.width, viewPort.height, 0, -1.0f, 1.0f);

		mc.SetView(Float4x4.Identity(Float4x4.Local()));
		mc.SetProjection(Float4x4.Ortho(Float4x4.Local(),
										0, viewPort.width, viewPort.height, 0, -1.0f, 1.0f
										));
		mc.SetWorld(Float4x4.Identity(Float4x4.Local()));

		
		gfxc.SetBlendState(m_blendStateTransparent);
		
		for (FramePointer.Pointer pointer : SubSystem.FramePointer.Pointers)
		{
			int color = 0x0;
			if (pointer.Push)
			{
				color = 0xff0000ff;
			}
			else if (pointer.Release)
			{
				color = 0x0000ffff;
			}
			else if (pointer.Down)
			{
				color = 0x00ff00ff;
			}
			else if (pointer.Up)
			{
				continue;
			}

			float mmRadius = 12.0f;
			float pixRadius = 0.5f*(mmRadius/25.4f)*((float)SubSystem.DisplayDPI);
			
			
			br.Arc(pointer.Position.X, pointer.Position.Y, pixRadius*0.5f, 0x00000000, pixRadius, color, 16);
		}

		if (gfxc != null)
		{
			gfxc.SetBlendState(m_blendStateTransparent);
		}
		else
		{
			GLES20.glDisable(GLES20.GL_BLEND);
		}
		float fElapsedTime = SubSystem.Timer.FrameElapsedTime;

		BitmapFont bf = SubSystem.BitmapFont;

		if(1280 < viewPort.width )
		
{
			bf.SetSize(16.0f);		
		}
		else
		{
			bf.SetSize(8.0f);
		}

		bf.SetColor(0x00ff00ff);
		float size = bf.m_fSize;

		bf.Begin();
		float fFps = UpdateFps(fElapsedTime);
		bf.Draw(0.0f, 0.0f, 0.0f, String.format("FPS:%3.3f", fFps));
		long freeMem = Runtime.getRuntime().freeMemory();
		long totalMem = Runtime.getRuntime().totalMemory();
		bf.Draw(0.0f, size, 0.0f, String.format("MEM:%d/%d", (int) (totalMem - freeMem), (int) totalMem));
		bf.Draw(0.0f, size * 2.0f, 0.0f, String.format("SCANOUT:%dX%d", 
													   viewPort.width, viewPort.height));
		bf.Draw(0.0f, size * 3.0f, 0.0f, String.format("SURFACE:%dX%d", 
													   m_width, m_height));

		bf.Draw(0.0f, size * 4.0f, 0.0f, String.format("DPI %d", SubSystem.DisplayDPI));

		if (SubSystem.AppFrame.AppCreated.get())
		{

			CardboardStarAppContext app = (CardboardStarAppContext) SubSystem.AppFrame.AppContext;
			if (app != null)
			{
				bf.Draw(0.0f, size * 5.0f, 0.0f, String.format("WINDOW:%dX%d", 
															   app.m_windowWidth, app.m_windowHeight));


				Print(bf, 0.0f, size * 9.0f, 0.0f, app.m_matrixHeadTransform);
				Print(bf, size * 36.0f, size * 9.0f, 0.0f, app.m_player.Transform);

				Print(bf, 0.0f, size * 14.0f, 0.0f, Float4x4.Invert(Float4x4.Local(), app.m_matrixEyeViewLeft));
				Print(bf, 0.0f, size * 19.0f, 0.0f, app.m_matrixEyeViewRight);

				Print(bf, size * 36.0f, size * 14.0f, 0.0f, app.m_matrixEyeViewLeftRaw);
				Print(bf, size * 36.0f, size * 19.0f, 0.0f, app.m_matrixEyeViewRightRaw);

				Print(bf, 0.0f, size * 24.0f, 0.0f, app.m_player.Transform.W);
				//Print(bf, 0.0f, size * 25.0f,0.0f,Float4x4.Invert(Fl	
				Print(bf, 960.0f, size * 24.0f, 0.0f, app.m_player.Shots[0].LightTail.GetTransform(0));

				Print(bf, 0.0f, size * 26.0f, 0.0f, Float3.Local().Mul(app.m_player.AngularVelocity, 180.0f / FMath.PI));

				bf.Draw(0.0f, size * 27.0f, 0.0f, String.format("FIRE %d", app.m_player.m_nbFireRemains));
			}
		}
		bf.Draw(0.0f, size * 6.0f, 0.0f, String.format("PRIMITIVE:%d", SubSystem.BasicRender.PrimitiveCount));

		bf.Draw(0.0f, size * 7.0f, 0.0f, String.format("STRING:%d", SubSystem.BitmapFont.StringCount + 1));


		//sfr.Draw( 0.0f, fr.m_fSize*2.0f, 0.0f, String.format( "%f %f %f", m_f3Euler.X, m_f3Euler.Y, m_f3Euler.Z ) );
		bf.End();

		//GfxCommandContext gfxc = SubSystem.GfxCommandContext;


	}

	void Print(BitmapFont bf, float x, float y, float z, final Transform3 m)
	{
		Print(bf, x, y, z, Float4x4.Local(m));
	}

	void Print(BitmapFont bf, float x, float y, float z, final Float4x4 m)
	{
		float h = bf.m_fSize;
		for (int yy = 0 ; yy < 4 ; yy++)
		{
			for (int xx=0;xx < 4;xx++)
			{
				bf.Draw
				(
					x + h * 8 * xx, y + h * yy, z,
					String.format("%3.3f", m.Values[xx + yy * 4])
				);
			}
		}
	}

	void Print(BitmapFont bf, float x, float y, float z, final Float3 m)
	{
		float h = bf.m_fSize;
		bf.Draw(x + h * 8 * 0, y, z, String.format("%3.3f", m.X));
		bf.Draw(x + h * 8 * 1, y, z, String.format("%3.3f", m.Y));
		bf.Draw(x + h * 8 * 2, y, z, String.format("%3.3f", m.Z));

	}
	@Override
	public void onRendererShutdown()
	{
		// TODO: Implement this method
	}

}
