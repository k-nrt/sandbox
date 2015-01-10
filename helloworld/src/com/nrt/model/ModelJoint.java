package nrt.model;

import nrt.math.Float4x4;

public class ModelJoint
{
	public String Name = "";
	public int Parent = -1;
	public Float4x4 Local = Float4x4.Identity();
	public Float4x4 BindPoseInverse = Float4x4.Identity();
}

