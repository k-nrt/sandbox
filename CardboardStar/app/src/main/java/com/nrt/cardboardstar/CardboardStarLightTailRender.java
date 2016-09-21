package com.nrt.cardboardstar;
import android.opengl.*;
import java.util.*;
import com.nrt.clipper.*;
import com.nrt.framework.*;
import com.nrt.math.*;
import com.nrt.render.*;

public class CardboardStarLightTailRender
{
	public static class BufferBuilder
	{
		public List<Byte> m_listBuffer = new ArrayList<Byte>();
		public int Stride = -1;

		public void Put(Object...args)
		{
			int nStride = 0;
			for (Object arg : args)
			{
				if (arg instanceof Byte)
				{
					PutByte((Byte) arg);
					nStride += 1;
				}
				else if (arg instanceof Short)
				{
					PutShort((Short) arg);
					nStride += 2;
				}
				else if (arg instanceof Integer)
				{
					PutInteger((Integer) arg);
					nStride += 4;
				}
				else if (arg instanceof Float)
				{
					PutFloat((Float) arg);
					nStride += 4;
				}
			}

			if (Stride < 0)
			{
				Stride = nStride;
			}

		}

		private void PutByte(byte v)
		{
			m_listBuffer.add(v);
		}

		private void PutShort(short v)
		{
			PutByte((byte)(v & 0xff));
			PutByte((byte)((v >> 8) & 0xff));
		}

		private void PutInteger(int v)
		{
			PutByte((byte)(v & 0xff));
			PutByte((byte)((v >> 8) & 0xff));
			PutByte((byte)((v >> 16) & 0xff));
			PutByte((byte)((v >> 24) & 0xff));
		}

		public void PutFloat(float v)
		{
			PutInteger(Float.floatToIntBits(v));
		}

		public int Size()
		{
			return m_listBuffer.size();
		}

		public byte[] ToArray()// byte[] result )
		{
			byte[] result = new byte[m_listBuffer.size()];
			for (int i = 0 ; i < m_listBuffer.size() ; i++)
			{
				result[i] = m_listBuffer.get(i);
				//	System.Error.WriteLine( Byte.toString( result[i] ) );
			}
			/*
			 for( int i = 0; i < m_listBuffer.size() ; i++ )
			 {
			 System.Error.Write( String.format( "%02x ", 0xff & (int) result[i] ) );
			 if( (i%20) == 19)
			 {
			 System.Error.WriteLine( " " );
			 }

			 }
			 */

			return result;
			//m_listBuffer.toArray( result );
			//return result;
		}


	}

	public StaticVertexBuffer VertexBuffer = null;
	public StaticIndexBuffer IndexBuffer = null;
	public VertexStream VertexStream = null;
	public Program Program = null;

	public Uniform u_m4WorldViewProjection = null;
	public Uniform u_fRadius = null;

	public Uniform u_v3ViewPosition = null;

	public Uniform u_v4ColorIn = null;
	public Uniform u_v4ColorOut = null;

	public Uniform u_v4TransformsX = null;
	public Uniform u_v4TransformsY = null;
	public Uniform u_v4TransformsZ = null;

	public int SegmentCount = 0; //. 節の数.
	public int PolygonCount = 0; //. 何角形か.

	public int WeightCount = 0; //. 節の間の分割数.

	

	public CardboardStarLightTailRender()
	{
	}

	public CardboardStarLightTailRender(DelayResourceQueue drq,  int nbSegments, int nbPolygons)
	{
		SegmentCount = nbSegments;
		PolygonCount = nbPolygons;
		int nbWeights = WeightCount = 2;

		BufferBuilder builder = new BufferBuilder();
		for (int i = 0 ; i < nbSegments ; i++)
		{
			for (int j = 0 ; j < nbWeights ; j++)
			{
				for (int r = 0 ; r < nbPolygons ; r++)
				{
					float rad = FMath.PI * 2.0f * ((float)r / (float)nbPolygons);
					float c = FMath.Cos(rad) * 1.0f;
					float s = FMath.Sin(rad) * 1.0f;

					float fWeight = 1.0f - ((float)j / (float)nbWeights);

					builder.Put(
						c, s, 0.0f,
						c, s, 0.0f,
						(float) i, (float) ((i == nbSegments - 1) ? i : i + 1),
						fWeight, 1.0f - fWeight);

				}

				if (i == (nbSegments - 1))
				{
					break;
				}
			}
		}

		List<Short> listIndices = new ArrayList<Short>();

		for (int i = 0 ; i < nbSegments - 1 ; i++)
		{
			for (int j = 0 ; j < nbWeights ; j++)
			{
				int i0 = (i * nbWeights + j) * nbPolygons;
				int i1 = (i * nbWeights + j + 1) * nbPolygons;

				for (int r = 0 ; r < nbPolygons ; r++)
				{
					int i00 = i0 + r;
					int i01 = i0 + (r + 1) % nbPolygons;
					int i10 = i1 + r;
					int i11 = i1 + (r + 1) % nbPolygons;

					listIndices.add((short) i00);
					listIndices.add((short) i10);
					listIndices.add((short) i01);

					listIndices.add((short) i10);
					listIndices.add((short) i11);
					listIndices.add((short) i01);
				}
			}
		}

		VertexAttribute[] arrayVertexAttributes =
		{
			new VertexAttribute(0, 3, GLES20.GL_FLOAT, false, 0),// Format.Float3, //. Position.
			new VertexAttribute(1, 3, GLES20.GL_FLOAT, false, 12),//VertexFormat.Float3, //. Normal.
			new VertexAttribute(2, 2, GLES20.GL_FLOAT, false, 24),//VertexFormat.Float2, //. Indices.
			new VertexAttribute(3, 2, GLES20.GL_FLOAT, false, 32),//VertexFormat.Float2, //. Weights.
		};

		VertexStream = new VertexStream(arrayVertexAttributes, 10 * 4);

		VertexBuffer = new StaticVertexBuffer(drq,  builder.ToArray());// listVertices.Count, listIndices.Count, arrayVertexFormats);
		//VertexBuffer.SetVertices(listVertices.ToArray());
		//VertexBuffer.SetIndices(listIndices.ToArray());
		short[] indices = new short[listIndices.size()];
		for (int i = 0 ; i < indices.length ; i++)
		{
			indices[i] = listIndices.get(i);
		}

		//listIndices.toArray( indices );
		IndexBuffer = new StaticIndexBuffer(drq, indices);

		AttributeBinding[] arrayAttributeBindings = 
		{
			new AttributeBinding(0,  "a_f3Position"),
			new AttributeBinding(1, "a_f3Normal"),
			new AttributeBinding(2, "a_f2Indices"),
			new AttributeBinding(3, "a_f2Weights"),
		};

		VertexShader vs = new VertexShader(drq, SubSystem.Loader.LoadTextFile("light_tail_render.vs"));
		FragmentShader fs = new FragmentShader(drq, SubSystem.Loader.LoadTextFile("light_tail_render.fs"));

		Program = new Program(drq, arrayAttributeBindings, vs, fs);

		u_m4WorldViewProjection = new Uniform(drq, Program, "u_m4WorldViewProjection");
		u_v3ViewPosition = new Uniform(drq, Program, "u_v3ViewPosition");
		u_fRadius = new Uniform(drq, Program, "u_fRadius");
		u_v4ColorIn = new Uniform(drq, Program, "u_v4ColorIn");
		u_v4ColorOut = new Uniform(drq, Program, "u_v4ColorOut");

		u_v4TransformsX = new Uniform(drq, Program, "u_v4TransformsX");
		u_v4TransformsY = new Uniform(drq, Program, "u_v4TransformsY");
		u_v4TransformsZ = new Uniform(drq, Program, "u_v4TransformsZ");

	}

	


	private final float[] m_arrayTransformsX = new float[16 * 4];
	private final float[] m_arrayTransformsY = new float[16 * 4];
	private final float[] m_arrayTransformsZ = new float[16 * 4];

	

	private final Box m_boxScissor = new Box();

	public final void Render
	(
		final BasicRender br, 
		final CardboardStarLightTail lightTail, 
		final Frustum frustum, 
		final float fScale, 
		final Float4 v4ColorIn, final Float4 v4ColorOut)
	{
		if (lightTail.Length < 2)
		{
			return;
		}

		/*
		lightTail.m_boxMinMax.ScissorBackFace(m_boxScissor, frustum.Surfaces);
		if (m_boxScissor.PolygonCount <= 0)
		{
			return;
		}
		
		*/
		MatrixCache mc = br.GetMatrixCache();

		//Render r = br.GetRender();

		GfxCommandContext r = br.GetCommandContext();
		
		r.SetProgram(Program);

		r.SetMatrix(u_m4WorldViewProjection, mc.GetWorldViewProjection().Values);
		r.SetFloat(u_fRadius, 1.0f);
		r.SetFloat3(u_v3ViewPosition, mc.GetViewInverse().GetAxisW(Float3.Local()));
		r.SetFloat4(u_v4ColorIn, v4ColorIn);
		r.SetFloat4(u_v4ColorOut, v4ColorOut);

		for (int i = 0 ; i < lightTail.Length ; i += 16)
		{
			int nbTransforms = lightTail.Length - i;
			if (nbTransforms > 16)
			{
				nbTransforms = 16;
			}

			for (int j = 0 ; j < nbTransforms ; j++)
			{
				final Transform3 transform = lightTail.GetTransform(i + j);
				//matrix.AxisX *= arrayRadius[i + j] * fScale;
				//matrix.AxisY *= arrayRadius[i + j] * fScale;
				//matrix = Matrix4.Transpose(matrix);
				//m_arrayTransformsX[j] = matrix.ColumnX;
				//m_arrayTransformsY[j] = matrix.ColumnY;
				//m_arrayTransformsZ[j] = matrix.ColumnZ;
				float fRadius = lightTail.Radius[i + j] * fScale;
				m_arrayTransformsX[j * 4 + 0] = transform.XYZ.X.X * fRadius;//X().X*fRadius;
				m_arrayTransformsX[j * 4 + 1] = transform.XYZ.Y.X * fRadius;//Y().X*fRadius;
				m_arrayTransformsX[j * 4 + 2] = transform.XYZ.Z.X;//Z().X;
				m_arrayTransformsX[j * 4 + 3] = transform.W.X;//W().X;
				m_arrayTransformsY[j * 4 + 0] = transform.XYZ.X.Y * fRadius;//X().Y*fRadius;
				m_arrayTransformsY[j * 4 + 1] = transform.XYZ.Y.Y * fRadius;//Y().Y*fRadius;
				m_arrayTransformsY[j * 4 + 2] = transform.XYZ.Z.Y;//Z().Y;
				m_arrayTransformsY[j * 4 + 3] = transform.W.Y;//W().Y;
				m_arrayTransformsZ[j * 4 + 0] = transform.XYZ.X.Z * fRadius;//X().Z*fRadius;
				m_arrayTransformsZ[j * 4 + 1] = transform.XYZ.Y.Z * fRadius;//Y().Z*fRadius;
				m_arrayTransformsZ[j * 4 + 2] = transform.XYZ.Z.Z;//Z().Z;
				m_arrayTransformsZ[j * 4 + 3] = transform.W.Z;//W().Z;

			}

			r.SetFloat4Array(u_v4TransformsX, m_arrayTransformsX);
			r.SetFloat4Array(u_v4TransformsY, m_arrayTransformsY);
			r.SetFloat4Array(u_v4TransformsZ, m_arrayTransformsZ);

			//Shader.Program.SetUniformValue(1, m_arrayTransformsX, 0, 0, 16);
			//Shader.Program.SetUniformValue(2, m_arrayTransformsY, 0, 0, 16);
			//Shader.Program.SetUniformValue(3, m_arrayTransformsZ, 0, 0, 16);

			//gc.SetShaderProgram(Shader.Program);
			//gc.SetVertexBuffer(0, VertexBuffer);
			//gc.DrawArrays(DrawMode.Triangles, 0, 6 * WeightCount * PolygonCount * (nbTransforms - 1));

			r.SetVertexBuffer(VertexBuffer);
			r.EnableVertexStream(VertexStream, 0);
			r.SetIndexBuffer(IndexBuffer);

			if( r != null )
			{
				r.DrawElements(
					Primitive.Triangles,
					6 * WeightCount * PolygonCount * (nbTransforms - 1),
					IndexFormat.UnsignedShort, 0);
			}
			else
			{
			GLES20.glDrawElements(
				GLES20.GL_TRIANGLES,
				6 * WeightCount * PolygonCount * (nbTransforms - 1),
				GLES20.GL_UNSIGNED_SHORT, 0);
			}

		}
	}


}

