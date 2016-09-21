package com.nrt.render;

import android.opengl.GLES20;

public class RasterizerState
{
	public boolean EnableCullFace = false;
	public CullFace CullFace = CullFace.Back;
	public FrontFace FrontFace = FrontFace.CCW;

	public boolean EnableDither = false;
	
	
	public boolean EnableSampleCoverage = false;
	
	public boolean EnableScissorTest = false;
	
	
	public RasterizerState()
	{}
	
	public RasterizerState( boolean enableCullFace, CullFace cullFace, FrontFace frontFace)
	{
		EnableCullFace = enableCullFace;
		CullFace = cullFace;
		FrontFace = frontFace;
	}
	
	public RasterizerState
	(
		boolean enablrCullFace, 
		CullFace cullFace, 
		FrontFace frontFace,
		boolean enanleDither,
		boolean enableSampleCoverage,
		boolean enableScissorTest
		)
	{
		EnableCullFace = enablrCullFace;
		CullFace = cullFace;
		FrontFace = frontFace;
		
		EnableDither = enanleDither;
		EnableSampleCoverage = enableSampleCoverage;
		EnableScissorTest = enableScissorTest;
		
	}
}
