package com.nrt.render;

import android.opengl.GLES30;

public class RingIndexBuffer extends RingBuffer implements IndexBuffer
{
	@Override
	public int GetIndexBufferName()
	{
		return Name;
	}

	public RingIndexBuffer( ResourceQueue drq, int size)
	{
		super( drq, com.nrt.render.BufferType.Index, size);
	}
}


