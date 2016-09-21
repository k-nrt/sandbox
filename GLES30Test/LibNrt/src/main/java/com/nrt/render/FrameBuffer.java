package com.nrt.render;

import android.opengl.GLES30;
//import android.opengl.*;
import com.nrt.framework.*;

public class FrameBuffer extends RenderResource
{
	public int Width = 0;
	public int Height = 0;

	public RenderBuffer ColorRenderBuffer = null;
	public RenderTexture ColorRenderTexture = null;
	public RenderBuffer DepthRenderBuffer = null;
	public RenderBuffer StencilRenderBuffer = null;

	public FrameBuffer
	(
		ResourceQueue drq,
		RenderBuffer color,
		RenderBuffer depth, 
		RenderBuffer stencil
	)
	//throws ThreadForceDestroyException
	{
		Name = 0;

		ColorRenderBuffer = color;
		ColorRenderTexture = null;
		DepthRenderBuffer = depth;
		StencilRenderBuffer = stencil;

		if( color != null )
		{				
			Width = color.Width;
			Height = color.Height;
		}

		if( depth != null)
		{				
			Width = depth.Width;
			Height = depth.Height;
		}

		if( stencil != null )
		{				
			Width = stencil.Width;
			Height = stencil.Height;
		}	

		Subsystem.Log.WriteLine( String.format( "new FrameBuffer() %dx%d", Width, Height ) );

		drq.Add(this);
	}

	public FrameBuffer
	(
		ResourceQueue drq,
		RenderTexture color,
		RenderBuffer depth, 
		RenderBuffer stencil
	)
	{
		Name = 0;

		ColorRenderBuffer = null;
		ColorRenderTexture = color;
		DepthRenderBuffer = depth;
		StencilRenderBuffer = stencil;

		if( color != null )
		{				
			Width = color.PotWidth;
			Height = color.PotHeight;
		}

		if( depth != null )
		{				
			Width = depth.Width;
			Height = depth.Height;
		}

		if( stencil != null )
		{				
			Width = stencil.Width;
			Height = stencil.Height;
		}	

		Subsystem.Log.WriteLine( String.format( "new FrameBuffer() %dx%d", Width, Height ) );

		drq.Add(this);
	}

	@Override
	public void Apply()
	{
		DeleteFrameBuffer( Name, ColorRenderTexture, ColorRenderBuffer, DepthRenderBuffer, StencilRenderBuffer );

		Name = CreateFrameBuffer
		(
			ColorRenderTexture, 
			ColorRenderBuffer,
			DepthRenderBuffer, 
			StencilRenderBuffer
		);

		Subsystem.Log.WriteLine( String.format( "FrameBuffer.Apply() %d %dx%d", Name, Width, Height ) );
	}	

	public void OnSurfaceChanged( int width, int height )
	{
		OnSurfaceChanged( width, height, true, true, true );
	}

	public void OnSurfaceChanged
	(
		int width, int height,
		boolean resizeColorBuffer,
		boolean resizeDepthBuffer,
		boolean resizeStencilBuffer
	)
	{
		DeleteFrameBuffer( Name, ColorRenderTexture, ColorRenderBuffer, DepthRenderBuffer, StencilRenderBuffer );
		Width = width;
		Height = height;

		if( resizeColorBuffer )
		{
			if(ColorRenderTexture!=null)
			{
				ColorRenderTexture.OnSurfaceChanged(width,height);

				width = ColorRenderTexture.PotWidth;
				height = ColorRenderTexture.PotHeight;
			}

			if(ColorRenderBuffer!=null)
			{
				ColorRenderBuffer.OnSurfaceChanged(width,height);
			}
		}

		if( resizeDepthBuffer )
		{
			if(DepthRenderBuffer!=null)
			{
				DepthRenderBuffer.OnSurfaceChanged(width,height);
			}
		}

		if( resizeStencilBuffer )
		{
			if(StencilRenderBuffer!=null)
			{
				StencilRenderBuffer.OnSurfaceChanged(width,height);
			}
		}

		Name = CreateFrameBuffer( ColorRenderTexture, ColorRenderBuffer, DepthRenderBuffer, StencilRenderBuffer );
		Subsystem.Log.WriteLine( String.format( "FrameBuffer.OnSurfaceChanged( %d, %d )", Width, Height ) );
	}

	protected static int CreateFrameBuffer( RenderTexture colorTexture, RenderBuffer color, RenderBuffer depth, RenderBuffer stencil )
	{
		int[] names = {0};
		GLES30.glGenFramebuffers( 1, names, 0 );

		GLES30.glBindFramebuffer( GLES30.GL_FRAMEBUFFER, names[0] );

		if( colorTexture != null )
		{
			GLES30.glFramebufferTexture2D( 
				GLES30.GL_FRAMEBUFFER, GLES30.GL_COLOR_ATTACHMENT0,
				GLES30.GL_TEXTURE_2D, colorTexture.Name, 0 );
		}
		else if( color != null )
		{
			GLES30.glFramebufferRenderbuffer( 
				GLES30.GL_FRAMEBUFFER, GLES30.GL_COLOR_ATTACHMENT0,
				GLES30.GL_RENDERBUFFER, color.Name );
		}

		if( depth != null )
		{
			GLES30.glFramebufferRenderbuffer(
				GLES30.GL_FRAMEBUFFER, GLES30.GL_DEPTH_ATTACHMENT,
				GLES30.GL_RENDERBUFFER, depth.Name );
		}

		if( stencil != null )
		{
			GLES30.glFramebufferRenderbuffer(
				GLES30.GL_FRAMEBUFFER, GLES30.GL_STENCIL_ATTACHMENT,
				GLES30.GL_RENDERBUFFER, stencil.Name );
		}

		GLES30.glBindFramebuffer( GLES30.GL_FRAMEBUFFER, 0 );

		return names[0];
	}

	protected static void DeleteFrameBuffer( int name, RenderTexture colorTexture, RenderBuffer color, RenderBuffer depth, RenderBuffer stencil )
	{		
		if( name <= 0 )
		{
			return;
		}

		GLES30.glBindFramebuffer( GLES30.GL_FRAMEBUFFER, name );

		if( colorTexture != null )
		{
			GLES30.glFramebufferTexture2D( 
				GLES30.GL_FRAMEBUFFER, GLES30.GL_COLOR_ATTACHMENT0,
				GLES30.GL_TEXTURE_2D, 0, 0 );
		}
		else if( color != null )
		{
			GLES30.glFramebufferRenderbuffer( 
				GLES30.GL_FRAMEBUFFER, GLES30.GL_COLOR_ATTACHMENT0,
				GLES30.GL_RENDERBUFFER, 0 );
		}

		if( depth != null )
		{
			GLES30.glFramebufferRenderbuffer( 
				GLES30.GL_FRAMEBUFFER, GLES30.GL_DEPTH_ATTACHMENT,
				GLES30.GL_RENDERBUFFER, 0 );
		}

		if( stencil != null )
		{
			GLES30.glFramebufferRenderbuffer( 
				GLES30.GL_FRAMEBUFFER, GLES30.GL_STENCIL_ATTACHMENT,
				GLES30.GL_RENDERBUFFER, 0 );
		}
		GLES30.glBindFramebuffer( GLES30.GL_FRAMEBUFFER, 0 );

		int[] names = {name};
		GLES30.glDeleteFramebuffers( 1, names, 0 );
	}
}

