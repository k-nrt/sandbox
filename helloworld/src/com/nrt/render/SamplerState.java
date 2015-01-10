package com.nrt.render;

import android.opengl.*;

//import com.nrt.r

public class SamplerState
{
	public MagFilter MagFilter = MagFilter.Nearest;
	public MinFilter MinFilter = MinFilter.Nearest;

	public Wrap WrapS = Wrap.Repeat;
	public Wrap WrapT = Wrap.Repeat;

	public SamplerState() {}

	public SamplerState( MagFilter eMagFilter, MinFilter eMinFilter, Wrap eWrapS, Wrap eWrapT )
	{
		MagFilter = eMagFilter;
		MinFilter = eMinFilter;
		WrapS = eWrapS;
		WrapT = eWrapT;
	}

}
