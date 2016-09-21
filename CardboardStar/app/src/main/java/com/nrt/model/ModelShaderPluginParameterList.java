package com.nrt.model;

import com.nrt.render.*;

public interface ModelShaderPluginParameterList
{
	public void OnUpdateParameter( GfxCommandContext gfxc, ModelShaderPluginUniformList uniform, int iMaterial, ModelMaterial material );
}
