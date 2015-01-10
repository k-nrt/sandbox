package com.nrt.render;

import android.opengl.*;

public class RingVertexBuffer extends RingBuffer implements VertexBuffer
{
	@Override
	public int GetVertexBufferName()
	{
		return Name;
	}

	public RingVertexBuffer( DelayResourceQueue drq, int size)
		//throws ThreadForceDestroyException
	{
		super( drq, com.nrt.render.BufferType.Vertex, size);
	}
}

