package com.nrt.render;

import android.opengl.GLES20;

public enum CullFace
{
	Front(GLES20.GL_FRONT),
	Back(GLES20.GL_BACK),
	FrontAndBack(GLES20.GL_FRONT_AND_BACK);
	
	public int Value = 0;
	
	public CullFace(int value)
	{
		Value = value;
	}
}
