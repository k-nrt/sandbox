package com.nrt.render;
import android.opengl.GLES30;

public enum Wrap
{
	ClampToEdge(GLES30.GL_CLAMP_TO_EDGE),
	//ClampToBorder(GLES10.GL_CLAMP_TO_BORDER),
	Repeat(GLES30.GL_REPEAT);
	public int Value = 0;
	private Wrap( int value )
	{
		Value=value;
	}
}

