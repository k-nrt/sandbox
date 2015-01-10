package com.nrt.model;

import com.nrt.render.*;

public interface ModelShaderPluginParameterList
{
	public void OnUpdateParameter( Render r, ModelShaderPluginUniformList uniform, int iMaterial, ModelMaterial material );
}
