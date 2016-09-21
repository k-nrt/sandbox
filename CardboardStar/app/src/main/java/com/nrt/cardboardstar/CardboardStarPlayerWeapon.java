package com.nrt.cardboardstar;
import com.nrt.math.*;

public final class CardboardStarPlayerWeapon
{
	public String Name = "";
	public int Bursts = 0;
	public float Interval = 0.1f;

	public float Speed = 0.0f;
	public float Pop = 0.0f;

	public Float4x4 SpawnPosition = Float4x4.Identity();

	public float Time = 0.0f;

	public CardboardStarShot[] Shots = null;


	public CardboardStarPlayerWeapon
	(
		String strName, 
		int nbBursts, 
		float fInterval,
		float fSpeed, 
		float fPop, 
		CardboardStarShot[] shots
	)
	{
		Name = strName;
		Bursts = nbBursts;
		Interval = fInterval;
		Speed = fSpeed;
		Pop = fPop;
		Shots = shots;

	}

	public float GetReloadRate()
	{
		return FMath.Clamp(1.0f - Time / Interval, 0.0f, 1.0f);
	}

	public void Update(float fElapsedTime)
	{
		if (Time >= 0.0f)
		{
			Time -= fElapsedTime;
		}
	}

	public boolean Spawn(final Transform3 transformRoot, final Float3 v3RootVelocity)
	{
		if (Time > 0.0f)
		{
			return false;
		}

		for (CardboardStarShot shot : Shots)
		{
			if (shot.IsIdle())
			{
				final Transform3 transform = Transform3.Local().Mul
				(
					Transform3.Local().LoadTranslation(-50.0f, -30.0f, 0.0f),
					//Transform3.Local().LoadTranslation(0.0f, -3.0f, 0.0f),
					transformRoot
				);

				final Float3 f3Velocity = Float3.Local();
				final Float3 f3Z = Float3.Local().Load(transformRoot.XYZ.Z);
				
				
				//matrixRoot.GetAxisZ(f3Z);
				f3Velocity.Mad( f3Z, Speed, v3RootVelocity);
				f3Velocity.Add( Float3.Rand(Float3.Local(), -Pop, Pop), f3Velocity);
				shot.Spawn(transform, f3Velocity);

				Time = Interval;
				return true;
			}
		}
		return false;
	}
}
