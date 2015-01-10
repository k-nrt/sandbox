package nrt.model;

import nrt.math.Float3;
import nrt.math.Float4x4;

public class ModelLocator
{
	public String Name = "";
	public int Joint = 0;
	public Float4x4 Local = Float4x4.Identity();
	public Float3 Scale = new Float3(1.0f); 
}

