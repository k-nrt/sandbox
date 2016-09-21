package com.nrt.render;

import android.opengl.GLES30;

public enum RenderTextureFormat
{
	ALPHA(GLES30.GL_ALPHA),
	RGB(GLES30.GL_RGB),
	RGBA(GLES30.GL_RGBA),
	LUMINANCE(GLES30.GL_LUMINANCE),
	LUMINANCE_ALPHA(GLES30.GL_LUMINANCE_ALPHA);

	public int Value = 0;
	private RenderTextureFormat( int value )
	{
		Value = value;
	}
}

