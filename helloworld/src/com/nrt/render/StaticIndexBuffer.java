package nrt.render;

public class StaticIndexBuffer extends nrt.render.Buffer implements IndexBuffer
{
	@Override
	public int GetIndexBufferName()
	{
		return this.Name;
	}

	public StaticIndexBuffer( DelayResourceQueue drq, short[] data )
	//throws ThreadForceDestroyException
	{
		super( drq, BufferType.Index, data, BufferUsage.StaticDraw);
	}

	public StaticIndexBuffer( DelayResourceQueue drq, java.nio.Buffer data )
	//throws ThreadForceDestroyException
	{
		super( drq, BufferType.Index, data, BufferUsage.StaticDraw);
	}
}

