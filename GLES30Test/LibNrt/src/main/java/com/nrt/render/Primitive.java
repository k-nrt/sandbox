package com.nrt.render;

import android.opengl.GLES30;

public enum Primitive
{
	Points(GLES30.GL_POINTS), 
	LineStrip(GLES30.GL_LINE_STRIP), 
	LineLoop(GLES30.GL_LINE_LOOP), 
	Lines(GLES30.GL_LINES), 
	TriangleStrip(GLES30.GL_TRIANGLE_STRIP), 
	TriangleFan(GLES30.GL_TRIANGLE_FAN),
	Triangles(GLES30.GL_TRIANGLES);
	
	public int Value;
	public Primitive(int value)
	{
		Value = value;
	}
}
