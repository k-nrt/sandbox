package com.nrt.cardboardstar;

import com.nrt.collision.Edge;
import com.nrt.math.Float3;
import com.nrt.math.Transform3;

public class CardboardStarShot
{
	public final Float3 Position = new Float3(0.0f);
	public final Float3 PrevPosition = new Float3(0.0f);

	public enum EStatus
	{
		Idle,
		Busy,
		Release,
	};

	public enum EType
	{
		Physical,
		Beam,
	};

	public EType Type = EType.Physical;

	public float Power = 0.0f;
	public float BaseRadius = 1.0f;
	public float RenderRadius = 1.0f;

	public float ParticleRadius = 100.0f;

	public float[] RadiusKernel = null;

	public EStatus Status = EStatus.Idle;
	public final Edge m_edge = new Edge();

	public void Spawn(final Transform3 transform, final Float3 v3Velocity)
	{

	}

	public void Release()
	{

	}

	public boolean IsIdle()
	{
		return (Status == EStatus.Idle ? true : false);
	}

	public boolean IsBusy()
	{
		return (Status == EStatus.Busy ? true : false);
	}

	public boolean IsRelease()
	{
		return (Status == EStatus.Release ? true : false);
	}

	public Edge GetEdge()
	{
		return m_edge;//new Edge(PrevPosition, Position);
	}	
}


