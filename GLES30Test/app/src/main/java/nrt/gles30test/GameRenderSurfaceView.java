package nrt.gles30test;
import android.opengl.GLSurfaceView;
import android.opengl.*;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import com.nrt.framework.Subsystem;

//////////////////////////////////////////////////////////////////////////
///GLSurfaceViewの拡張 

public class GameRenderSurfaceView extends GLSurfaceView
{
	public GameSurfaceRenderer m_surfaceRenderer = null;

	public GameRenderSurfaceView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		Initialize();
	}

	public GameRenderSurfaceView(Context context) 
	{ 
		super(context);
		Initialize();
	}

	public void Initialize()
	{
		this.setEGLContextClientVersion(3);
		m_surfaceRenderer = new GameSurfaceRenderer();
		this.setRenderer(m_surfaceRenderer);
	}

	public boolean onTouchEvent(MotionEvent me)
	{

		//DevicePointer.OnTouchEvent(me);
		return true;
	}

	@Override
	public void onResume()
	{
		// TODO: Implement this method
		Subsystem.Log.Write( "onResume()" );
		super.onResume();
	}

	@Override
	public void onPause()
	{
		// TODO: Implement this method
		Subsystem.Log.Write( "onPause()" );
		super.onPause();
	}

	@Override
	protected void onDetachedFromWindow()
	{
		// TODO: Implement this method
		super.onDetachedFromWindow();
		Subsystem.Log.WriteLine( "onDetachedFromWindow" );
	}



	public void onDestroy()
	{
		//super.onDestroy();
	}
}

