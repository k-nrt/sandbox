package com.nrt.render;
import android.opengl.*;

public class SamplerState extends RenderResource
{
	public MagFilter MagFilter = MagFilter.Nearest;
	public MinFilter MinFilter = MinFilter.Nearest;

	public Wrap WrapS = Wrap.Repeat;
	public Wrap WrapT = Wrap.Repeat;
	public Wrap WrapR = Wrap.Repeat;
	
	public float MinLod = -1000.0f;
	public float MaxLod = 1000.0f;
	
	public TextureCompareMode CompareMode = TextureCompareMode.None;
	public CompareFunc CompareFunc = CompareFunc.Always;

	public SamplerState() {}

	public SamplerState( MagFilter eMagFilter, MinFilter eMinFilter, Wrap eWrapS, Wrap eWrapT )
	{
		MagFilter = eMagFilter;
		MinFilter = eMinFilter;
		WrapS = eWrapS;
		WrapT = eWrapT;
	}

	@Override
	public void Apply()
	{
		int[] names = { 0 };
		GLES30.glGenSamplers(1, names, 0);
		Name = names[0];
		
		GLES30.glSamplerParameteri(Name, GLES30.GL_TEXTURE_WRAP_S, WrapS.Value);
		GLES30.glSamplerParameteri(Name, GLES30.GL_TEXTURE_WRAP_T, WrapT.Value);
		GLES30.glSamplerParameteri(Name, GLES30.GL_TEXTURE_WRAP_R, WrapS.Value);
		GLES30.glSamplerParameteri(Name, GLES30.GL_TEXTURE_MIN_FILTER, MinFilter.Value);
		GLES30.glSamplerParameteri(Name, GLES30.GL_TEXTURE_MAG_FILTER, MagFilter.Value);
		GLES30.glSamplerParameterf(Name, GLES30.GL_TEXTURE_MIN_LOD, MinLod);
		GLES30.glSamplerParameterf(Name, GLES30.GL_TEXTURE_MAX_LOD, MaxLod);
		GLES30.glSamplerParameteri(Name, GLES30.GL_TEXTURE_COMPARE_MODE, CompareMode.Value );
		GLES30.glSamplerParameteri(Name, GLES30.GL_TEXTURE_COMPARE_FUNC, CompareFunc.Value);
	}

	
	
}

