package com.nrt.render;

public class RingVertexBuffer extends RingBuffer implements VertexBuffer
{
	@Override
	public int GetVertexBufferName()
	{
		return Name;
	}

	public RingVertexBuffer( ResourceQueue drq, int size)
	{
		super( drq, com.nrt.render.BufferType.Vertex, size);
	}
}


