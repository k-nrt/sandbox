package com.nrt.render;

public class BlockIndexBuffer extends BlockBuffer implements IndexBuffer
{
	public BlockIndexBuffer(DelayResourceQueue drq, int blockSizeInBytes, int nbBlocks)
	{
		super(drq, com.nrt.render.BufferType.Index, blockSizeInBytes, nbBlocks);
	}

	@Override
	public int GetIndexBufferName()
	{
		return Name;
	}
}

