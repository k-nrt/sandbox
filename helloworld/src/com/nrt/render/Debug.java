package nrt.render;

import java.nio.ByteBuffer;
import java.nio.*;
import android.opengl.*;

import nrt.render.*;
import nrt.math.FMath;
import nrt.math.Float4;
import nrt.math.Float4x4;


public class Debug
{
	static class Sphere
	{
		private int m_nbVertices = 0;
		private int m_nbIndices = 0;
		private StaticVertexBuffer m_vertexBuffer = null;
		private StaticIndexBuffer m_indexBuffer = null;

		private VertexStream m_stream = null;

		private Program m_program = null;

		private Uniform u_worldViewProjecrion = null;
		private Uniform u_color = null;

		public Sphere( DelayResourceQueue drq, int nbVSplits, int nPolygon )
		{
			String[] strVs = 
			{
				"uniform mat4 u_worldViewProjection;",
				"attribute vec3 a_position;",
				"void main(){",
				"gl_Position = u_worldViewProjection*vec4(a_position,1.0);",
				"}",
			};

			String[] strFs =
			{
				"precision mediump float;",
				"uniform vec4 u_color;",
				"void main(){",
				"gl_FragColor=u_color;",
				"}",
			};

			AttributeBinding[] ab =
			{
				new AttributeBinding(0, "a_position"),
			};

			//try
			//{
			
			m_program = new Program( drq, ab, new VertexShader( drq, strVs ), new FragmentShader( drq, strFs ) );

			u_worldViewProjecrion = new Uniform(drq, m_program, "u_worldViewProjection" );
			u_color = new Uniform( drq, m_program, "u_color" );

			VertexAttribute[] at =
			{
				new VertexAttribute( 0, 3, GLES20.GL_FLOAT, false, 0 ),
			};

			m_stream = new VertexStream( at, 3*4 );

			m_nbVertices = 2 + nbVSplits*nPolygon;

			ByteBuffer bufferVertices = ByteBuffer.allocateDirect( m_nbVertices*12 );
			bufferVertices.order( ByteOrder.nativeOrder() );

			bufferVertices.putFloat( 0.0f );
			bufferVertices.putFloat( 1.0f );
			bufferVertices.putFloat( 0.0f );

			for( int v = 1 ; v < nbVSplits ; v++ )
			{
				float rad = FMath.PI*((float)v)/((float)(nbVSplits));
				float y = FMath.Cos( rad );
				float r = FMath.Sin( rad );
				for( int p = 0 ; p < nPolygon ; p++ )
				{
					rad = FMath.RD*((float)p)/((float)nPolygon);
					float x = r*FMath.Sin( rad );
					float z = r*FMath.Cos( rad );

					bufferVertices.putFloat( x );
					bufferVertices.putFloat( y );
					bufferVertices.putFloat( z );
				}
			}

			bufferVertices.putFloat( 0.0f );
			bufferVertices.putFloat( -1.0f );
			bufferVertices.putFloat( 0.0f );
			bufferVertices.position(0);
			m_vertexBuffer = new StaticVertexBuffer( drq, bufferVertices );

			short[] indices = new short[nbVSplits*nPolygon*2 + (nbVSplits-1)*nPolygon*2];
			int i = 0;
			for( int v = 0 ; v < nbVSplits ; v++ )
			{
				for( int p = 0 ; p < nPolygon ; p++ )
				{
					if( v <= 0 )
					{
						indices[i] = 0;
						i++;
						indices[i] = (short) (1 + v*nPolygon + p);
						i++;
					}
					else if( v < (nbVSplits-1) )
					{
						indices[i] = (short) (1 + (v-1)*nPolygon + p);
						i++;
						indices[i] = (short) (1 + v*nPolygon + p);
						i++;
					}
					else
					{
						indices[i] = (short) (1 + (v-1)*nPolygon + p);
						i++;
						indices[i] = (short) (1 + v*nPolygon);
						i++;
					}
				}

				if( 0 < v )//&& v < (nbVSplits- 1) )
				{
					for( int p = 0 ; p < nPolygon ; p++ )
					{
						int pp = (p+1)%nPolygon;
						indices[i] = (short) (1+ (v-1)*nPolygon + p );
						i++;
						indices[i] = (short) (1+ (v-1)*nPolygon + pp );
						i++;
					}
				}
			}

			m_nbIndices = i;

			m_indexBuffer = new StaticIndexBuffer( drq, indices );
			/*
			 for( int p = 0 ; p < nPolygon ; p++ )
			 {
			 indices[i] = 0;
			 i++;
			 indices[i] = (short)(p*(nbVSplits+1));
			 i++;

			 x					indices[i] = (short)(p*(nbVSplits+1) + v);
			 i++;
			 indices[i] = (short)(p*(nbVSplits+1) + v + 1);
			 i++;
			 }
			 }
			 */
			 
			 /*
			 }
			 catch ( ThreadForceDestroyException ex )
			 {
				 
			 }

			 */
		}

		public void Draw( Render r, Float4 f4Color, Float4x4 matrix )
		{
			//BasicRender br = SubSystem.BasicRender;
			//MatrixCache mc = br.GetMatrixCache();
			//Render r = br.GetRender();


			r.SetProgram( m_program );
			r.SetMatrix( u_worldViewProjecrion, matrix.Values );
			r.SetFloat4( u_color, f4Color );

			r.SetVertexBuffer( m_vertexBuffer );

			r.EnableVertexStream( m_stream, 0 );

			r.SetIndexBuffer( m_indexBuffer );

			GLES20.glDrawElements( GLES20.GL_LINES, m_nbIndices, GLES20.GL_UNSIGNED_SHORT, 0 );
			//GLES20.glDrawArrays( GLES20.GL_LINES, 0, m_nbVertices );
		}


	}


	Sphere m_sphere = null;
	Float4 m_color = new Float4( 1.0f );
	private Render m_render = null;
	private MatrixCache m_matrixCache = null;

	public Debug( Render render, DelayResourceQueue drq, MatrixCache matrixCache)
	{
		m_sphere = new Sphere( drq, 8, 16 );
		m_render = render;
		m_matrixCache = matrixCache;

	}

	public void  SetColor( float r, float g, float b, float a )
	{
		m_color.Set( r, g, b, a );
	}

	public void DrawSphere()
	{
		m_sphere.Draw( m_render, m_color, m_matrixCache.GetWorldViewProjection() );


	}
}
