package com.nrt.cardboardstar;
import com.nrt.render.*;
import android.opengl.*;
import com.nrt.math.*;

public class GameLightGlare
{
	
	public Program m_program = null;
	public VertexStream m_stream = null;
	public Uniform u_matrixWorldViewProjection = null;
	public Sampler u_textureColor = null;
	public Sampler u_textureOcclusion = null;
	
	public static class Glare
	{
		//public final Float3 Position = new Float3();
		public boolean IsActive = false;
		public OcclusionQuery.Point Point = null;
		
		public Glare( OcclusionQuery.Point point )
		{
			Point = point;
		}
	}
	
	public Glare[] Glares = null;
	
	public OcclusionQuery m_occlusionQuery = null;
	/*
	public float[] m_arrayLocalX = new float[3*4];
	public float[] m_arrayLocalY = new float[3*4];
	
	public float[] m_arrayTexcoords = 
	{
		0.0f, 0.0f,
		0.0f, 1.0f,
		1.0f, 1.0f,
		1.0f, 0.0f,		
	};
	*/
	public Texture Texture = null;
	
	public FrameBuffer FrameBuffer = null;
	public SamplerState m_samplerLinear = null;
	
	public DelayResourceQueueMarker Marker = new DelayResourceQueueMarker("LightGlare");
	
	public GameLightGlare( DelayResourceQueue drq, int nbGlares, Texture texture, FrameBuffer frameBuffer )
	{
		m_program = new Program
		(
			drq,
			new AttributeBinding[]
			{
				new AttributeBinding( 0, "a_v3Position" ),
				new AttributeBinding( 1, "a_v4Color" ),
				new AttributeBinding( 2, "a_v4Texcoord" ),
			},
			new VertexShader
			(
				drq,
				new String[]
				{
					"attribute vec3 a_v3Position;",
					"attribute vec4 a_v4Color;",
					"attribute vec4 a_v4Texcoord;",
					"uniform mat4 u_matrixWorldViewProjection;",
					"varying vec4 v_v4Color;",
					"varying vec4 v_v4Texcoord;",
					"void main()",
					"{",
					"	gl_Position = u_matrixWorldViewProjection*vec4(a_v3Position,1.0);",
					"	v_v4Color = a_v4Color;",
					"	v_v4Texcoord = a_v4Texcoord;",
					"}",
				}
			),
			new FragmentShader
			(
				drq,
				new String[]
				{
					"precision mediump float;",
					"varying vec4 v_v4Color;",
					"varying vec4 v_v4Texcoord;",
					"uniform sampler2D u_textureColor;",
					"uniform sampler2D u_textureOcclusion;",
					"void main()",
					"{",
					"	vec4 v4Occlusion = texture2D( u_textureOcclusion, v_v4Texcoord.zw );",
					"	if( 0.0 < v4Occlusion.w )",
					"	{",
					"		gl_FragColor = texture2D( u_textureColor, v_v4Texcoord.xy )*v_v4Color;",
					"	}",
					"	else",
					"	{",
					"		discard;",
					"	}",
					"}",
				}
			)
		);
		
		u_matrixWorldViewProjection = new Uniform( drq, m_program, "u_matrixWorldViewProjection" );
		
		u_textureColor = new Sampler( drq, m_program, 0, "u_textureColor" );
		u_textureOcclusion = new Sampler( drq, m_program, 1, "u_textureOcclusion" );
		
		m_stream = new VertexStream
		(
			new VertexAttribute[]
			{
				new VertexAttribute( 0, 3, GLES20.GL_FLOAT, false, 0 ),
				new VertexAttribute( 1, 4, GLES20.GL_FLOAT, false, 12 ),
				new VertexAttribute( 2, 4, GLES20.GL_FLOAT, false, 28 ),
			},
			44
		);
		
		m_occlusionQuery = new OcclusionQuery( drq, nbGlares );//, frameBuffer );
		
		Glares = new Glare[nbGlares];
		for( int i = 0 ; i < Glares.length ; i++ )
		{
			Glares[i] = new Glare( m_occlusionQuery.m_points[i] );
		}
		
		Texture = texture;
		FrameBuffer = new FrameBuffer( drq,
			new RenderTexture( drq, RenderTextureFormat.RGBA,
				frameBuffer.Width/2, frameBuffer.Height/2),null,null);
		m_samplerLinear = new SamplerState( MagFilter.Linear, MinFilter.Linear, Wrap.ClampToEdge, Wrap.ClampToEdge );
		drq.Add(Marker);
	}
	
	public void OnSurfaceChanged( FrameBuffer frameBuffer )
	{
		//m_occlusionQuery.OnSurfaceChanged( frameBuffer );
		FrameBuffer.OnSurfaceChanged( frameBuffer.Width/2, frameBuffer.Height/2 );
	}
	
	public void Update( int i, boolean isActive, Float3 f3Position )
	{
		Glares[i].IsActive = isActive;
		m_occlusionQuery.Update( i, f3Position );
	}
	
	public BlendState m_blendStateAdd = new BlendState(true, BlendFunc.One, BlendFunc.One);
	public DepthStencilState m_depthStencilStateDisable = 
	new DepthStencilState
	(
		false, DepthFunc.Less, false
	);
	
	public RasterizerState m_rasterizerStateDisableCullFace = new RasterizerState
	(
		false, CullFace.Back, FrontFace.CCW 
	);
	
	public void Render(BasicRender br, GameViewPoint viewPoint, FrameBuffer frameBuffer )
	{
		if(!Marker.Done)
		{
			return;
		}
		
		GfxCommandContext gfxc = br.GetCommandContext();
		MatrixCache mc = br.GetMatrixCache();
		FrameLinearVertexBuffer vb = br.GetVertexBuffer();
		FrameLinearIndexBuffer ib = br.GetIndexBuffer();
		
		if( gfxc == null || mc == null || vb == null || ib == null )
		{
			return;
		}
		
		m_occlusionQuery.RenderPointsToFrameBuffer( gfxc, mc, vb, frameBuffer );
		
		Float3 f3X = viewPoint.Transform.GetAxisX( Float3.Local() );
		Float3 f3Y = viewPoint.Transform.GetAxisY( Float3.Local() );

		float fSize = 1000.0f;
		Float3 f30 = Float3.Mad( Float3.Local(), f3X, -fSize, Float3.Mul( Float3.Local(), f3Y, -fSize ) );
		Float3 f31 = Float3.Mad( Float3.Local(), f3X, -fSize, Float3.Mul( Float3.Local(), f3Y,  fSize ) );
		Float3 f32 = Float3.Mad( Float3.Local(), f3X,  fSize, Float3.Mul( Float3.Local(), f3Y, -fSize ) );
		Float3 f33 = Float3.Mad( Float3.Local(), f3X,  fSize, Float3.Mul( Float3.Local(), f3Y,  fSize ) );
		
		int nbPoints = 0;
		int vbOffset = vb.GetOffset();
		int ibOffset = ib.GetOffset();
		vb.Begin();
		ib.Begin();
		for( final Glare glare : Glares )
		{
			if( glare.IsActive == false )
			{
				continue;
			}
			
			OcclusionQuery.Point point = glare.Point;
			if( point.Visiblity )
			{
				float u = point.m_f3ScreenPosition.X/(float)frameBuffer.ColorRenderTexture.PotWidth;
				float v = point.m_f3ScreenPosition.Y/(float)frameBuffer.ColorRenderTexture.PotHeight;
				
				vb.Add( Float3.Add( Float3.Local(), point.m_f3WorldPosition, f30 ) );
				vb.Add( 1.0f, 1.0f, 1.0f, 1.0f );
				vb.Add( 0.0f, 0.0f, u, v );

				vb.Add( Float3.Add( Float3.Local(), point.m_f3WorldPosition, f31 ) );
				vb.Add( 1.0f, 1.0f, 1.0f, 1.0f );
				vb.Add( 0.0f, 1.0f, u, v );
				
				vb.Add( Float3.Add( Float3.Local(), point.m_f3WorldPosition, f32 ) );
				vb.Add( 1.0f, 1.0f, 1.0f, 1.0f );				
				vb.Add( 1.0f, 0.0f, u, v );
				
				vb.Add( Float3.Add( Float3.Local(), point.m_f3WorldPosition, f33 ) );
				vb.Add( 1.0f, 1.0f, 1.0f, 1.0f );
				vb.Add( 1.0f, 1.0f, u, v );
				
				short i = (short) (nbPoints*4);
				
				
				ib.Add( (short)(i+0) );
				ib.Add( (short)(i+1) );
				ib.Add( (short)(i+2) );
				
				ib.Add( (short)(i+2) );
				ib.Add( (short)(i+1) );
				ib.Add( (short)(i+3) );
				
				nbPoints++;
			}	
		}
		vb.End();
		ib.End();
		if( nbPoints <= 0 )
		{
			return;
		}
		
		
		gfxc.SetFrameBuffer( FrameBuffer );
		gfxc.SetViewport(0,0,FrameBuffer.Width,FrameBuffer.Height);
		gfxc.SetClearColor( 0.0f, 0.0f, 0.0f, 0.5f );
		gfxc.Clear(EClearBuffer.Color);// Cl GLES20.GL_COLOR_BUFFER_BIT );
		
		//GLES20.glEnable(GLES20.GL_BLEND);
		//GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE);
		gfxc.SetBlendState( m_blendStateAdd );
		//GLES20.glDisable(GLES20.GL_DEPTH_TEST);
		//GLES20.glDepthMask( false );
		gfxc.SetDepthStencilState( m_depthStencilStateDisable );
		
		gfxc.SetRasterizerState( m_rasterizerStateDisableCullFace );
		
		//GLES20.glDisable(GLES20.GL_CULL_FACE );
		
		//GLES20.glColorMask( true, true, true, true );
		
		gfxc.SetProgram(m_program);
		gfxc.SetMatrix(u_matrixWorldViewProjection, mc.GetWorldViewProjection().Values);
		gfxc.SetTexture(u_textureColor,Texture);
		gfxc.SetSamplerState( u_textureColor, m_samplerLinear );
		gfxc.SetTexture(u_textureOcclusion,frameBuffer.ColorRenderTexture);
		gfxc.SetSamplerState( u_textureOcclusion, m_samplerLinear );
		
		gfxc.SetVertexBuffer( vb );
		gfxc.EnableVertexStream(m_stream, vbOffset);
		
		gfxc.SetIndexBuffer( ib );
		gfxc.DrawElements( Primitive.Points, nbPoints*6, IndexFormat.UnsignedShort, ibOffset );
		
		gfxc.DisableVertexStream(m_stream);
		
		
		//r.SetFrameBuffer( frameBuffer );
		//GLES20.glViewport(0,0,frameBuffer.Width,frameBuffer.Height);
		br.SetColor( 1.0f, 1.0f, 1.0f, 1.0f );
		PresentFrameBuffer( br, FrameBuffer, frameBuffer );
		
		//GLES20.glDisable(GLES20.GL_BLEND);
		//GLES20.glDepthMask( true );
		//GLES20.glEnable(GLES20.GL_DEPTH_TEST);
		
	}
	
	public DepthStencilState m_depthStencilStateTestDisableWriteDisable =
	new DepthStencilState( false, DepthFunc.Less, false );
	
	public void PresentFrameBuffer( BasicRender br, FrameBuffer frameBufferFrom, FrameBuffer frameBufferTo )
	{
		if(!Marker.Done)
		{
			return;
		}
		
		GfxCommandContext gfxc = br.GetCommandContext();
		MatrixCache mc = br.GetMatrixCache();
		
		if( gfxc == null || mc == null )
		{
			return;
		}

		gfxc.SetFrameBuffer( frameBufferTo );
		gfxc.SetViewport(0, 0, frameBufferTo.Width, frameBufferTo.Height);
		
		//GLES20.glDisable(GLES20.GL_DEPTH_TEST);
		//GLES20.glDepthMask( false );
		
		gfxc.SetDepthStencilState( m_depthStencilStateTestDisableWriteDisable );
		
		Float4x4 matrixOrtho = Float4x4.Local();
		Matrix.orthoM(matrixOrtho.Values, 0, 0, frameBufferTo.Width, 0, frameBufferTo.Height, -1.0f, 1.0f);

		Float4x4 matrixProjection = Float4x4.Local().Set( mc.GetProjection() );
		Float4x4 matrixView = Float4x4.Local().Set( mc.GetView() );
		Float4x4 matrixWorld = Float4x4.Local().Set( mc.GetWorld() );
		
		mc.SetProjection(matrixOrtho);
		mc.SetView(Float4x4.Identity());
		mc.SetWorld(Float4x4.Identity());

		br.SetTexture(frameBufferFrom.ColorRenderTexture);
		br.SetSamplerState(m_samplerLinear);

		br.Begin(Primitive.TriangleStrip, BasicRender.EShader.ColorTexture);

		float u = ((float)frameBufferFrom.ColorRenderTexture.GetNpotWidth()) / ((float)frameBufferFrom.ColorRenderTexture.GetPotWidth());
		float v = ((float)frameBufferFrom.ColorRenderTexture.GetNpotHeight()) / ((float)frameBufferFrom.ColorRenderTexture.GetPotHeight());

		br.SetTexcoord(0.0f,   0.0f);   br.SetVertex(0.0f, 0.0f, 0.0f);
		br.SetTexcoord(0.0f,   v*2.0f); br.SetVertex(0.0f, frameBufferTo.Height*2.0f, 0.0f);
		br.SetTexcoord(u*2.0f, 0.0f);   br.SetVertex(frameBufferTo.Width*2.0f, 0.0f, 0.0f);
		//br.SetTexcoord(u,    v); br.SetVertex(ScanOutWidth, ScanOutHeight, 0.0f);
		br.End();
		
		mc.SetProjection(matrixProjection);
		mc.SetView(matrixView);
		mc.SetWorld(matrixWorld);
	}
	
	
	public void Render(BasicRender br, GameViewPoint viewPoint, float fWidth, float fHeight )
	{
		if( !Marker.Done)
		{
			return;
		}
		
		GfxCommandContext gfxc = br.GetCommandContext();
		MatrixCache mc = br.GetMatrixCache();
		FrameLinearVertexBuffer vb = br.GetVertexBuffer();
		
		if( gfxc ==  null || mc == null || vb == null )
		{
			return;
		}
		
		m_occlusionQuery.RenderPoints( gfxc, mc, vb, fWidth, fHeight );
		Float3 f3X = viewPoint.Transform.GetAxisX( Float3.Local() );
		Float3 f3Y = viewPoint.Transform.GetAxisY( Float3.Local() );
		
		float fSize = 4000.0f;
		Float3 f30 = Float3.Mad( Float3.Local(), f3X, -fSize, Float3.Mul( Float3.Local(), f3Y, -fSize ) );
		Float3 f31 = Float3.Mad( Float3.Local(), f3X, -fSize, Float3.Mul( Float3.Local(), f3Y,  fSize ) );
		Float3 f32 = Float3.Mad( Float3.Local(), f3X,  fSize, Float3.Mul( Float3.Local(), f3Y, -fSize ) );
		Float3 f33 = Float3.Mad( Float3.Local(), f3X,  fSize, Float3.Mul( Float3.Local(), f3Y,  fSize ) );
		
		br.SetTexture( Texture );
		GLES20.glEnable(GLES20.GL_BLEND);
		GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE);
		GLES20.glDisable(GLES20.GL_DEPTH_TEST);
		
		for( OcclusionQuery.Point point : m_occlusionQuery.m_points )
		{
			if( 0 < point.OcclusionCount )
			{
				br.SetColor( 1.0f, 1.0f, 1.0f, 1.0f );
				br.Begin( Primitive.TriangleStrip, BasicRender.EShader.ColorTexture );
				br.SetTexcoord( 0.0f, 0.0f );
				br.SetVertex( Float3.Add( Float3.Local(), point.m_f3WorldPosition, f30 ) );
				br.SetTexcoord( 0.0f, 1.0f );
				br.SetVertex( Float3.Add( Float3.Local(), point.m_f3WorldPosition, f31 ) );
				br.SetTexcoord( 1.0f, 0.0f );
				br.SetVertex( Float3.Add( Float3.Local(), point.m_f3WorldPosition, f32 ) );
				br.SetTexcoord( 1.0f, 1.0f );
				br.SetVertex( Float3.Add( Float3.Local(), point.m_f3WorldPosition, f33 ) );
				br.End();
			}	
		}
		
		GLES20.glDisable(GLES20.GL_BLEND);
	}
}

