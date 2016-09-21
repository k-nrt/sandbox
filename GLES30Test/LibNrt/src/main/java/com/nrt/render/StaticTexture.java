package com.nrt.render;

import java.nio.Buffer;
import android.opengl.GLES30;
import android.opengl.GLUtils;
import android.graphics.Bitmap;

import com.nrt.basic.RawImage;
import android.media.effect.*;
import com.nrt.framework.*;
import java.nio.*;

public class StaticTexture extends RenderResource implements Texture
{
	@Override
	public int GetTextureName()
	{
		// TODO: Implement this method
		return Name;
	}

	@Override
	public int GetPotWidth()
	{
		// TODO: Implement this method
		return PotWidth;
	}

	@Override
	public int GetPotHeight()
	{
		// TODO: Implement this method
		return PotHeight;
	}

	@Override
	public int GetNpotWidth()
	{
		// TODO: Implement this method
		return 0;
	}

	@Override
	public int GetNpotHeight()
	{
		// TODO: Implement this method
		return 0;
	}

	public int m_internalFormat = 0;
	public int m_sourceFormat = 0;
	public int m_sourceType = 0;
	public java.nio.Buffer m_buffer = null;
	public Bitmap m_bitmap = null;

	public int PotWidth = 0;
	public int PotHeight = 0;
	public int NpotWidth = 0;
	public int NpotHeight = 0;

	public boolean m_isGenerateMipmap;

	public void Initialize
	(
		ResourceQueue queue, 
		int internalFormat,
		int nPotWidth,
		int nPotHeight,
		int sourceFormat,
		int sourceType,
		java.nio.Buffer buffer,
		boolean isGenarateMipmap 
	)
	{
		m_internalFormat = internalFormat;
		PotWidth = nPotWidth;
		PotHeight = nPotHeight;
		m_sourceFormat = sourceFormat;
		m_sourceType = sourceType;
		m_buffer = buffer;
		m_isGenerateMipmap = isGenarateMipmap;

		if( queue != null )
		{
			queue.Add( this );
		}
		else
		{
			Subsystem.Log.WriteLine( String.format( "texture null queue %dx%d", PotWidth, PotHeight ));
		}
	}

	public void Initialize
	(
		ResourceQueue queue, 
		Bitmap bitmap,
		boolean isGenarateMipmap
	)
	{
		PotWidth = bitmap.getWidth();
		PotHeight = bitmap.getHeight();
		m_bitmap = bitmap;
		m_isGenerateMipmap = isGenarateMipmap;

		if( queue != null )
		{
			queue.Add( this );
		}
		else
		{
			Subsystem.Log.WriteLine( String.format( "texture null queue %dx%d", m_bitmap.getWidth(), m_bitmap.getHeight() ));
		}

	}

	public StaticTexture()
	{}

	public StaticTexture
	(
		ResourceQueue queue,
		RawImage image,
		boolean isGenerateMipmap
	)
	{
		Initialize
		( 
			queue,
			image.Format,
			image.Width,
			image.Height,
			image.Format,
			image.Type,
			image.Pixels,
			isGenerateMipmap
		);
	}

	public StaticTexture
	(
		ResourceQueue queue,
		Bitmap bitmap,
		boolean isGenerateMipmap 
	)
	{
		Initialize
		( 
			queue,
			bitmap,
			isGenerateMipmap 
		);
	}

	public StaticTexture
	(
		ResourceQueue queue, 
		int eFormat, int nWidth, int nHeight, 
		int eSrcFormat, int eSrcType, Buffer buffer, boolean isGenerateMipmap
	)
	{
		Initialize( queue,
				   eFormat, nWidth, nHeight,
				   eSrcFormat, eSrcType, buffer, isGenerateMipmap );
	}

	public static int GetPowerOfTwo( int a )
	{
		int result = 0;
		for( result = 1 ; result < a ; result <<= 1 );
		return result;
	}

	@Override
	public void Apply()
	{		
		int[] names = { 0 };
		GLES30.glGenTextures(1, names, 0);
		Name = names[0];

		Subsystem.Log.WriteLine( String.format( "StaticTexture applied %d %dx%d", Name, PotWidth, PotHeight ) );

		GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, Name);

		if( m_bitmap != null )
		{
			GLUtils.texImage2D(GLES30.GL_TEXTURE_2D, 0, m_bitmap, 0);
			Subsystem.Log.WriteLine( "Texture source is bitmap" );
		}
		else
		{
			m_buffer.position(0);
			GLES30.glTexImage2D( GLES30.GL_TEXTURE_2D,
								0, m_internalFormat,
								PotWidth, PotHeight, 0,
								m_sourceFormat, m_sourceType, m_buffer );

			Subsystem.Log.WriteLine( String.format( "Texture source is buffer %d", m_buffer.capacity() ) );
		}

		if( m_isGenerateMipmap )
		{
			GLES30.glGenerateMipmap( GLES30.GL_TEXTURE_2D );
		}

		GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_NEAREST);
		GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_NEAREST);
		GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_S, GLES30.GL_REPEAT);
		GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_T, GLES30.GL_REPEAT);

		GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, 0);
	}
}

