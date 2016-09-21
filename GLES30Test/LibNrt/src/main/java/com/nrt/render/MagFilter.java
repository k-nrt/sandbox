package com.nrt.render;
import android.opengl.GLES30;

public enum MagFilter
{
	Nearest(GLES30.GL_NEAREST),
	Linear(GLES30.GL_LINEAR);

	public int Value;
	private MagFilter(int value)
	{
		Value=value;
	}
}

