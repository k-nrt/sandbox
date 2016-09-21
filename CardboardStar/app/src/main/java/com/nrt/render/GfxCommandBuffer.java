package com.nrt.render;

import com.nrt.math.Float4;
import com.nrt.math.Float3;

public class GfxCommandBuffer
{
	public GfxCommand[] Commands = null;
	public int[] Integers = null;
	public float[] Floats = null;
	public Object[] Objects = null;
	
	public GfxCommandBuffer()
	{
	}
	
	public GfxCommandBuffer( int nbMaxCommands, int nbMaxIntegers, int nbMaxFloats, int nbMaxObjects )
	{
		Commands = new GfxCommand[nbMaxCommands];
		for(int i = 0 ; i < Commands.length ; i++ )
		{
			Commands[i] = new GfxCommand();
		}
		
		Integers = new int[nbMaxIntegers];
		Floats = new float[nbMaxFloats];
		Objects = new Object[nbMaxObjects];
	}
}
