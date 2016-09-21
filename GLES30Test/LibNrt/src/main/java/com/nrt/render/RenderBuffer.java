package com.nrt.render;
import android.opengl.GLES30;
import java.text.*;
import com.nrt.framework.Subsystem;

public class RenderBuffer extends RenderResource
{
	public RenderBufferFormat RenderBufferFormat = RenderBufferFormat.RGBA4;
	public int Width = 0;
	public int Height = 0;

	public RenderBuffer( ResourceQueue drq, RenderBufferFormat eFormat,  int width, int height )
	{
		RenderBufferFormat = eFormat;
		Name = 0;	
		Width = width;
		Height = height;

		drq.Add( this );
	}

	@Override
	public void Apply()
	{
		DeleteRenderBuffer(Name);
		Name = CreateRenderBuffer( RenderBufferFormat, Width, Height );
		Subsystem.Log.WriteLine( String.format( "RenderBuffer.Apply() %d %s %dx%d", Name, RenderBufferFormat.toString(),  Width, Height ) );
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
		GLES30.glGenRenderbuffers( 1, names, 0 );

		GLES30.glBindRenderbuffer( GLES30.GL_RENDERBUFFER, names[0] );
		GLES30.glRenderbufferStorage( 
			GLES30.GL_RENDERBUFFER, 
			eFormat.Value,
			width, height );

		GLES30.glBindRenderbuffer( GLES30.GL_RENDERBUFFER, 0 );

		return names[0];
	}

	protected static void DeleteRenderBuffer( int name )
	{
		if( name <= 0 )
		{
			return;
		}

		GLES30.glBindRenderbuffer( GLES30.GL_RENDERBUFFER, 0 );

		int[] names = {name};
		GLES30.glDeleteRenderbuffers( 1, names, 0 );

	}
}


