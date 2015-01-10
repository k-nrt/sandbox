package nrt.render;

import android.opengl.*;
import nrt.math.Float3;
import nrt.math.Float4;
import nrt.math.Vec3;
import nrt.math.Vec4;
import nrt.math.Float4x4;

public class Render
{
	public int ScanOutWidth = 0;
	public int ScanOutHeight = 0;
	
	//public DelayResourceQueue DelayResourceQueue = new DelayResourceQueue();
	
	public void SetFrameBuffer( FrameBuffer frameBuffer )
	{
		if( frameBuffer != null )
		{
			GLES20.glBindFramebuffer( GLES20.GL_FRAMEBUFFER, frameBuffer.Name );
		}
		else
		{
			GLES20.glBindFramebuffer( GLES20.GL_FRAMEBUFFER, 0 );			
		}
	}

	public void SetVertexBuffer(VertexBuffer vb)
	{
		if( vb != null )
		{
			GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vb.GetVertexBufferName());
		}
		else
		{
			GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
		}
	}

	public void SetIndexBuffer(IndexBuffer ib)
	{
		if( ib != null )
		{
			GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, ib.GetIndexBufferName());
		}
		else
		{
			GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
		}
	}
	
	

	public void EnableVertexStream(VertexStream stream, int offset)
	{
		for (int i = 0 ; i < stream.VertexAttributes.length ; i++)
		{
			VertexAttribute va = stream.VertexAttributes[i];
			GLES20.glEnableVertexAttribArray(va.Index);
			GLES20.glVertexAttribPointer( 
				va.Index, 
				va.Components, 
				va.Format, 
				va.Normalize, 
				stream.Stride, 
				va.Offset + offset);
		}
	}
	
	public void DisableVertexStream(VertexStream stream)
	{
		for (int i = 0 ; i < stream.VertexAttributes.length ; i++)
		{
			VertexAttribute va = stream.VertexAttributes[i];
			GLES20.glDisableVertexAttribArray(va.Index);
		}
	}

	public void DisableVertexStream()
	{
		for (int i = 0 ; i < GLES20.GL_MAX_VERTEX_ATTRIBS ; i++)
		{
			GLES20.glDisableVertexAttribArray(i);
		}	
	}

	public void SetProgram(Program program)
	{
		GLES20.glUseProgram(program.Name);
	}

	public void SetTexture(int texUnit, Texture texture)
	{
		GLES20.glActiveTexture(texUnit);
		if( texture != null )
		{
			GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture.GetTextureName());
		}
		else
		{
			GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
		}
	}

	public void SetTexture(Sampler s, Texture texture)
	{
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + s.TextureUnit);
		if( texture != null )
		{
			GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture.GetTextureName());
		}
		else
		{
			GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
		}
		GLES20.glUniform1i(s.Index, s.TextureUnit);
	}

	public void SetSamplerState( Sampler s, SamplerState samplerState )
	{
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + s.TextureUnit);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, samplerState.MagFilter.Value);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, samplerState.MinFilter.Value);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, samplerState.WrapS.Value);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, samplerState.WrapT.Value);
		
	}
	
	public void SetMatrix(Uniform u, float[] matrix)
	{
		GLES20.glUniformMatrix4fv(u.Index, 1, false, matrix, 0);
	}

	public void SetMatrices(Uniform u, int nbMatrices, float[] matrices, int start)
	{
		GLES20.glUniformMatrix4fv(u.Index, nbMatrices, false, matrices, start * 16);
	}
	
	public void SetFloat4Array( Uniform u, float[] f4Array )
	{
		GLES20.glUniform4fv( u.Index, f4Array.length/4, f4Array, 0 );
	}

	public void SetFloat4Array( Uniform u, int nCount, float[] f4Array, int offset )
	{
		GLES20.glUniform4fv( u.Index, nCount, f4Array, offset );
	}

	public void SetFloat4(Uniform u, float x, float y, float z, float w)
	{
		GLES20.glUniform4f(u.Index, x, y, z, w);
	}

	public void SetFloat4(Uniform u, Float4 v )
	{
		GLES20.glUniform4f(u.Index, v.X, v.Y, v.Z, v.W );
	}

	public void SetFloat4(Uniform u, Vec4 v )
	{
		GLES20.glUniform4f(u.Index, v.X, v.Y, v.Z, v.W );
	}
	

	public void SetFloat3(Uniform u, float x, float y, float z)
	{
		GLES20.glUniform3f(u.Index, x, y, z);
	}

	public void SetFloat3(Uniform u, float[] v, int o )
	{
		GLES20.glUniform3f(u.Index, v[o+0], v[o+1], v[o+2]);
	}
	
	
	public void SetFloat3( Uniform u, Float3 v )
	{
		GLES20.glUniform3f( u.Index, v.X, v.Y, v.Z );
	}
	
	public void SetFloat3( Uniform u, Vec3 v )
	{
		GLES20.glUniform3f( u.Index, v.X, v.Y, v.Z );
	}
	
	public void SetFloat3Array( Uniform u, float[] f3Array )
	{
		GLES20.glUniform3fv( u.Index, f3Array.length/3, f3Array, 0 );
	}

	public void SetFloat3Array( Uniform u, int nCount, float[] f3Array, int offset )
	{
		GLES20.glUniform3fv( u.Index, nCount, f3Array, offset );
	}
	
	public void SetFloat2( Uniform u, float x, float y )
	{
		GLES20.glUniform2f( u.Index, x, y );
	}


	public void SetFloat( Uniform u, float x )
	{
		GLES20.glUniform1f( u.Index, x );
	}
	
	public void SetFloatArray( Uniform u, int nCount, float[] values, int offset )
	{
		GLES20.glUniform1fv( u.Index, nCount, values, offset );
	}

	public void DrawElements(int primitive, int nbVertices, IndexFormat eFormat, int nByteOffset)
	{
		GLES20.glDrawElements( 
			primitive, 
			nbVertices, 
			eFormat.Value, 
			nByteOffset
		);
	}
	
	public void DrawArrays(int primitive, int iFirst, int nbVertices)
	{
		GLES20.glDrawArrays(primitive, iFirst, nbVertices);
	}
}
