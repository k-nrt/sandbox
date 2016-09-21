package com.nrt.render;

import com.nrt.render.*;

public class FrameLinearIndexBuffer 
	extends FrameLinearBuffer 
	implements IndexBuffer
{
	@Override
	public int GetIndexBufferName()
	{
		return Name;
	}

	public FrameLinearIndexBuffer( DelayResourceQueue drq, int initialSize, int expandSize)
	{
		super( drq, com.nrt.render.BufferType.Index, initialSize, expandSize);
	}
}
