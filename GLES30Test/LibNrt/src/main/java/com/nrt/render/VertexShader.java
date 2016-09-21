package com.nrt.render;

public class VertexShader extends Shader
{
	public VertexShader()
	{}

	public VertexShader( ResourceQueue queue, String[] arrayLines)
	{
		super( queue, Shader.EType.Vertex, arrayLines);
	}
}

