package com.nrt.render;

import com.nrt.render.FrameLinearBuffer;

import android.opengl.*;

public class FrameLinearVertexBuffer extends FrameLinearBuffer implements VertexBuffer
{
	@Override
	public int GetVertexBufferName()
	{
		return Name;
	}

	public FrameLinearVertexBuffer( DelayResourceQueue drq, int initialSize, int expandSize)
	{
		super( drq, com.nrt.render.BufferType.Vertex, initialSize, expandSize);
	}
}

