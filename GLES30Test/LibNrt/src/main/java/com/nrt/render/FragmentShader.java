package com.nrt.render;

public class FragmentShader extends Shader
{
	public FragmentShader()
	{}

	public FragmentShader( ResourceQueue queue, String[] arrayLines)
	{
		super( queue, Shader.EType.Fragment, arrayLines );
	}
}


