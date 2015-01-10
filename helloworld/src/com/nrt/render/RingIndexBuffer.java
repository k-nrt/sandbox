package nrt.render;

import android.opengl.GLES20;

public class RingIndexBuffer extends RingBuffer implements IndexBuffer
{
	@Override
	public int GetIndexBufferName()
	{
		return Name;
	}
	
	public RingIndexBuffer( DelayResourceQueue drq, int size)
		//throws ThreadForceDestroyException
	{
		super( drq, nrt.render.BufferType.Index, size);
	}
}

