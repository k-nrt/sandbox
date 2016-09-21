package com.nrt.render;

public class BlockVertexBuffer extends BlockBuffer implements VertexBuffer
{
	public BlockVertexBuffer(DelayResourceQueue drq, int blockSizeInBytes, int nbBlocks)
	{
		super(drq, com.nrt.render.BufferType.Vertex, blockSizeInBytes, nbBlocks);
	}

	@Override
	public int GetVertexBufferName()
	{
		return Name;
	}
}
