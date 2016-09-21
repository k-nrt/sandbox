package com.nrt.render;
import android.opengl.GLES30;

public enum BufferType
{
	Unknown(0),
	Vertex(GLES30.GL_ARRAY_BUFFER),
	Index(GLES30.GL_ELEMENT_ARRAY_BUFFER);

	public int Value = 0;
	private BufferType(int glEnum )
	{
		Value = glEnum;
	}
}


