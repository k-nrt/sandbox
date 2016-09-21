package com.nrt.render;

import com.nrt.math.Float4;
import com.nrt.math.Float3;
import android.graphics.*;

public class GfxCommandContext
{
	public GfxCommandBuffer CommandBuffer = null;

	public int CommandPosition = 0;
	public int IntegerPosition = 0;
	public int FloatPosition = 0;
	public int ObjectPosition = 0;
	
	private boolean IsBufferOverflow( int nbNextIntegers, int nbNextFloats, int nbNextObjects )
	{
		if( CommandBuffer == null )
		{
			return true;
		}
		if( CommandBuffer.Commands.length <= CommandPosition )
		{
			return true;
		}
		
		if( CommandBuffer.Integers.length <= (IntegerPosition + nbNextIntegers) )
		{
			return true;
		}
		
		if( CommandBuffer.Floats.length <= (FloatPosition + nbNextFloats ) )
		{
			return true;
		}
		
		if( CommandBuffer.Objects.length <= (ObjectPosition + nbNextObjects ) )
		{
			return true;
		}
		
		return false;
	}

	private void AppendCommand( GfxCommand.Processor processor )
	{
		CommandBuffer.Commands[CommandPosition].Store( processor, IntegerPosition,FloatPosition,ObjectPosition);
		CommandPosition++;
	}

	private void AppendInteger(int value)
	{
		CommandBuffer.Integers[IntegerPosition] = value;
		IntegerPosition++;
	}

	private void AppendFloat(float value)
	{
		CommandBuffer.Floats[FloatPosition] = value;
		FloatPosition++;
	}

	private void AppendFloats(float[] values, int offset, int size)
	{
		for(int i = 0 ; i < size ; i++ )
		{
			CommandBuffer.Floats[FloatPosition] = values[i+offset];
			FloatPosition++;
		}
	}

	private void AppendFloat4(float x, float y, float z, float w)
	{
		CommandBuffer.Floats[FloatPosition] = x;
		FloatPosition++;
		CommandBuffer.Floats[FloatPosition] = y;
		FloatPosition++;
		CommandBuffer.Floats[FloatPosition] = z;
		FloatPosition++;
		CommandBuffer.Floats[FloatPosition] = w;
		FloatPosition++;
	}

	private void AppendFloat3(float x, float y, float z)
	{
		CommandBuffer.Floats[FloatPosition] = x;
		FloatPosition++;
		CommandBuffer.Floats[FloatPosition] = y;
		FloatPosition++;
		CommandBuffer.Floats[FloatPosition] = z;
		FloatPosition++;
	}

	private void AppendFloat2(float x, float y)
	{
		CommandBuffer.Floats[FloatPosition] = x;
		FloatPosition++;
		CommandBuffer.Floats[FloatPosition] = y;
		FloatPosition++;
	}

	private void AppendObject(Object object)
	{
		CommandBuffer.Objects[ObjectPosition] = object;
		ObjectPosition++;
	}

	public void BeginFrame( GfxCommandBuffer cb)
	{
		CommandBuffer = cb;
		CommandPosition = 0;
		IntegerPosition = 0;
		FloatPosition = 0;
		ObjectPosition = 0;
	}
	
	public void SetFrameBuffer( FrameBuffer frameBuffer )
	{
		if( IsBufferOverflow(0,0,1))
		{
			return;
		}
		
		AppendCommand( SetFrameBuffer );
		AppendObject( frameBuffer );
	}
	
	private static final GfxCommand.Processor SetFrameBuffer = new GfxCommand.Processor()
	{
		public final void OnCommand( Render r, GfxCommand c, GfxCommandBuffer cb )
		{
			FrameBuffer frameBuffer = (FrameBuffer) cb.Objects[c.Objects+0];
			r.SetFrameBuffer(frameBuffer);
		}
	};
	
	
	public void SetScissor(int x,int y, int width, int height)
	{
		if( IsBufferOverflow(4,0,0) )
		{
			return;
		}
		
		AppendCommand(SetScissor);
		AppendInteger(x);
		AppendInteger(y);
		AppendInteger(width);
		AppendInteger(height);
	}
	
	private static final GfxCommand.Processor SetScissor = new GfxCommand.Processor()
	{
		public final void OnCommand( Render r, GfxCommand c, GfxCommandBuffer cb )
		{
			int x = cb.Integers[c.Integers+0];
			int y = cb.Integers[c.Integers+1];
			int w = cb.Integers[c.Integers+2];
			int h = cb.Integers[c.Integers+3];
			r.SetScissor(x,y,w,h);
		}
	};
	
	public void SetViewport(int x,int y, int width, int height)
	{
		if( IsBufferOverflow(4,0,0) )
		{
			return;
		}
		AppendCommand(SetViewport);
		AppendInteger(x);
		AppendInteger(y);
		AppendInteger(width);
		AppendInteger(height);
	}
	
	private static final GfxCommand.Processor SetViewport = new GfxCommand.Processor()
	{
		public final void OnCommand( Render r, GfxCommand c, GfxCommandBuffer cb )
		{
			int x = cb.Integers[c.Integers+0];
			int y = cb.Integers[c.Integers+1];
			int w = cb.Integers[c.Integers+2];
			int h = cb.Integers[c.Integers+3];
			r.SetViewport(x,y,w,h);
		}
	};
	
	public final void SetClearColor( float red, float green, float blue, float alpha )
	{
		if( IsBufferOverflow(0,4,0))
		{
			return;
		}
		AppendCommand( SetClearColor );
		AppendFloat4( red, green, blue, alpha );
	}
	
	private static final GfxCommand.Processor SetClearColor = new GfxCommand.Processor()
	{
		public final void OnCommand( Render r, GfxCommand c, GfxCommandBuffer cb )
		{
			float red = cb.Floats[c.Floats+0];
			float green = cb.Floats[c.Floats+1];
			float blue = cb.Floats[c.Floats+2];
			float alpha = cb.Floats[c.Floats+3];
			r.SetClearColor(red,green,blue,alpha);
		}
	};
	
	public final void Clear( EClearBuffer clearBuffer )
	{
		if( IsBufferOverflow(0,0,1))
		{
			return;
		}
		AppendCommand(Clear);
		AppendObject( clearBuffer );
	}
	
	private static final GfxCommand.Processor Clear = new GfxCommand.Processor()
	{
		public final void OnCommand( Render r, GfxCommand c, GfxCommandBuffer cb )
		{
			EClearBuffer clearBuffer = (EClearBuffer) cb.Objects[c.Objects];
			r.Clear( clearBuffer );
		}
	};
	
	public void SetVertexBuffer(final VertexBuffer vb)
	{
		if(IsBufferOverflow(1,0,0))
		{
			return;
		}
		AppendCommand( SetVertexBuffer );
		int name = 0;
		if(vb != null )
		{
			name = vb.GetVertexBufferName();
		}
		AppendInteger( name );
	}
	
	private static final GfxCommand.Processor SetVertexBuffer = new GfxCommand.Processor()
	{
		public final void OnCommand( Render r, GfxCommand c, GfxCommandBuffer cb )
		{
			int name = cb.Integers[c.Integers];
			r.SetVertexBuffer(name);
		}
	};
	
	public void SetIndexBuffer(IndexBuffer ib)
	{
		if(IsBufferOverflow(1,0,0))
		{
			return;
		}
		AppendCommand(SetIndexBuffer);
		int name = 0;
		if(ib != null )
		{
			name = ib.GetIndexBufferName();
		}
		AppendInteger(name);
	}

	private static final GfxCommand.Processor SetIndexBuffer = new GfxCommand.Processor()
	{
		public final void OnCommand( Render r, GfxCommand c, GfxCommandBuffer cb )
		{
			int name = cb.Integers[c.Integers];
			r.SetIndexBuffer(name);
		}
	};
	
	public void EnableVertexStream(VertexStream stream, int offset)
	{
		if(IsBufferOverflow(1,0,1))
		{
			return;
		}
		AppendCommand(EnableVertexStream);
		AppendObject(stream);
		AppendInteger(offset);
	}

	private static final GfxCommand.Processor EnableVertexStream = new GfxCommand.Processor()
	{
		public final void OnCommand( Render r, GfxCommand c, GfxCommandBuffer cb )
		{
			VertexStream vs = (VertexStream) cb.Objects[c.Objects];
			int offset = cb.Integers[c.Integers];
			r.EnableVertexStream(vs,offset);
		}
	};

	public void DisableVertexStream(VertexStream stream)
	{
		if(IsBufferOverflow(0,0,1))
		{
			return;
		}
		AppendCommand(DisableVertexStream);
		AppendObject(stream);
	}

	private static final GfxCommand.Processor DisableVertexStream= new GfxCommand.Processor()
	{
		public final void OnCommand( Render r, GfxCommand c, GfxCommandBuffer cb  )
		{
			VertexStream vs = (VertexStream) cb.Objects[c.Objects];
			r.DisableVertexStream(vs);
		}
	};
	
	public void DisableVertexStreams()
	{
		if(IsBufferOverflow(0,0,0))
		{
			return;
		}
		AppendCommand(DisableVertexStreams);
	}

	private static final GfxCommand.Processor DisableVertexStreams = new GfxCommand.Processor()
	{
		public final void OnCommand( Render r, GfxCommand c, GfxCommandBuffer cb )
		{
			r.DisableVertexStream();
		}
	};
	
	public void SetProgram(Program program)
	{
		if(IsBufferOverflow(0,0,1))
		{
			return;
		}
		AppendCommand(SetProgram);
		AppendObject(program);
	}

	private static final GfxCommand.Processor SetProgram = new GfxCommand.Processor()
	{
		public final void OnCommand(Render r, GfxCommand c, GfxCommandBuffer cb )
		{
			Program program = (Program) cb.Objects[c.Objects];
			r.SetProgram( program );
		}
	};

	public void SetTexture(Sampler sampler, Texture texture)
	{
		if(IsBufferOverflow(0,0,2))
		{
			return;
		}
		AppendCommand(SetTexture);
		AppendObject(sampler);
		AppendObject(texture);
	}

	private static final GfxCommand.Processor SetTexture = new GfxCommand.Processor()
	{
		public final void OnCommand(Render r, GfxCommand c, GfxCommandBuffer cb  )
		{
			Sampler sampler = (Sampler) cb.Objects[c.Objects+0];
			Texture texture = (Texture) cb.Objects[c.Objects+1];
			r.SetTexture(sampler,texture);
		}
	};

	public void SetSamplerState( Sampler sampler, SamplerState samplerState )
	{
		if(IsBufferOverflow(0,0,2))
		{
			return;
		}
		AppendCommand(SetSamplerState);
		AppendObject(sampler);
		AppendObject(samplerState);
	}

	private static final GfxCommand.Processor SetSamplerState = new GfxCommand.Processor()
	{
		public final void OnCommand( Render r, GfxCommand c, GfxCommandBuffer cb  )
		{
			Sampler sampler = (Sampler) cb.Objects[c.Objects+0];
			SamplerState samplerState = (SamplerState) cb.Objects[c.Objects+1];
			r.SetSamplerState(sampler,samplerState);
		}
	};
	
	public void SetMatrix(Uniform uniform, float[] matrix)
	{
		if(IsBufferOverflow(1,16,1))
		{
			return;
		}
		AppendCommand(SetMatrixArray);
		AppendObject(uniform);
		AppendInteger(1);
		AppendFloats(matrix,0,16);
	}

	public void SetMatrixArray(Uniform uniform, int nbMatrices, float[] matrices, int offset)
	{
		if(IsBufferOverflow(1,nbMatrices*16,1))
		{
			return;
		}
		AppendCommand(SetMatrixArray);
		AppendObject(uniform);
		AppendInteger(nbMatrices);
		AppendFloats(matrices, offset, nbMatrices*16);
	}

	private static final GfxCommand.Processor SetMatrixArray = new GfxCommand.Processor()
	{
		public final void OnCommand( Render r, GfxCommand c, GfxCommandBuffer cb )
		{
			Uniform uniform = (Uniform) cb.Objects[c.Objects];
			int nbMatrices = cb.Integers[c.Integers];
			r.SetMatrixArray( uniform, nbMatrices, cb.Floats, c.Floats );
		}
	};
	
	public void SetFloat4Array( Uniform uniform, float[] f4Array )
	{
		if(IsBufferOverflow(1,f4Array.length,1))
		{
			return;
		}
		AppendCommand(SetFloat4Array);
		AppendObject(uniform);
		AppendInteger(f4Array.length/4);
		AppendFloats(f4Array,0,f4Array.length);
	}

	public void SetFloat4Array( Uniform uniform, int nCount, float[] f4Array, int offset )
	{
		if(IsBufferOverflow(1,4*nCount,1))
		{
			return;
		}
		AppendCommand(SetFloat4Array);
		AppendObject(uniform);
		AppendInteger(nCount);
		AppendFloats(f4Array,offset,4*nCount);	
	}

	private static final GfxCommand.Processor SetFloat4Array = new GfxCommand.Processor()
	{
		public final void OnCommand( Render r, GfxCommand c, GfxCommandBuffer cb  )
		{
			Uniform uniform = (Uniform) cb.Objects[c.Objects];
			int nbFloat4s = cb.Integers[c.Integers];
			r.SetFloat4Array(uniform, nbFloat4s, cb.Floats, c.Floats );
		}
	};
	
	public void SetFloat4(Uniform uniform, float x, float y, float z, float w)
	{
		if(IsBufferOverflow(1,4,1))
		{
			return;
		}
		AppendCommand(SetFloat4Array);
		AppendObject(uniform);
		AppendInteger(1);
		AppendFloat4(x,y,z,w);
	}

	public void SetFloat4(Uniform uniform, Float4 v )
	{
		if(IsBufferOverflow(1,4,1))
		{
			return;
		}
		AppendCommand(SetFloat4Array);
		AppendObject(uniform);
		AppendInteger(1);
		AppendFloat4(v.X,v.Y,v.Z,v.W);
	}

	public void SetFloat3(Uniform uniform, float x, float y, float z)
	{
		if(IsBufferOverflow(1,3,1))
		{
			return;
		}
		AppendCommand(SetFloat3Array);
		AppendObject(uniform);
		AppendInteger(1);
		AppendFloat3(x,y,z);
	}

	public void SetFloat3(Uniform uniform, float[] v, int offset )
	{
		if(IsBufferOverflow(1,3,1))
		{
			return;
		}
		AppendCommand(SetFloat3Array);
		AppendObject(uniform);
		AppendInteger(1);
		AppendFloat3(v[offset+0],v[offset+1],v[offset+2]);
	}

	public void SetFloat3( Uniform uniform, Float3 v )
	{
		if(IsBufferOverflow(1,3,1))
		{
			return;
		}
		AppendCommand(SetFloat3Array);
		AppendObject(uniform);
		AppendInteger(1);
		AppendFloat3(v.X,v.Y,v.Z);
	}

	public void SetFloat3Array( Uniform uniform, float[] f3Array )
	{
		if(IsBufferOverflow(1,f3Array.length,1))
		{
			return;
		}
		AppendCommand(SetFloat3Array);
		AppendObject(uniform);
		AppendInteger(f3Array.length/3);
		AppendFloats( f3Array, 0, f3Array.length );
	}

	public void SetFloat3Array( Uniform uniform, int nCount, float[] f3Array, int offset )
	{
		if(IsBufferOverflow(1,nCount*3,1))
		{
			return;
		}
		AppendCommand(SetFloat3Array);
		AppendObject(uniform);
		AppendInteger(nCount);
		AppendFloats( f3Array, offset, nCount*3 );	
	}

	private static final GfxCommand.Processor SetFloat3Array = new GfxCommand.Processor()
	{
		public final void OnCommand( Render r, GfxCommand c, GfxCommandBuffer cb  )
		{
			Uniform uniform = (Uniform) cb.Objects[c.Objects];
			int nbFloat3s = cb.Integers[c.Integers];
			r.SetFloat3Array(uniform, nbFloat3s, cb.Floats, c.Floats );
		}
	};
	
	public void SetFloat2( Uniform uniform, float x, float y )
	{
		if(IsBufferOverflow(1,2,1))
		{
			return;
		}
		
		AppendCommand(SetFloat2Array);
		AppendObject(uniform);
		AppendInteger(1);
		AppendFloat2(x,y);
	}

	private static final GfxCommand.Processor SetFloat2Array = new GfxCommand.Processor()
	{
		public final void OnCommand( Render r, GfxCommand c, GfxCommandBuffer cb  )
		{
			Uniform uniform = (Uniform) cb.Objects[c.Objects];
			int nbFloat2s = cb.Integers[c.Integers];
			r.SetFloat2Array(uniform, nbFloat2s, cb.Floats, c.Floats );
		}
	};
	
	public void SetFloat( Uniform uniform, float x )
	{
		if(IsBufferOverflow(1,1,1))
		{
			return;
		}
		AppendCommand(SetFloatArray);
		AppendObject(uniform);
		AppendInteger(1);
		AppendFloat(x);
	}

	public void SetFloatArray( Uniform uniform, int nCount, float[] values, int offset )
	{
		if(IsBufferOverflow(1,nCount,1))
		{
			return;
		}
		AppendCommand(SetFloatArray);
		AppendObject(uniform);
		AppendInteger(nCount);
		AppendFloats( values, offset, nCount);
	}

	private static final GfxCommand.Processor SetFloatArray = new GfxCommand.Processor()
	{
		public final void OnCommand( Render r, GfxCommand c, GfxCommandBuffer cb  )
		{
			Uniform uniform = (Uniform) cb.Objects[c.Objects];
			int nbFloats = cb.Integers[c.Integers];
			r.SetFloatArray(uniform, nbFloats, cb.Floats, c.Floats );
		}
	};
	
	public void DrawElements(Primitive primitive, int nbIndices, IndexFormat eFormat, int nByteOffset)
	{
		if(IsBufferOverflow(2,0,2))
		{
			return;
		}
		
		AppendCommand(DrawElements);
		AppendObject(primitive);
		AppendInteger(nbIndices);
		AppendObject(eFormat);
		AppendInteger(nByteOffset);
	}

	private static final GfxCommand.Processor DrawElements = new GfxCommand.Processor()
	{
		public final void OnCommand( Render r, GfxCommand c, GfxCommandBuffer cb  )
		{
			Primitive primitive = (Primitive) cb.Objects[c.Objects+0];
			int nbIndices = cb.Integers[c.Integers+0];			
			IndexFormat format = (IndexFormat) cb.Objects[c.Objects+1];
			int nByteOffset = cb.Integers[c.Integers+1];
			r.DrawElements(primitive, nbIndices, format, nByteOffset);
		}
	};
	
	public void SetBlendState( final BlendState blendState )
	{
		if(IsBufferOverflow(0,0,1))
		{
			return;
		}
		
		AppendCommand( SetBlendState );
		AppendObject( blendState );
	}
	
	private static GfxCommand.Processor SetBlendState = new GfxCommand.Processor()
	{
		public void OnCommand( Render r, GfxCommand c, GfxCommandBuffer cb )
		{
			BlendState blendState = (BlendState) cb.Objects[c.Objects];
			r.SetBlendState( blendState );
		}
	};
	
	public void SetDepthStencilState( final DepthStencilState depthStencilState )
	{
		if(IsBufferOverflow(0,0,1))
		{
			return;
		}
		AppendCommand( SetDepthStencilState );
		AppendObject( depthStencilState );
	}
	
	private static final GfxCommand.Processor SetDepthStencilState = new GfxCommand.Processor()
	{
		public final void OnCommand( Render r, GfxCommand c, GfxCommandBuffer cb )
		{
			final DepthStencilState depthStencilState = (DepthStencilState) cb.Objects[c.Objects];
			r.SetDepthStencilState( depthStencilState );
		}
	};
	
	public void SetRasterizerState( final RasterizerState rasterizerState )
	{
		if(IsBufferOverflow(0,0,1))
		{
			return;
		}
		AppendCommand(SetRasterizerState);
		AppendObject(rasterizerState);
	}
	
	private static final GfxCommand.Processor SetRasterizerState = new GfxCommand.Processor()
	{
		public final void OnCommand( Render r, GfxCommand c, GfxCommandBuffer cb )
		{
			final RasterizerState rasterizerState = (RasterizerState) cb.Objects[c.Objects];
			r.SetRasterizerState( rasterizerState );
		}
	};
	
	public void DrawArrays(Primitive primitive, int iFirst, int nbVertices)
	{
		if(IsBufferOverflow(2,0,1))
		{
			return;
		}
		AppendCommand(DrawArrays);
		AppendObject(primitive);
		AppendInteger(iFirst);
		AppendInteger(nbVertices);
	}

	private static final GfxCommand.Processor DrawArrays = new GfxCommand.Processor()
	{
		public final void OnCommand( Render r, GfxCommand c, GfxCommandBuffer cb  )
		{
			Primitive primitive  = (Primitive) cb.Objects[c.Objects+0];
			int iFirst           = cb.Integers[c.Integers+0];			
			int nbVertices       = cb.Integers[c.Integers+1];
			r.DrawArrays(primitive.Value, iFirst, nbVertices );
		}
	};
	
	public void End()
	{
		if(IsBufferOverflow(0,0,0))
		{
			return;
		}
		
		AppendCommand(null);
	}

	public void ProcessCommands( Render r, int start, int end )
	{
		if(CommandBuffer == null )
		{
			return;
		}
		
		if( end < 0 || CommandPosition < end)
		{
			end = CommandPosition;
		}
		
		for( int i = start ; i < end ; i++ )
		{
			final GfxCommand c = CommandBuffer.Commands[i];
			if( c.Processor == null )
			{
				break;
			}
			c.Processor.OnCommand(r,c, CommandBuffer);
		}
	}
}

