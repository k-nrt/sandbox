package nrt.render;

import android.opengl.*;

public class SamplerState
{
	public MagFilter MagFilter = nrt.render.MagFilter.Nearest;
	public MinFilter MinFilter = nrt.render.MinFilter.Nearest;

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
