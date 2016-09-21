package com.nrt.render;
import android.opengl.GLES30;
public enum MinFilter
{
	Nearest(GLES30.GL_NEAREST),
	Linear(GLES30.GL_LINEAR),
	NearestMipmapNearest(GLES30.GL_NEAREST_MIPMAP_NEAREST),
	NearestMipmapLinear(GLES30.GL_NEAREST_MIPMAP_LINEAR),
	LinearMipmapNearest(GLES30.GL_LINEAR_MIPMAP_NEAREST),
	LinearMipmapLinear(GLES30.GL_LINEAR_MIPMAP_LINEAR);

	public int Value;
	private MinFilter( int value )
	{
		Value=value;
	}
}

