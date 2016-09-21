package com.nrt.cardboardstar;

import com.nrt.collision.Edge;
import com.nrt.math.Float3;
import com.nrt.math.Float4;
import com.nrt.math.Transform3;

public final class CardboardStarGunShot extends CardboardStarShot
{

	public final Float3 Velocity = new Float3(0.0f);

	public float Time = 0.0f;
	public float TimeOut = 0.0f;

	public CardboardStarLightTail LightTail = null;

	public final Float4 ColorIn = new Float4( 1.0f, 1.0f, 3.0f, 1.0f );
	public final Float4 ColorOut = new Float4( 0.0f, 0.0f, 0.0f, 1.0f );

	public boolean Reflect = false;
	public final Float3 ReflectPosition = new Float3( 0.0f );
	public float ReflectionRadius = 0.0f;

	public CardboardStarGunShot( float fBaseRadius, float[] radiusKernel )
	{
		Transform3 transform = Transform3.Local().LoadIdentity();
		LightTail = new CardboardStarLightTail(radiusKernel, transform);

		BaseRadius = fBaseRadius;
		RadiusKernel = radiusKernel;
	}

	public void Spawn(final Transform3 transformPosition, final Float3 v3Velocity)
	{
		Position.Load(transformPosition.W);
		//transformPosition.GetAxisW( Position );
		PrevPosition.Load(transformPosition.W);
		//matrixPosition.GetAxisW( PrevPosition );
		Velocity.Load( v3Velocity );
		Status = EStatus.Busy;

		Time = 0.0f;
		TimeOut = 2.0f;


		//	BaseRadius = 10.0f;
		RenderRadius = BaseRadius;

		LightTail.Reset( transformPosition );

		Reflect = false;
	}

	public void Release()
	{
		Time = 0.0f;
		TimeOut = 0.2f;

		Status = EStatus.Release;
	}

	public void Reflect( Float3 f3Position, float fRadius )
	{
		Reflect = true;
		ReflectPosition.Set( f3Position );
		ReflectionRadius = fRadius;
	}




	public void Update(float fElapsedTime)
	{
		if (Status == EStatus.Idle)
		{
			return;
		}

		if( Status == EStatus.Busy )
		{
			PrevPosition.Set( Position );
			Float3.Mad( Position, Velocity, fElapsedTime, Position);
			DoReflection();
			Time += fElapsedTime;

			LightTail.Update( Position, PrevPosition );	

			//System.Error.WriteLine( String.format( "v %f %f %f", Velocity.X, Velocity.Y, Velocity.Z )  );


			if (Time >= TimeOut)
			{
				Release();
				//Status = EStatus.Idle;
				//System.Error.WriteLine( String.format("timeout %f %f", Time, fElapsedTime) );
			}
		}
		else if( Status == EStatus.Release )
		{
			PrevPosition.Set(Position);
			//Position = Float3.Mad(Velocity, fElapsedTime, Position);
			RenderRadius = BaseRadius*(1.0f - Time/TimeOut);

			LightTail.Update( Position, PrevPosition );	


			Time += fElapsedTime;
			if (Time >= TimeOut)
			{
				Status = EStatus.Idle;
			}
		}

		m_edge.Set( PrevPosition, Position );
	}

	private void DoReflection()
	{
		if( Reflect )
		{
			float fDistance = Float3.Distance( Position, ReflectPosition );
			if( fDistance < ReflectionRadius )
			{
				Float3.SubNormalize( Position, Position, ReflectPosition );
				Float3.Mad( Position, Position, ReflectionRadius, ReflectPosition );
			}
		}
	}

}
