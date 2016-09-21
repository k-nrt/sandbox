package com.nrt.render;
import android.opengl.GLES20;
import java.text.*;
import com.nrt.framework.SubSystem;

public class RenderBuffer extends RenderResource
{
	public RenderBufferFormat RenderBufferFormat = RenderBufferFormat.RGBA4;
	public int Width = 0;
	public int Height = 0;
	
	public RenderBuffer( DelayResourceQueue drq, RenderBufferFormat eFormat,  int width, int height )
		//throws ThreadForceDestroyException
	{
		RenderBufferFormat = eFormat;
		Name = 0;//CreateRenderBuffer( eFormat, width, height );		
		Width = width;
		Height = height;
		
		drq.Add( this );
	}

	@Override
	public void Apply()
	{
		DeleteRenderBuffer(Name);
		Name = CreateRenderBuffer( RenderBufferFormat, Width, Height );
		SubSystem.Log.WriteLine( String.format( "RenderBuffer.Apply() %d %s %dx%d", Name, RenderBufferFormat.toString(),  Width, Height ) );
	}	
	
	
	
	
	public void OnSurfaceChanged( int width, int height )
	{
		DeleteRenderBuffer( Name );		
		Name = CreateRenderBuffer( RenderBufferFormat, width, height );
		Width = width;
		Height = height;
	}
	
	protected static int CreateRenderBuffer( RenderBufferFormat eFormat, int width, int height )
	{
		int[] names = {0};
		GLES20.glGenRenderbuffers( 1, names, 0 );
		
		GLES20.glBindRenderbuffer( GLES20.GL_RENDERBUFFER, names[0] );
		GLES20.glRenderbufferStorage( 
			GLES20.GL_RENDERBUFFER, 
			eFormat.Value,
			width, height );

		GLES20.glBindRenderbuffer( GLES20.GL_RENDERBUFFER, 0 );
		
		return names[0];
	}
	
	protected static void DeleteRenderBuffer( int name )
	{
		if( name <= 0 )
		{
			return;
		}
		
		GLES20.glBindRenderbuffer( GLES20.GL_RENDERBUFFER, 0 );
		
		int[] names = {name};
		GLES20.glDeleteRenderbuffers( 1, names, 0 );
		
	}
}

