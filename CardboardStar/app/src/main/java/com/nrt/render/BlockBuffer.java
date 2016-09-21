package com.nrt.render;

import java.nio.*;
import android.opengl.*;
import com.nrt.math.*;

public class BlockBuffer extends RenderResource
{
	public BufferType BufferType = BufferType.Index;
	public int BlockSizeInBytes = 0;
	public int[] Names = null;

	public ByteBuffer m_scratchBuffer = null;
	//public int ScratchBufferSizeInBytes = 0;
	public int BlockIndex = 0;

	public BlockBuffer(DelayResourceQueue drq, BufferType eType, int blockSizeInBytes, int nbBlocks)
	{
		BufferType = eType;
		BlockSizeInBytes = blockSizeInBytes;
		Names = new int[nbBlocks];

		m_scratchBuffer = ByteBuffer.allocateDirect(BlockSizeInBytes);
		m_scratchBuffer.order(ByteOrder.nativeOrder());
		//ScratchBufferSizeInBytes = scratchBufferSizeInBytes;

		drq.Add(this);
	}

	@Override
	public void Apply()
	{
		//int[] names = { 0 };
		GLES20.glGenBuffers(Names.length, Names, 0);
		Name = Names[BlockIndex];

		m_scratchBuffer.position(0);
		for(int i = 0 ; i < Names.length ; i++)
		{
			if(0<Names[i])
			{
				GLES20.glBindBuffer(BufferType.Value, Names[i]);
				GLES20.glBufferData(BufferType.Value, BlockSizeInBytes, m_scratchBuffer, GLES20.GL_DYNAMIC_DRAW);
				GLES20.glBindBuffer(BufferType.Value, 0);
			}
		}
	}
	
	public void Copy(int src, int sizeInBytes)
	{
		int dst = m_scratchBuffer.position();
		byte[] buffer = new byte[sizeInBytes];
		
		m_scratchBuffer.position(src);
		m_scratchBuffer.get(buffer);
		
		m_scratchBuffer.position(dst);
		m_scratchBuffer.put(buffer);
	}

	public void Begin(int position)
	{
		m_scratchBuffer.position(position);
		for(;Names[BlockIndex]<=0;BlockIndex++)
		{
			
		}
		Name = Names[BlockIndex];
	}
	
	public int Capacity()
	{
		return BlockSizeInBytes - m_scratchBuffer.position();
	}

	public void Add(short x)
	{
		if((m_scratchBuffer.position()+2)<BlockSizeInBytes)
		{
			m_scratchBuffer.putShort(x);
		}
	}

	public void Add(int x)
	{
		if((m_scratchBuffer.position()+4)<BlockSizeInBytes)
		{
			m_scratchBuffer.putInt(x);
		}
	}

	public void Add(float x)
	{
		if((m_scratchBuffer.position()+4)<BlockSizeInBytes)
		{
			m_scratchBuffer.putFloat(x);
		}
	}

	public void Add(float x, float y)
	{
		if((m_scratchBuffer.position()+8)<BlockSizeInBytes)
		{
			m_scratchBuffer.putFloat(x);
			m_scratchBuffer.putFloat(y);
		}
	}

	public void Add(float x, float y, float z)
	{
		if((m_scratchBuffer.position()+12)<BlockSizeInBytes)
		{
			m_scratchBuffer.putFloat(x);
			m_scratchBuffer.putFloat(y);
			m_scratchBuffer.putFloat(z);
		}
	}

	public void Add(float x, float y, float z, float w)
	{
		if((m_scratchBuffer.position()+16)<BlockSizeInBytes)
		{
			m_scratchBuffer.putFloat(x);
			m_scratchBuffer.putFloat(y);
			m_scratchBuffer.putFloat(z);
			m_scratchBuffer.putFloat(w);
		}
	}

	public void Add(Float3 xyz)
	{
		if((m_scratchBuffer.position()+12)<BlockSizeInBytes)
		{
			m_scratchBuffer.putFloat(xyz.X);
			m_scratchBuffer.putFloat(xyz.Y);
			m_scratchBuffer.putFloat(xyz.Z);
		}
	}

	public int End()
	{
		int size = m_scratchBuffer.position();
		if (size <= 0)
		{
			return 0;
		}
		
		m_scratchBuffer.position(0);
		GLES20.glBindBuffer(BufferType.Value, Name);
		//GLES20.glBufferData(BufferType.Value, BlockSizeInBytes, m_scratchBuffer, GLES20.GL_DYNAMIC_DRAW);
		GLES20.glBufferSubData(BufferType.Value, 0, size, m_scratchBuffer);
		
		GLES20.glBindBuffer(BufferType.Value, 0);
		
		BlockIndex = (BlockIndex+1)%Names.length;
		
		return 0;
	}

	/*
	public void Align(int alignment)
	{
		if ((Position % alignment) > 0)
		{
			Position += alignment - (Position % alignment);
		}
	}
	*/
}

