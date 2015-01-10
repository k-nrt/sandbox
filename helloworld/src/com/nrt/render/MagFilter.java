package com.nrt.render;
import android.opengl.*;

public enum MagFilter
{
	Nearest(GLES20.GL_NEAREST),
	Linear(GLES20.GL_LINEAR);
	
	public int Value;
	private MagFilter(int value)
	{
		Value=value;
	}
}
