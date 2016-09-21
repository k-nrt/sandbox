package com.nrt.cardboardstar;

import com.nrt.math.Float3;
import com.nrt.math.Transform3;

public class CardboardStarTarget
{
	public Transform3 Transform = null;
	public Float3 Velocity = null;
	public boolean Busy = false;

	public boolean IsBusy() { return Busy; }
	public boolean IsIdle() { return !Busy; }

	public final Transform3 GetWorldTransform()
	{
		return Transform;
	}

	public final Float3 GetWorldVelocity()
	{
		return Velocity;
	}

	public final void Update( final Transform3 transform, final Float3 velocity, boolean isBusy )
	{
		Transform = transform;
		Velocity = velocity;
		Busy = isBusy;
	}
}


