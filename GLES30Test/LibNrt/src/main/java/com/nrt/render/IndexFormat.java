package com.nrt.render;
import android.opengl.GLES30;
public enum IndexFormat
{
	UnsignedShort(GLES30.GL_UNSIGNED_SHORT),
	UnsignedInt(GLES30.GL_UNSIGNED_INT);

	public int Value = 0;
	private IndexFormat(int value)
	{
		Value = value;
	}
}



