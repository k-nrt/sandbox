package com.nrt.render;

import android.opengl.GLES20;

public enum StencilFunc
{
	Never(GLES20.GL_NEVER),
	Less(GLES20.GL_LESS),
	Equal(GLES20.GL_EQUAL),
	LessEqual(GLES20.GL_LEQUAL),
	Greater(GLES20.GL_GREATER),
	NotEqual(GLES20.GL_NOTEQUAL),
	GreaterEqual(GLES20.GL_GEQUAL),
	Always(GLES20.GL_ALWAYS);

	public int Value = GLES20.GL_LESS;

	public StencilFunc( int value )
	{
		Value = value;
	}
}

