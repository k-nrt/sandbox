package com.nrt.basic;
import java.nio.*;
import android.opengl.*;

import com.nrt.render.*;

public class BitmapFont
{
	public FontCharInfo[] FontCharInfos = null;
	public ByteBuffer BufferImageRGBA = null;
	public int Width = 0;
	public int Height = 0;

	
	
	
	
	public static class Pattern
	{
		public String Targets = "";
		public String[] Image = null;

		public Pattern( String targets, String[] image )
		{
			Targets = targets;
			Image = image;
		}
	}

	public BitmapFont( Pattern[] patterns )
	{
		final int kTextureSize = 128;
		final int kFontHeight = 8;
		final int kFontWidth = 6;
		final int kPatternStride = kTextureSize/kFontWidth;

		FontCharInfos = new FontCharInfo[256];
		for(int i = 0 ; i< FontCharInfos.length ; i++)
		{
			int ox = (i%kPatternStride)*kFontWidth;
			int oy = (i/kPatternStride)*kFontHeight;
			FontCharInfos[i] = new FontCharInfo
			( 
				((float)ox)/((float) kTextureSize),
				((float)oy)/((float) kTextureSize),
				((float)kFontWidth)/((float) kTextureSize),
				0,0
			);			
		}
		
		BufferImageRGBA = ByteBuffer.allocateDirect( kTextureSize*kTextureSize*4 );
		Width = Height = kTextureSize;
		
		
		for( Pattern pattern : patterns )
		{
			for( int i = 0 ; i < pattern.Targets.length() ; i++ )
			{
				int c =  pattern.Targets.charAt(i);
				int ox = (c%kPatternStride)*kFontWidth;
				int oy = (c/kPatternStride)*kFontHeight;

				for( int y = 0 ; y < kFontHeight ; y++ )
				{
					for( int x = 0 ; x < kFontWidth ; x++ )
					{
						if( pattern.Image[y].charAt( x + i*kFontWidth ) == '-' )
						{
							BufferImageRGBA.putInt( ((ox+x)+(oy+y)*kTextureSize)*4,  0x0000 );
						}
						else
						{
							BufferImageRGBA.putInt( ((ox+x)+(oy+y)*kTextureSize)*4,  0xffffffff );
						}
					}
				}
			}

		}

		/*
		m_texture = new StaticTexture(
			drq,
			GLES20.GL_RGBA, nTextureSize, nTextureSize,
			GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, bufferFont, false );


		String[] strVsColorTexture = 
		{
			"uniform highp mat4 u_worldViewProjection;",
			"attribute highp vec3 a_position;",
			"attribute vec4 a_color;",
			"attribute highp vec2 a_texcoord;",
			"varying vec4 v_color;",
			"varying highp vec2 v_texcoord;",
			"void main(){",
			"gl_Position = u_worldViewProjection*vec4(a_position,1.0);",
			"v_color = a_color.xyzw;",
			"v_texcoord = a_texcoord;",
			"}",
		};

		String[] strFsColorTexture =
		{
			"precision highp float;",
			"varying vec4 v_color;",
			"varying highp vec2 v_texcoord;",
			"uniform sampler2D u_texture0;",
			"void main(){",
			"gl_FragColor = texture2D( u_texture0, v_texcoord )*v_color;",
			"}",
		};

		AttributeBinding[] abColorTextures =
		{
			new AttributeBinding(0, "a_position"),
			new AttributeBinding(1, "a_color"),
			new AttributeBinding(2, "a_texcoord"),
		};


		m_program = new Program( drq,
								abColorTextures,
								new VertexShader( drq, strVsColorTexture),
								new FragmentShader( drq, strFsColorTexture));

		u_worldViewProjection
			= new Uniform(drq, m_program, "u_worldViewProjection");
		u_sampler0 = new Sampler(drq,m_program, 0, "u_texture0");



		VertexAttribute[] va = 
		{
			new VertexAttribute( 0, 3, GLES20.GL_FLOAT, false, 0 ),
			new VertexAttribute( 1, 4, GLES20.GL_UNSIGNED_BYTE, true, 12 ),
			new VertexAttribute( 2, 2, GLES20.GL_FLOAT, false, 16 ),
		};

		m_stream = new VertexStream( va, 0 );

		m_samplerLinear = new SamplerState( MagFilter.Nearest, MinFilter.Nearest, Wrap.ClampToEdge, Wrap.ClampToEdge );

		m_basicRender = basicRender;
		*/

	}

	/*
	public void SetSize( float size )
	{
		m_fSize = size;
	}

	public void SetColor( int rgba )
	{
		m_rgbaFont = rgba;
	}

	public void Begin()
	{
		BasicRender br = m_basicRender;
		br.GetVertexBuffer().Begin();
		br.GetIndexBuffer().Begin();
		m_nbVertices = 0;
		m_nIndexOffset = 0;
	}

	public void Draw( float x, float y, float z, String strText )
	{
		SetVertices( x, y, z, strText, m_rgbaFont );
	}

	void SetVertices( float fX, float fY, float fZ, String strText, int rgba )
	{
		RingVertexBuffer vb = m_basicRender.GetVertexBuffer();
		RingIndexBuffer ib = m_basicRender.GetIndexBuffer();

		float xsize = (m_fSize*6.0f)/8.0f;
		float ysize = m_fSize;
		float usize = 6.0f/128.0f;
		float vsize = 8.0f/128.0f;
		float x = fX;

		for( int i = 0 ; i < strText.length() ; i++ )
		{
			int c = strText.charAt( i );
			float u = ((float)((c%16)*8))/128.0f;
			float v = ((float)((c/16)*8))/128.0f;


			vb.Add( x, fY, fZ );
			vb.Add( rgba );
			vb.Add( u, v );

			vb.Add( x, fY + ysize, fZ );
			vb.Add( rgba );
			vb.Add( u, v+ vsize );

			vb.Add( x + xsize, fY, fZ );
			vb.Add( rgba );
			vb.Add( u + usize, v );

			vb.Add( x + xsize, fY + ysize, fZ );
			vb.Add( rgba );
			vb.Add( u + usize, v + vsize );

			x += xsize;

			ib.Add( (short) (0 + m_nIndexOffset) );
			ib.Add( (short) (1 + m_nIndexOffset) );
			ib.Add( (short) (2 + m_nIndexOffset) );
			ib.Add( (short) (2 + m_nIndexOffset) );
			ib.Add( (short) (1 + m_nIndexOffset) );
			ib.Add( (short) (3 + m_nIndexOffset) );

			m_nbVertices += 6;
			m_nIndexOffset += 4;
		}

	}

	public void End()
	{
		int nVertexOffset = m_basicRender.GetVertexBuffer().End();
		int nIndexOffset = m_basicRender.GetIndexBuffer().End();

		if( m_nbVertices <= 0 )
		{
			return;
		}

		Render r = m_basicRender.GetRender();

		GLES20.glEnable( GLES20.GL_BLEND );
		GLES20.glBlendFunc( GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA );
		//GLES20.glDepthFunc( GLES20.GL_LEQUAL );
		//GLES20.glDisable( GLES20.GL_DEPTH_TEST );
		//GLES20.glDisable( GLES20.GL_CULL_FACE );

		r.SetProgram( m_program );

		r.SetMatrix( u_worldViewProjection, m_basicRender.GetMatrixCache().GetWorldViewProjection().Values );

		r.SetTexture( u_sampler0, m_texture );
		r.SetSamplerState( u_sampler0, m_samplerLinear );
		r.SetVertexBuffer( m_basicRender.GetVertexBuffer() );
		r.EnableVertexStream( m_stream, nVertexOffset );

		r.SetIndexBuffer( m_basicRender.GetIndexBuffer() );
		GLES20.glDrawElements( GLES20.GL_TRIANGLES, m_nbVertices, GLES20.GL_UNSIGNED_SHORT, nIndexOffset );

		r.DisableVertexStream( m_stream );
		GLES20.glBindBuffer( GLES20.GL_ELEMENT_ARRAY_BUFFER, 0 );

	}
	*/
}

