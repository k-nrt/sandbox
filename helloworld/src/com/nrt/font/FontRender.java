package com.nrt.font;

import com.nrt.framework.SubSystem;
import android.opengl.GLES20;
import com.nrt.math.Float3;
import com.nrt.render.*;
import com.nrt.framework.*;

public class FontRender
{
	public Font m_font = null;
	public float m_fSize = 16.0f;
	public int m_rgbaFontColor = 0xffffffff;
	public int m_rgbaBoarderColor = 0x000000ff;

	public VertexStream m_stream = null;

	public BasicRender m_basicRender = null;
	public int m_nbVertices = 0;
	public int m_nIndexOffset = 0;

	public Program m_program = null;
	public Uniform u_worldViewProjection = null;
	public Sampler u_texture0 = null;
	public Sampler u_texture1 = null;
	public Sampler u_texture2 = null;
	public Sampler u_texture3 = null;
	public Sampler u_texture4 = null;
	public Sampler u_texture5 = null;
	public Sampler u_texture6 = null;
	public Sampler u_texture7 = null;

	public SamplerState m_samplerState = null;
	//Sampler u_sampler0 = null;

	public FontRender( DelayResourceQueue drq, BasicRender basicRender )// throws ThreadForceDestroyException
	{
		m_basicRender = basicRender;

		AttributeBinding[] ab =
		{
			new AttributeBinding( 0, "a_position" ),
			new AttributeBinding( 1, "a_color" ),
			new AttributeBinding( 2, "a_texcoord"),
		};

		m_program = new Program( drq, ab,
								new VertexShader( drq, SubSystem.Loader.LoadTextFile( "font_render.vs" ) ),
								new FragmentShader( drq, SubSystem.Loader.LoadTextFile( "font_render.fs" ) ) );

		u_worldViewProjection = new Uniform( drq, m_program, "u_worldViewProjection" );

		u_texture0 = new Sampler( drq, m_program, 0, "u_texture0" );
		u_texture1 = new Sampler( drq, m_program, 1, "u_texture1" );
		u_texture2 = new Sampler( drq, m_program, 2, "u_texture2" );
		u_texture3 = new Sampler( drq, m_program, 4, "u_texture3" );
		u_texture4 = new Sampler( drq, m_program, 5, "u_texture4" );
		u_texture5 = new Sampler( drq, m_program, 6, "u_texture5" );
		u_texture6 = new Sampler( drq, m_program, 7, "u_texture6" );
		u_texture7 = new Sampler( drq, m_program, 8, "u_texture7" );
		
		m_samplerState = new SamplerState( MagFilter.Linear, MinFilter.Linear, Wrap.ClampToEdge, Wrap.ClampToEdge );

		VertexAttribute[] va = 
		{
			new VertexAttribute( 0, 3, GLES20.GL_FLOAT, false, 0 ),
			new VertexAttribute( 1, 4, GLES20.GL_UNSIGNED_BYTE, true, 12 ),
			new VertexAttribute( 2, 4, GLES20.GL_FLOAT, false, 16 ),
		};

		m_stream = new VertexStream( va, 0 );
	}

	public void SetFont( Font font )
	{
		m_font = font;
	}

	public void SetSize( float fSize )
	{
		m_fSize = fSize;
	}

	public void SetBoarderColor( int rgba )
	{
		m_rgbaBoarderColor = rgba;
	}

	public void SetFontColor( int rgba )
	{
		m_rgbaFontColor = rgba;
	}

	public void Begin()
	{
		BasicRender br = m_basicRender;
		br.GetVertexBuffer().Begin();
		br.GetIndexBuffer().Begin();
		m_nbVertices = 0;
		m_nIndexOffset = 0;
	}

	public void Draw( Float3 f3Position, String strText )
	{
		Draw( f3Position.X, f3Position.Y, f3Position.Z, strText );
		//SetVertices( f3Position, strText, m_rgbaFontColor, 4.0f );
	}

	public void Draw( float fX, float fY, float fZ, String strText )
	{
		SetVertices( fX, fY, fZ, strText, m_rgbaBoarderColor, 0.0f );
		SetVertices( fX, fY, fZ, strText, m_rgbaFontColor, 2.0f );
		//SetVertices( Float3.Add( f3Position, new Float3( 0,16,0 )), strText, m_rgbaFontColor, 4.0f );

	}

	void SetVertices( float fX, float fY, float fZ, String strText, int rgba, float tex )
	{
		RingVertexBuffer vb = m_basicRender.GetVertexBuffer();
		RingIndexBuffer ib = m_basicRender.GetIndexBuffer();

		float size = m_fSize*(float)m_font.m_nRectSize/(float)m_font.m_nFontSize;
		float uvsize = (float)m_font.m_nRectSize/(float)m_font.m_nTextureSize;
		float x = fX;

		for( int i = 0 ; i < strText.length() ; i++ )
		{
			int c = strText.charAt( i );

			FontCharInfo info = m_font.m_infos[c];

			if( info == null )
			{
				continue;
			}

			vb.Add( x, fY, fZ );
			vb.Add( rgba );
			vb.Add( info.U, info.V, info.Texture + tex, info.Channel );

			vb.Add( x, fY + size, fZ );
			vb.Add( rgba );
			vb.Add( info.U, info.V + uvsize, info.Texture + tex, info.Channel  );

			vb.Add( x + size, fY, fZ );
			vb.Add( rgba );
			vb.Add( info.U + uvsize, info.V, info.Texture + tex, info.Channel  );

			vb.Add( x + size, fY + size, fZ );
			vb.Add( rgba );
			vb.Add( info.U + uvsize, info.V + uvsize, info.Texture + tex, info.Channel  );

			x += info.Width;

			ib.Add( (short) (0 + m_nIndexOffset) );
			ib.Add( (short) (1 + m_nIndexOffset) );
			ib.Add( (short) (2 + m_nIndexOffset) );
			ib.Add( (short) (1 + m_nIndexOffset) );
			ib.Add( (short) (2 + m_nIndexOffset) );
			ib.Add( (short) (3 + m_nIndexOffset) );

			m_nbVertices += 6;
			m_nIndexOffset += 4;
		}

	}

	public void End()
	{

		int nVertexOffset = m_basicRender.GetVertexBuffer().End();
		int nIndexOffset = m_basicRender.GetIndexBuffer().End();
		
		if( m_font.m_textureFonts[0] == null )
		{
			return;
		}
		
		if(m_font.m_textureFonts[0].Name <= 0 )
		{
			return;
		}
		

		if( m_nbVertices <= 0 )
		{
			return;
		}

		Render r = m_basicRender.GetRender();

		GLES20.glEnable( GLES20.GL_BLEND );
		GLES20.glBlendFunc( GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA );
		GLES20.glDepthFunc( GLES20.GL_LEQUAL );

		r.SetProgram( m_program );

		r.SetMatrix( u_worldViewProjection, m_basicRender.GetMatrixCache().GetWorldViewProjection().Values );
		/*
		 r.Bind( u_texture0, m_font.m_textureBoarders[0] );
		 r.Bind( u_texture1, m_font.m_textureBoarders[1] );
		 r.Bind( u_texture2, m_font.m_textureBoarders[2] );
		 r.Bind( u_texture3, m_font.m_textureBoarders[3] );
		 r.Bind( u_texture0, m_font.m_textureFonts[0] );
		 r.Bind( u_texture5, m_font.m_textureFonts[1] );
		 r.Bind( u_texture6, m_font.m_textureFonts[2] );
		 r.Bind( u_texture7, m_font.m_textureFonts[3] );
		 */
		r.SetTexture( u_texture0, m_font.m_textureBoarders[0] );
		r.SetTexture( u_texture1, m_font.m_textureBoarders[1] );
		r.SetTexture( u_texture2, m_font.m_textureFonts[0] );
		r.SetTexture( u_texture3, m_font.m_textureFonts[1] );
		
		r.SetSamplerState( u_texture0, m_samplerState );
		r.SetSamplerState( u_texture1, m_samplerState );
		r.SetSamplerState( u_texture2, m_samplerState );
		r.SetSamplerState( u_texture3, m_samplerState );
		
		/*

		 r.Bind( u_sampler0, m_font.m_textureBoarders[0] );

		 //r.Bind( GLES20.GL_TEXTURE0, m_font.m_textureBoarders[0] );
		 r.Bind( GLES20.GL_TEXTURE1, m_font.m_textureBoarders[1] );
		 r.Bind( GLES20.GL_TEXTURE2, m_font.m_textureBoarders[2] );
		 r.Bind( GLES20.GL_TEXTURE3, m_font.m_textureBoarders[3] );
		 r.Bind( GLES20.GL_TEXTURE4, m_font.m_textureFonts[0] );
		 r.Bind( GLES20.GL_TEXTURE5, m_font.m_textureFonts[1] );
		 r.Bind( GLES20.GL_TEXTURE6, m_font.m_textureFonts[2] );
		 r.Bind( GLES20.GL_TEXTURE7, m_font.m_textureFonts[3] );
		 //GLES20.glUniform1i( u_texture0.Index, 0 );
		 GLES20.glUniform1i( u_texture1.Index, 1 );
		 GLES20.glUniform1i( u_texture2.Index, 2 );
		 GLES20.glUniform1i( u_texture3.Index, 3 );
		 GLES20.glUniform1i( u_texture4.Index, 4 );
		 GLES20.glUniform1i( u_texture5.Index, 5 );
		 GLES20.glUniform1i( u_texture6.Index, 6 );
		 GLES20.glUniform1i( u_texture7.Index, 7 );
		 */
		r.SetVertexBuffer( m_basicRender.GetVertexBuffer() );
		r.EnableVertexStream( m_stream, nVertexOffset );

		r.SetIndexBuffer( m_basicRender.GetIndexBuffer() );
		GLES20.glDrawElements( GLES20.GL_TRIANGLES, m_nbVertices, GLES20.GL_UNSIGNED_SHORT, nIndexOffset );

		r.DisableVertexStream( m_stream );
		GLES20.glBindBuffer( GLES20.GL_ELEMENT_ARRAY_BUFFER, 0 );

		r.SetTexture( u_texture0, null );
		r.SetTexture( u_texture1, null );
		r.SetTexture( u_texture2, null );
		r.SetTexture( u_texture3, null );
		//r.Unbind( u_texture4 );
		//r.Unbind( u_texture5 );
		//r.Unbind( u_texture6 );
		//r.Unbind( u_texture7 );
	}
}
