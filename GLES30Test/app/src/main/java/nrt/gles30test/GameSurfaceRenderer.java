package nrt.gles30test;
import android.opengl.GLSurfaceView;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.egl.EGLConfig;
import android.opengl.GLES30;
import android.opengl.Matrix;
import com.nrt.framework.*;

import com.nrt.render.BasicRender;
import com.nrt.render.MatrixCache;
import com.nrt.math.Float4x4;
import com.nrt.render.*;
import com.nrt.font.BitmapFontRender;
//////////////////////////////////////////////////////////////////////////
///ビュー用のレンダラ―
public class GameSurfaceRenderer implements GLSurfaceView.Renderer 
{
	private int SurfaceWidth = 0;
	private int SurfaceHeight = 0;

	//public UiForm m_form = new UiForm();

	//public UiRectButton m_buttonDebug = null;
	public boolean m_isDispError = true;

	//public GameContext m_gameContext = null;	
	int m_nbOnSurfaceCreated = 0;

	public GameSurfaceRenderer()
	{
	}

	/*
	 public void SetGameContext(GameContext gameContext)
	 {
	 m_gameContext = gameContext;
	 }
	 */
	///////////////////////////////////////////////////////////////////////////
	// 最初に呼ばれる
	public void onSurfaceCreated(GL10 gl, EGLConfig config)
	{
		Subsystem.Log.WriteLine(String.format("onSurfaceCreated() %d", m_nbOnSurfaceCreated));
		OnSurfaceCreated();
	}

	private synchronized void OnSurfaceCreated()
	{

		if (m_nbOnSurfaceCreated <= 0)
		{			

		}
		else
		{
			Subsystem.ResourceQueue.ReloadResources();
		}

		m_nbOnSurfaceCreated++;
	}

	///////////////////////////////////////////////////////////////////////////
	// サーフェイスのサイズ変更時とかに呼ばれる
	public void onSurfaceChanged(GL10 gl, int width, int height) 
	{

		Subsystem.Log.WriteLine("onSurfaceChanged()");
		SurfaceWidth = width;
		SurfaceHeight = height;

		//if(Subsystem.MinimumMarker.Done==false)

		//

		if (Subsystem.IsInitialized)
		{
			Subsystem.Render.ScanOutWidth = width;
			Subsystem.Render.ScanOutHeight = height;

			Subsystem.AppContext.OnSurfaceChanged(width, height);
		}

	}

	public int m_mem = 0;
	///////////////////////////////////////////////////////////////////////////
	// 毎フレーム呼ばれるやつ
	public void onDrawFrame(GL10 gl)
	{
		if (Subsystem.IsInitialized == false)
		{
			GLES30.glClearColor(0.5f, 0.0f, 0.0f, 0.0f);
			GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT | GLES30.GL_DEPTH_BUFFER_BIT | GLES30.GL_STENCIL_BUFFER_BIT);
		}
		else
		{
			Subsystem.FrameTimer.Update();
			float fElapsedTime = Subsystem.FrameTimer.SafeFrameElapsedTime;

			if (Subsystem.MinimumMarker.Done == false || Subsystem.SubsystemReadyMarker.Done == false)
			{
				GLES30.glClearColor(0, 0, 1, 0);
				GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT | GLES30.GL_DEPTH_BUFFER_BIT | GLES30.GL_STENCIL_BUFFER_BIT);

				Subsystem.ResourceQueue.Update(fElapsedTime);

				gl.glFlush();
			}
			else
			{
				GLES30.glClearColor(1, 0, 0, 0);
				GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT | GLES30.GL_DEPTH_BUFFER_BIT | GLES30.GL_STENCIL_BUFFER_BIT);

				BasicRender br = Subsystem.BasicRender;
				MatrixCache mc = Subsystem.MatrixCache;

				Float4x4 matrixOrtho = Float4x4.Local();
				Matrix.orthoM(matrixOrtho.Values, 0, 0, SurfaceWidth, SurfaceHeight, 0, -1.0f, 1.0f);

				mc.SetView(Float4x4.Identity(Float4x4.Local()));
				mc.SetProjection(matrixOrtho);
				mc.SetWorld(Float4x4.Identity(Float4x4.Local()));

				br.Begin(Primitive.Lines, BasicRender.EShader.Color);
				br.SetColor(0xffffffff);
				br.SetVertex(0.0f, 0.0f, 0.0f);
				br.SetVertex(SurfaceWidth, SurfaceHeight, 0.0f);
				br.End();

				if (Subsystem.AppContext != null)
				{
					synchronized (Subsystem.AppMutex)

					{
						Subsystem.AppContext.OnRender(fElapsedTime);
					}
				}

				if(Subsystem.BitmapFontRender != null)
					{
				if (Subsystem.BitmapFontRender.IsInitialized)
				{
					if (Subsystem.BitmapFontRender.m_texture.GetTextureName() != 0)
					{
						/*
						br.SetTexture(Subsystem.BitmapFontRender.m_texture);
						br.Begin(Primitive.TriangleStrip, BasicRender.EShader.ColorTexture);
						br.SetColor(0xffffffff);
						br.SetTexcoord(0, 0);
						br.SetVertex(0.0f, 0.0f, 0.0f);
						br.SetTexcoord(1, 1);
						br.SetVertex(SurfaceWidth, SurfaceHeight, 0.0f);
						br.SetTexcoord(0, 1);
						br.SetVertex(0, SurfaceHeight, 0.0f);
						br.End();
						*/
					}
				}
				}


				//MatrixCache mc = Subsystem.MatrixCache;
				//Float4x4 matrixOrtho = Float4x4.Local();
				Matrix.orthoM(matrixOrtho.Values, 0, 0, SurfaceWidth, SurfaceHeight, 0, -1.0f, 1.0f);
				//mc.SetView(Float4x4.Identity(Float4x4.Local()));
				//mc.SetProjection(matrixOrtho);
				//mc.SetWorld(Float4x4.Identity(Float4x4.Local()));
				//mc.Update();
				/*
				 BitmapFontRender fr = Subsystem.BitmapFontRender;
				 fr.SetColor(0xffffffff);
				 fr.SetSize(16.0f);
				 fr.Begin();
				 fr.Draw(100,100,0,"TEXT");
				 fr.End();
				 */
			}
		}
		/*
		 SubSystem.Timer.Update();
		 float fElapsedTime = SubSystem.Timer.SafeFrameElapsedTime;

		 //float fElapsedTime = 1.0f/60.0f;

		 if(SubSystem.MinimumMarker.Done)
		 {
		 SubSystem.FramePointer.Update(fElapsedTime);	


		 m_form.Update(SubSystem.FramePointer, fElapsedTime);
		 if (m_buttonDebug.IsPush())
		 {
		 m_isDispError = !m_isDispError;
		 }

		 m_gameMain.OnUpdate();

		 m_gameMain.OnRender();

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

		 fr.Begin();
		 fr.Draw(0.0f, 0.0f, 0.0f, String.format("FPS:%3.3f", 1.0f / fElapsedTime));
		 long freeMem = Runtime.getRuntime().freeMemory();
		 long totalMem = Runtime.getRuntime().totalMemory();
		 fr.Draw(0.0f, fr.m_fSize, 0.0f, String.format("Mem:%d/%d", (int) (totalMem - freeMem), (int) totalMem));
		 fr.Draw(0.0f, fr.m_fSize * 2.0f, 0.0f, String.format("ScanOut:%dx%d", 
		 (int) ScanOutWidth, (int) ScanOutHeight));

		 fr.Draw(0.0f, fr.m_fSize * 3, 0.0f, String.format("BackBuffer:%dx%d %dx%d", 
		 (int) m_gameMain.m_frameBuffer.Width,
		 (int) m_gameMain.m_frameBuffer.Height,
		 (int) ((RenderTexture) m_gameMain.m_frameBuffer.ColorRenderTexture).PotWidth,
		 (int) ((RenderTexture) m_gameMain.m_frameBuffer.ColorRenderTexture).PotHeight));

		 Float3 f3 = m_gameMain.m_player.m_trackBall.EulerAngularVelocity;

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
		 */
	}
}
