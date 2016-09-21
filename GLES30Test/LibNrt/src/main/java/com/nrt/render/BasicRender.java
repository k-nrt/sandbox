package com.nrt.render;

import android.opengl.GLES30;
import com.nrt.basic.*;
import com.nrt.math.*;
import com.nrt.framework.*;

public class BasicRender
{
	public enum EShader
	{
		Color,
		ColorTexture,
		ColorTextureAlphaTest,
		Max,
	}

	private Program[] m_programs = new Program[EShader.Max.ordinal()];
	private VertexStream[] m_streams = new VertexStream[EShader.Max.ordinal()];
	private Uniform[] u_worldViewProjections = new Uniform[EShader.Max.ordinal()];
	private Render m_render = null; 
	private MatrixCache m_matrixCache = null;

	private RingVertexBuffer m_vertexBuffer = null;
	private RingIndexBuffer m_indexBuffer = null;

	private Float4 m_f4Color = new Float4(0.0f);
	private Float4 m_f4Texcoord = new Float4(0.0f);

	private EShader m_eShader = EShader.Color;
	private int m_nbVertices = 0;
	private Primitive m_primitive = Primitive.Points;

	private Texture m_texture = null;
	private SamplerState m_samplerState = null;

	private Sampler[] u_sampler0 = new Sampler[EShader.Max.ordinal()];

	public BasicRender(Render r, MatrixCache mc, int nVertexBufferSize, int nIndexBufferSize)
	{
		m_render = r;
		m_matrixCache = mc;

		m_vertexBuffer = new RingVertexBuffer( Subsystem.ResourceQueue, nVertexBufferSize);
		m_indexBuffer = new RingIndexBuffer( Subsystem.ResourceQueue, nIndexBufferSize);

		VertexAttribute[] vaColors =
		{
			new VertexAttribute(0, 3, GLES30.GL_FLOAT, false, 0),
			new VertexAttribute(1, 4, GLES30.GL_FLOAT, false, 12),
			//new VertexAttribute( 2, 2, GLES20.GL_FLOAT, false, 28 ),
		};
		m_streams[EShader.Color.ordinal()] = new VertexStream(vaColors, 0);

		VertexAttribute[] vaColorTextures =
		{
			new VertexAttribute(0, 3, GLES30.GL_FLOAT, false, 0),
			new VertexAttribute(1, 4, GLES30.GL_FLOAT, false, 12),
			new VertexAttribute(2, 2, GLES30.GL_FLOAT, false, 28),	
		};	
		m_streams[EShader.ColorTexture.ordinal()] = new VertexStream(vaColorTextures, 0);	
		m_streams[EShader.ColorTextureAlphaTest.ordinal()] = new VertexStream(vaColorTextures, 0);

		String[] strVsColor = 
		{
			"uniform mat4 u_worldViewProjection;",
			"attribute vec3 a_position;",
			"attribute vec4 a_color;",
			//	"attribute vec2 a_texcoord;",
			"varying vec4 v_color;",
			//	"varying vec2 v_texcoord;",
			"void main(){",
			"gl_Position = u_worldViewProjection*vec4(a_position,1.0);",
			"v_color = a_color;",
			//	"v_texcoord = a_texcoord;",
			"}",
		};

		String[] strFsColor =
		{
			"precision mediump float;",
			"varying vec4 v_color;",
			//	"varying vec4 v_texcoord;",
			"void main(){",
			"gl_FragColor=v_color;",
			//	"gl_FragColor=vec4(1.0,1.0,1.0,1.0);",
			"}",
		};

		AttributeBinding[] abColors =
		{
			new AttributeBinding(0, "a_position"),
			new AttributeBinding(1, "a_color"),
			//	new AttributeBinding( 2, "a_texcoord" ),
		};

		m_programs[EShader.Color.ordinal()] = new Program
		(
			Subsystem.ResourceQueue,
			abColors,
			new VertexShader( Subsystem.ResourceQueue, strVsColor),
			new FragmentShader( Subsystem.ResourceQueue, strFsColor)
		);

		u_worldViewProjections[EShader.Color.ordinal()] = new Uniform
		(
			Subsystem.ResourceQueue, 
			m_programs[EShader.Color.ordinal()], 
			"u_worldViewProjection"
		);

		String[] strVsColorTexture = 
		{
			"precision highp float;",
			"uniform highp mat4 u_worldViewProjection;",
			"attribute vec3 a_position;",
			"attribute vec4 a_color;",
			"attribute highp vec2 a_texcoord;",
			"varying vec4 v_color;",
			"varying highp vec2 v_texcoord;",
			"void main(){",
			"gl_Position = u_worldViewProjection*vec4(a_position,1.0);",
			"v_color = a_color;",
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


		m_programs[EShader.ColorTexture.ordinal()] = new Program
		(
			Subsystem.ResourceQueue, 
			abColorTextures,
			new VertexShader(Subsystem.ResourceQueue,strVsColorTexture),
			new FragmentShader(Subsystem.ResourceQueue,strFsColorTexture)
		);

		u_worldViewProjections[EShader.ColorTexture.ordinal()] = new Uniform
		(
			Subsystem.ResourceQueue, 
			m_programs[EShader.ColorTexture.ordinal()], 
			"u_worldViewProjection"
		);

		u_sampler0[EShader.ColorTexture.ordinal()] = new Sampler
		(
			Subsystem.ResourceQueue, 
			m_programs[EShader.ColorTexture.ordinal()], 
			0,
			"u_texture0"
		);
	}

	public void SetColor(float r, float g, float b, float a)
	{
		m_f4Color.X = r;
		m_f4Color.Y = g;
		m_f4Color.Z = b;
		m_f4Color.W = a;
	}

	public void SetColor( Float4 f4Rgba )
	{
		m_f4Color.Set( f4Rgba );
	}

	public void SetColor( int rgba )
	{
		m_f4Color.X = ((float)(0xff & rgba))/255.0f;
		m_f4Color.Y = ((float)(0xff & (rgba >> 8)))/255.0f;
		m_f4Color.Z = ((float)(0xff & (rgba >> 16)))/255.0f;
		m_f4Color.W = ((float)(0xff & (rgba >> 24)))/255.0f;
	}

	public void SetTexcoord(float u, float v)
	{
		m_f4Texcoord.X = u;
		m_f4Texcoord.Y = v;
	}

	public void SetTexture(Texture texture)
	{
		m_texture = texture;
	}

	public void SetSamplerState( SamplerState samplerState )
	{
		m_samplerState = samplerState;
	}

	public void Begin(Primitive primitive, EShader shader)
	{
		m_primitive = primitive;
		m_eShader = shader;
		m_nbVertices = 0;

		if (u_sampler0[m_eShader.ordinal()] != null && m_texture != null)
		{
			m_render.SetTexture(u_sampler0[m_eShader.ordinal()], m_texture);
			m_render.SetSamplerState( u_sampler0[m_eShader.ordinal()], m_samplerState );
		}
//		m_render.DisableVertexStream();
		m_vertexBuffer.Align(16);
		m_vertexBuffer.Begin();
		m_indexBuffer.Begin();
	}

	public void SetVertex( Float3 v )
	{
		SetVertex( v.X, v.Y, v.Z );
	}

	public void SetVertex(float x, float y, float z)
	{
		m_vertexBuffer.Add(x, y, z);
		m_vertexBuffer.Add(m_f4Color.X, m_f4Color.Y, m_f4Color.Z, m_f4Color.W);
		if (m_eShader == EShader.ColorTexture || m_eShader ==  EShader.ColorTextureAlphaTest)
		{
			m_vertexBuffer.Add(m_f4Texcoord.X, m_f4Texcoord.Y);
		}
		m_indexBuffer.Add((short)m_nbVertices);

		m_nbVertices++;
	}

	public void End()
	{

		int first = m_vertexBuffer.End();//m_streams[m_eShader.ordinal()].Stride;
		int indexOffset = m_indexBuffer.End();
		Render r = m_render;
		r.SetVertexBuffer(m_vertexBuffer);
		r.EnableVertexStream(m_streams[m_eShader.ordinal()], first);

		int iShader = m_eShader.ordinal();
		r.SetProgram(m_programs[iShader]);
		r.SetMatrix(u_worldViewProjections[iShader], m_matrixCache.GetWorldViewProjection().Values);
//		r.DrawArrays(m_primitive, 0, m_nbVertices);	
//		r.DisableVertexStream(m_streams[m_eShader.ordinal()]);
		r.SetIndexBuffer(m_indexBuffer);
		r.DrawElements(m_primitive, m_nbVertices, IndexFormat.UnsignedShort, indexOffset );

	}

	public void FillRectangle(Rect rect)
	{
		Begin(Primitive.TriangleStrip, EShader.Color);
		SetVertex(rect.X, rect.Y, 0.0f);
		SetVertex(rect.X, rect.Y + rect.Height, 0.0f);
		SetVertex(rect.X + rect.Width, rect.Y, 0.0f);
		SetVertex(rect.X + rect.Width, rect.Y + rect.Height, 0.0f);
		End();
	}

	public void FillRectangle( float x, float y, float z, float w, float h )
	{
		Begin(Primitive.TriangleStrip, EShader.Color);
		SetVertex(x, y, z);
		SetVertex(x, y + h, z);
		SetVertex(x + w, y, z);
		SetVertex(x + w, y + h, z);
		End();
	}




	public void Rectangle(Rect rect)
	{
		Begin( Primitive.LineStrip, EShader.Color);
		SetVertex(rect.X, rect.Y, 0.0f);
		SetVertex(rect.X + rect.Width, rect.Y, 0.0f);
		SetVertex(rect.X + rect.Width, rect.Y + rect.Height, 0.0f);

		SetVertex(rect.X, rect.Y + rect.Height, 0.0f);
		SetVertex(rect.X, rect.Y, 0.0f);

		End();
	}

	public void Rectangle( float x, float y, float w, float h )
	{
		Begin(Primitive.LineStrip, EShader.Color);
		SetVertex(x, y, 0.0f);
		SetVertex(x + w, y, 0.0f);
		SetVertex(x + w, y + h, 0.0f);

		SetVertex(x, y + h, 0.0f);
		SetVertex(x, y, 0.0f);

		End();

	}

	public void Arc(float x, float y, float radius, int nbPolygons)
	{
		Begin(Primitive.LineStrip, EShader.Color);

		for (int i = 0 ; i <= nbPolygons ; i++)
		{
			double rad = ((double)i / (double)nbPolygons) * Math.PI * 2.0;
			float s = (float) Math.sin(rad);
			float c = (float) Math.cos(rad);

			SetVertex(x + c * radius, y + s * radius, 0.0f);
		}
		End();
	}

	public static final int RgbaToAbgr( int rgba )
	{
		return ((rgba >> 24) & 0xff) |((rgba >> 8) & 0xff00) | ((rgba << 8) & 0xff0000) | ((rgba << 24) & 0xff000000);
	}

	public void Arc(float x, float y, float radiusIn, int rgbaIn, float radiusOut, int rgbaOut, int nbPolygons)
	{
		Begin(Primitive.TriangleStrip, EShader.Color);

		int abgrIn = RgbaToAbgr( rgbaIn );
		int abgrOut = RgbaToAbgr( rgbaOut );
		for (int i = 0 ; i <= nbPolygons ; i++)
		{
			double rad = ((double)i / (double)nbPolygons) * Math.PI * 2.0;
			float s = (float) Math.sin(rad);
			float c = (float) Math.cos(rad);

			SetColor( abgrOut );
			SetVertex(x + c * radiusOut, y + s * radiusOut, 0.0f);

			SetColor( abgrIn );
			SetVertex(x + c * radiusIn, y + s * radiusIn, 0.0f);

		}
		End();
	}

	public void Axis( float x, float y, float z, float size )
	{
		Begin( Primitive.Lines, EShader.Color );

		SetColor( 1.0f, 0.0f, 0.0f, 1.0f );
		SetVertex( x, y, z );
		SetVertex( x + size, y, z );

		SetColor( 0.0f, 1.0f, 0.0f, 1.0f );
		SetVertex( x, y, z );
		SetVertex( x, y + size, z );

		SetColor( 0.0f, 0.0f, 1.0f, 1.0f );
		SetVertex( x, y, z );
		SetVertex( x, y, z + size );

		End();
	}

	public Render GetRender()
	{
		return m_render;
	}

	public MatrixCache GetMatrixCache()
	{
		return m_matrixCache;
	}

	public RingVertexBuffer GetVertexBuffer()
	{
		return m_vertexBuffer;
	}

	public RingIndexBuffer GetIndexBuffer()
	{
		return m_indexBuffer;
	}
}

