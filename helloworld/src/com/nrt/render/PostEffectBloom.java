package com.nrt.render;
import android.widget.*;
import android.opengl.*;

import com.nrt.math.*;

public class PostEffectBloom
{
	public FrameBuffer[] FrameBuffers = null;
	public FrameBuffer FrameBufferBloom = null;
	
	public Program m_programBloom = null;
	public Uniform u_matrixWorldViewProjection = null;
	public Uniform u_v2RcpPotSize = null;
	public Sampler u_texture0 = null;
	public VertexStream m_stream = null;
	public VertexBuffer[] m_vertexBuffers = null;
	
	public SamplerState m_samplerLinear = null;
	
	public PostEffectBloom( DelayResourceQueue drq, int nWidth, int nHeight, int nDepth )
	{
		FrameBuffers = new FrameBuffer[nDepth];
		for( int i = 0 ; i < nDepth ; i++ )
		{
			nWidth /= 2;
			nHeight /= 2;
			RenderTexture renderTexture = new RenderTexture
			(
				drq, RenderTextureFormat.RGBA, nWidth, nHeight 
			);

			FrameBuffers[i] = new FrameBuffer( drq, renderTexture, null, null );
		}		
		
		FrameBufferBloom = new FrameBuffer
		(
			drq,
			new RenderTexture( drq, RenderTextureFormat.RGBA, nWidth*2, nHeight*2 ),
			null, null
		);
			
		
		m_programBloom = new Program
		(
			drq,
			new AttributeBinding[]
			{
				new AttributeBinding( 0, "a_v3Position" ),
				new AttributeBinding( 1, "a_v2Texcoord" ),
			}
			,
			new VertexShader
			(
				drq,
				new  String[]
				{
					"attribute vec3 a_v3Position;",
					"attribute vec2 a_v2Texcoord;",
					"uniform mat4 u_matrixWorldViewProjection;",
					"varying vec2 v_v2Texcoord;",
        			"void main()",
					"{",
					"	gl_Position  = u_matrixWorldViewProjection*vec4(a_v3Position,1.0);",
					"	v_v2Texcoord = a_v2Texcoord;",
					"}",
				}
			),
			new FragmentShader
			(
				drq,
				new String[]
				{
					"precision mediump float;",
					"varying highp vec2 v_v2Texcoord;",
					"uniform vec2 u_v2RcpPotSize;",
					"uniform sampler2D u_texture0;",
					"void main()",
					"{",
					"	vec4 v4Color = vec4( 0.0, 0.0, 0.0, 0.0 );",
					"	float fPixels = 0.0;",
					"	for(int y = -2 ; y <= 2 ; y+=2 )",
					"	{",
					"		for( int x = -2 ; x <= 2 ; x+=2 )",
					"		{",
					"			vec2 v2Offset = vec2(x,y)*u_v2RcpPotSize;",
					"			v4Color += texture2D( u_texture0, v_v2Texcoord+v2Offset );",
					"			fPixels += 1.0;",
					"		}",
					"	}",
					"	gl_FragColor = v4Color/fPixels;",
					"}",
				}
			)
		);
		
		u_matrixWorldViewProjection = new Uniform( drq, m_programBloom, "u_matrixWorldViewProjection");
		u_v2RcpPotSize = new Uniform( drq, m_programBloom, "u_v2RcpPotSize" );
		u_texture0 = new Sampler( drq, m_programBloom, 0, "u_texture0" );
		
		m_stream = new VertexStream
		(
			new VertexAttribute[]
			{
				new VertexAttribute( 0, 3, GLES20.GL_FLOAT, false, 0 ),
				new VertexAttribute( 1, 2, GLES20.GL_FLOAT, false, 12 ),
			},
			20
		);
		
		m_samplerLinear = new SamplerState( MagFilter.Linear, MinFilter.Linear, Wrap.ClampToEdge, Wrap.ClampToEdge );
			/*
		for( int i = 0 ; i < nDepth ; i++ )
		{
			m_vertexBuffers[i] = new VertexBuffer
			(
				drq, new float[]
				{
				
				}
			);
		}
		*/
	}
	
	public void OnSurfaceChanged( int nWidth, int nHeight )
	{
		for( int i = 0 ; i < FrameBuffers.length ; i++ )
		{
			nWidth /= 2;
			nHeight /= 2;
			FrameBuffers[i].OnSurfaceChanged( nWidth, nHeight );
		}		
		FrameBufferBloom.OnSurfaceChanged( nWidth*2, nHeight*2 );
	}
	
	public void Render( BasicRender br, FrameBuffer frameBufferBase )
	{
		GLES20.glDisable(GLES20.GL_BLEND);
		GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
		GLES20.glDisable(GLES20.GL_DEPTH_TEST);
		GLES20.glDisable(GLES20.GL_CULL_FACE);
		
		br.SetColor(1.0f, 1.0f, 1.0f, 1.0f);
		
		Render( br, frameBufferBase, FrameBuffers[0] );
		for( int i = 0 ; i < FrameBuffers.length-1 ;i++ )
		{
			Render( br, FrameBuffers[i], FrameBuffers[i+1] );
		}
		
		//Render( br, FrameBuffers[3], frameBufferBase );
		
		RenderBloom( br, FrameBuffers[FrameBuffers.length-1], FrameBufferBloom );
		
		GLES20.glEnable(GLES20.GL_BLEND);
		GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE);
		br.SetColor(1.0f, 1.0f, 1.0f, 1.0f);
		
		Render( br, FrameBufferBloom, frameBufferBase );
		
		GLES20.glDisable(GLES20.GL_BLEND);
		GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
		
	}
	
	public void Render( BasicRender br, FrameBuffer frameBufferFrom, FrameBuffer frameBufferTo )
	{
		Render r = br.GetRender();
		MatrixCache mc = br.GetMatrixCache();
		
		r.SetFrameBuffer( frameBufferTo );
		GLES20.glViewport(0, 0, frameBufferTo.Width, frameBufferTo.Height);

		Float4x4 matrixOrtho = Float4x4.Local();
		Matrix.orthoM(matrixOrtho.Values, 0, 0, frameBufferTo.Width, 0, frameBufferTo.Height, -1.0f, 1.0f);

		mc.SetProjection(matrixOrtho);
		mc.SetView(Float4x4.Identity());
		mc.SetWorld(Float4x4.Identity());

		br.SetTexture(frameBufferFrom.ColorRenderTexture);
		br.SetSamplerState(m_samplerLinear);

		br.Begin(GLES20.GL_TRIANGLE_STRIP, BasicRender.EShader.ColorTexture);

		float u = ((float)frameBufferFrom.ColorRenderTexture.GetNpotWidth()) / ((float)frameBufferFrom.ColorRenderTexture.GetPotWidth());
		float v = ((float)frameBufferFrom.ColorRenderTexture.GetNpotHeight()) / ((float)frameBufferFrom.ColorRenderTexture.GetPotHeight());
		
		br.SetTexcoord(0.0f,   0.0f);   br.SetVertex(0.0f, 0.0f, 0.0f);
		br.SetTexcoord(0.0f,   v*2.0f); br.SetVertex(0.0f, frameBufferTo.Height*2.0f, 0.0f);
		br.SetTexcoord(u*2.0f, 0.0f);   br.SetVertex(frameBufferTo.Width*2.0f, 0.0f, 0.0f);
		//br.SetTexcoord(u,    v); br.SetVertex(ScanOutWidth, ScanOutHeight, 0.0f);
		br.End();
	}
	
	public void RenderBloom( BasicRender br, FrameBuffer frameBufferFrom, FrameBuffer frameBufferTo )
	{
		Render r = br.GetRender();
		MatrixCache mc = br.GetMatrixCache();

		r.SetFrameBuffer( frameBufferTo );
		GLES20.glViewport(0, 0, frameBufferTo.Width, frameBufferTo.Height);

		Float4x4 matrixOrtho = Float4x4.Local();
		Matrix.orthoM(matrixOrtho.Values, 0, 0, frameBufferTo.Width, 0, frameBufferTo.Height, -1.0f, 1.0f);

		mc.SetProjection(matrixOrtho);
		mc.SetView(Float4x4.Identity());
		mc.SetWorld(Float4x4.Identity());

		/*
		GLES20.glDisable(GLES20.GL_BLEND);
		GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
		GLES20.glDisable(GLES20.GL_DEPTH_TEST);
		GLES20.glDisable(GLES20.GL_CULL_FACE);
		*/
		
		r.SetProgram(m_programBloom);
		
		r.SetTexture( u_texture0, frameBufferFrom.ColorRenderTexture );
		r.SetSamplerState( u_texture0, m_samplerLinear );
		r.SetMatrix( u_matrixWorldViewProjection, mc.GetWorldViewProjection().Values );
		r.SetFloat2
		(
			u_v2RcpPotSize, 
			1.0f/(float)frameBufferFrom.ColorRenderTexture.PotWidth, 
			1.0f/(float)frameBufferFrom.ColorRenderTexture.PotHeight 
		);

		RingVertexBuffer vb = br.GetVertexBuffer();
		vb.Begin();
		
		float u = ((float)frameBufferFrom.ColorRenderTexture.GetNpotWidth()) / ((float)frameBufferFrom.ColorRenderTexture.GetPotWidth());
		float v = ((float)frameBufferFrom.ColorRenderTexture.GetNpotHeight()) / ((float)frameBufferFrom.ColorRenderTexture.GetPotHeight());

		vb.Add(0.0f, 0.0f, 0.0f); vb.Add(0.0f,   0.0f);   
		vb.Add(0.0f, frameBufferTo.Height*2.0f, 0.0f); vb.Add(0.0f,   v*2.0f); 
		vb.Add(frameBufferTo.Width*2.0f, 0.0f, 0.0f);  vb.Add(u*2.0f, 0.0f);  
		
		int start = vb.End();
		r.SetVertexBuffer(vb);
		
		r.EnableVertexStream( m_stream, start );
		r.DrawArrays( GLES20.GL_TRIANGLE_STRIP, 0, 3 );		
		r.DisableVertexStream();
		
		/*
		br.SetTexture(frameBufferFrom.ColorRenderTexture);
		br.SetSamplerState(m_samplerLinear);

		br.Begin(GLES20.GL_TRIANGLE_STRIP, BasicRender.EShader.ColorTexture);

		br.SetColor(1.0f, 1.0f, 1.0f, 1.0f);

		float u = ((float)frameBufferFrom.ColorRenderTexture.GetNpotWidth()) / ((float)frameBufferFrom.ColorRenderTexture.GetPotWidth());
		float v = ((float)frameBufferFrom.ColorRenderTexture.GetNpotHeight()) / ((float)frameBufferFrom.ColorRenderTexture.GetPotHeight());

		br.SetTexcoord(0.0f,   0.0f);   br.SetVertex(0.0f, 0.0f, 0.0f);
		br.SetTexcoord(0.0f,   v*2.0f); br.SetVertex(0.0f, frameBufferTo.Height*2.0f, 0.0f);
		br.SetTexcoord(u*2.0f, 0.0f);   br.SetVertex(frameBufferTo.Width*2.0f, 0.0f, 0.0f);
		//br.SetTexcoord(u,    v); br.SetVertex(ScanOutWidth, ScanOutHeight, 0.0f);
		br.End();
		*/
	}
}
