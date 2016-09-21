package com.nrt.cardboardstar;

import com.nrt.math.Transform3;
import com.nrt.model.*;
import com.nrt.collision.*;
import com.nrt.math.FMath;
import com.nrt.math.Float3;
import com.nrt.math.Float4;
import com.nrt.math.Float4x4;

import com.nrt.framework.SubSystem;

public class CardboardStarStarShip
{

	public final Transform3 Transform = new Transform3().LoadIdentity();
	public final Transform3 TransformInverse = new Transform3().LoadIdentity();

	public final Float3 Velocity = new Float3();
	public final Float3 AngularVelocity = new Float3();

	public enum EStatus
	{
		Idle,
		Busy,
		Explosion,
		ExplosionFinal,
	};

	public EStatus Status = EStatus.Idle;

	//public Model Model = null;
	public GameModel Model = new GameModel();
	public CollisionMesh Collision = null;

	//public float Shield = 400.0f;

	public float m_fTime = 0.0f;
	public float m_fTimeOut = 0.0f;

	public float DiscardRadius = 0.0f;

	public int m_nbFireRemains = 0;
	public float m_fFireTime = 0.0f;

	//public float ShieldRadius = 0.0f;
	//public final Sphere ShieldSphere = new Sphere();
	/*
	 public float AntiBeamFieldRadius = 1200.0f;
	 public float AntiBeamFieldDamage = 0.0f;
	 public float AntiBeamFieldDamageLimit = 100.0f;
	 */
	public final Float3 Position = new Float3(0.0f);

	public final GameAntiBeamField AntiBeamField = new GameAntiBeamField();
	public float BodyDamage = 0.0f;
	public float BodyDamageLimit = 1000.0f;

	public CardboardStarStarShip()
	{}

	public boolean IsIdle()
	{ return ((Status == EStatus.Idle) ? true : false); }
	public boolean IsBusy()
	{ return ((Status == EStatus.Busy) ? true : false); }
	public boolean IsExplosion()
	{ return ((Status == EStatus.Explosion) ? true : false); }
	public boolean IsExplosionFinal()
	{ return ((Status == EStatus.ExplosionFinal) ? true : false); }

	public void Spawn(final Transform3 transform, Model model, CollisionMesh collisionMesh)
	{
		Transform.Load(transform);
		TransformInverse.LoadInverse(transform);
		

		Velocity.Set(0.0f);
		AngularVelocity.Set(0.0f);

		Status = EStatus.Busy;

		Model.Model = model;
		Collision = collisionMesh;

		//Shield = 1000.0f;

		DiscardRadius = 0.0f;

		AntiBeamField.Spawn(Transform3.Local().LoadRotationX(-FMath.PI / 2.0f), Transform3.Local().Load(Transform));
		//AntiBeamField.Spawn( new Transform3().Load( transform.Values ) );
		BodyDamage = 0.0f;


		//ShieldSphere.Radius = 1500.0f;
	}

	public void Fire(CardboardStarShot[] shots, final Float3 f3Target, final Float3 f3Position)
	{
		for (CardboardStarShot shot : shots)
		{
			if (shot.IsIdle())
			{
				Float3 f3Velocity = Float3.Local();
				Float3.SubNormalize(f3Velocity, f3Target, f3Position);
				Float3.Mul(f3Velocity, f3Velocity, 2000.0f);


				shot.Spawn(Transform, f3Velocity);
				break;
			}
		}
	}
	public final void Update
	(
		float fElapsedTime,
		final CardboardStarParticle particle, 
		final CardboardStarTarget targetPlayer,
		final CardboardStarShot[] shots
	)
	{
		switch (Status)
		{
			case Idle:
				break;

			case Busy:
				if (m_nbFireRemains <= 0 && SubSystem.Rand.Float() < 1.0f / 60.0f && targetPlayer.Busy)
				{
					Float3 f3Target = Float3.Local().Load(targetPlayer.GetWorldTransform().W);
					//targetPlayer.GetWorldTransform().GetAxisW(f3Target);
					Float3 f3Position = Float3.Local().Load(Transform.W);
					//f3Position.Transform.GetAxisW(f3Position);

					if (Float3.Distance(f3Target, f3Position) < 4000.0f)
					{
						m_nbFireRemains = 4;
					}
				}


				if (m_nbFireRemains > 0)
				{
					if (m_fFireTime <= 0.0f)
					{
						final Float3 f3Target = Float3.Local().Load(targetPlayer.GetWorldTransform().W);
						//targetPlayer.GetWorldTransform().GetAxisW(f3Target);
						final Float3 f3Position = Float3.Local().Load(Transform.W);
						//Transform.GetAxisW(f3Position);

						f3Target.Mad(targetPlayer.Velocity, 0.2f * (float)m_nbFireRemains, f3Target);


						Fire(shots, f3Target, f3Position);

						m_nbFireRemains--;
						m_fFireTime += 0.1f;
					}
					else
					{
						m_fFireTime -= fElapsedTime;
					}
				}
				break;

			case Explosion:
				{
					Float4 f4 = Float4.Local(0.0f, 0.0f, 0.0f, 1.0f);

					Float3 f3 = Float3.Local();
					if (SubSystem.Rand.Float() < 0.5f)
					{
						//Transform.AxisW(f3);
						f4.X = SubSystem.Rand.Float(Model.Model.Min.X, Model.Model.Max.X);
						f4.Y = SubSystem.Rand.Float(Model.Model.Min.Y, Model.Model.Max.Y);

						float c = (Model.Model.Min.Z + Model.Model.Max.Z) * 0.5f;
						float r = (Model.Model.Max.Z - Model.Model.Min.Z) * 0.5f * m_fTime / m_fTimeOut;
						f4.Z = SubSystem.Rand.Float(c - r, c + r);
						Float4x4.Mul(f3, f4, Float4x4.Local(Transform));
						particle.SpawnSmallExplosion(f3, SubSystem.Rand.Float(100.0f, 200.0f));

						Float3 f3Size = Float3.SubNormalize(Float3.Local(), Model.Model.Max, Model.Model.Min);

						Float3.Mul(f3Size, 0.05f, f3Size);

						Float3.Add(AngularVelocity, AngularVelocity,
								   Float3.Local(
									   SubSystem.Rand.Float(-f3Size.X, f3Size.X), 
									   SubSystem.Rand.Float(-f3Size.Y, f3Size.Y), 
									   SubSystem.Rand.Float(-f3Size.Z, f3Size.Z)));

						Float3.Add(Velocity, Velocity,
								   Float3.Rand(Float3.Local(), -10.0f, 10.0f));
					}

					m_fTime += fElapsedTime;
					if (m_fTimeOut < m_fTime)
					{
						Status = EStatus.ExplosionFinal;
						m_fTime = 0.0f;
						m_fTimeOut = 2.0f;


						//Float3 f3 = Float3.Local();
						//Transform.AxisW(f3);
						particle.SpawnFlash(f3, Float3.Local(0.0f), 2000.0f);

						for (int i = 0 ; i < 4 ; i++)
						{
							//Float3 f3 = Float3.Local();
							//Transform.AxisW(f3);
							f4.X = SubSystem.Rand.Float(Model.Model.Min.X, Model.Model.Max.X);
							f4.Y = SubSystem.Rand.Float(Model.Model.Min.Y, Model.Model.Max.Y);
							f4.Z = SubSystem.Rand.Float(Model.Model.Min.Z, Model.Model.Max.Z);
							Float4x4.Mul(f3, f4, Float4x4.Local( Transform));

							particle.SpawnSmallExplosion(f3, SubSystem.Rand.Float(200.0f, 400.0f));
						}

					}
				}
				break;

			case ExplosionFinal:
				{
					DiscardRadius = m_fTime / m_fTimeOut;

					m_fTime += fElapsedTime;
					if (m_fTimeOut < m_fTime)
					{
						Status = EStatus.Idle;
					}
				}
				break;
		}

		final Float3 v3X = Float3.Local().Load(Transform.XYZ.X);
		final Float3 v3Y = Float3.Local().Load(Transform.XYZ.Y);
		final Float3 v3Z = Float3.Local().Load(Transform.XYZ.Z);
		final Float3 v3W = Float3.Local().Load(Transform.W);

		//. 回転の積分

		final Float4x4 matrix = Float4x4.Local(Transform);
		//matrix.AxisW = Vector3.Zero;
		//matrix = Matrix4.RotationAxis( v3Y, AngularVelocity.Y*fElapsedTime )*matrix;
		//matrix = Matrix4.RotationAxis( v3X, AngularVelocity.X*fElapsedTime )*matrix;
		//matrix = Matrix4.RotationAxis( v3Z, AngularVelocity.Z*fElapsedTime )*matrix;
		matrix.SetAxisW(Float3.Local().Set(0.0f));
		Float4x4.Mul(
			matrix, 
			matrix,
			Float4x4.Rotation(Float4x4.Local(), v3Z, AngularVelocity.Z * fElapsedTime),
			Float4x4.Rotation(Float4x4.Local(), v3X, AngularVelocity.X * fElapsedTime),
			Float4x4.Rotation(Float4x4.Local(), v3Y, AngularVelocity.Y * fElapsedTime)
		);

		Transform.Load(matrix);

		//. 速度の変更.

		//. 位置積分.
		//v3W += Velocity * fElapsedTime;
		v3W.Mad( Velocity, fElapsedTime, v3W);

		//. 姿勢へ戻す.
		//Transform.AxisW = v3W;
		Transform.W.Load(v3W);

		Position.Set(v3W);

		AntiBeamField.OnUpdate(fElapsedTime, Transform3.Local().Load(Transform));
		//AntiBeamField.Transform.Load( Transform.Values );

	}

	public final Intersection Intersect(final Edge edgeWorld)
	{
		if (Status == EStatus.Idle)
		{
			return null;
		}

		if (Collision == null)
		{
			return null;
		}

		final Edge edge = Edge.Local();

		//Float4x4.Mul(edge.Start, Float4.Local().Set(edgeWorld.Start, 1.0f), Float4x4.Local(TransformInverse));
		//Float4x4.Mul(edge.End,   Float4.Local().Set(edgeWorld.End, 1.0f),   Float4x4.Local(TransformInverse));
		edge.Start.Mul(edgeWorld.Start,TransformInverse);
		edge.End.Mul(edgeWorld.End,TransformInverse);
		
		//Intersection intersection = Collision.Intersect( edge );
		//if( intersection == null )
		final Intersection intersection = Intersection.Local();// Collision.Intersect( edge );
		if (Collision.Intersect(edge, intersection) == false)
		{
			return null;
		}
		
		//Float4x4.Mul(intersection.Position, Float4.Local().Set(intersection.Position, 1.0f), Float4x4.Local(Transform));
		//Float4x4.Mul(intersection.Normal,   Float4.Local().Set(intersection.Normal, 0.0f),   Float4x4.Local(Transform));
		intersection.Position.Mul(intersection.Position,Transform);
		intersection.Normal.Mul(intersection.Normal,Transform.XYZ);
		
		//System.Error.WriteLine( "hit" );
		return intersection;
	}

	public final boolean Intersect
	(
		final Sphere sphereWorld, 
		final Intersection intersectionWorld
	)
	{
		if (Status == EStatus.Idle)
		{
			return false;
		}

		if (Collision == null)
		{
			return false;
		}

		final Sphere sphere = new Sphere();
		//Float4x4.Mul(sphere.Position, Float4.Local().Set(sphereWorld.Position, 1.0f), TransformInverse);
		sphere.Position.Mul(sphereWorld.Position,TransformInverse);
		sphere.Radius = sphereWorld.Radius;
		final Intersection intersection = Intersection.Local().Clean();// Collision.Intersect( edge );

		if (Collision.IntersectToSphere(sphere, intersection) == false)
		{
			return false;
		}

		//Float4x4.Mul(intersectionWorld.Position, Float4.Local().Set(intersection.Position, 1.0f), Transform);
		//Float4x4.Mul(intersectionWorld.Normal,   Float4.Local().Set(intersection.Normal, 0.0f),   Transform);
		intersectionWorld.Position.Mul(intersection.Position, Transform);
		intersectionWorld.Normal.Mul(intersection.Normal,Transform.XYZ);
		
		intersectionWorld.Distance = intersection.Distance;

		return true;
	}

	public final boolean AddBodyDamage
	(
		final CardboardStarShot shot, 
		final CardboardStarParticle particle
	)
	{
		if (IsBusy())
		{
			BodyDamage += shot.Power;

			if (BodyDamage >= BodyDamageLimit)
			{
				Status = EStatus.Explosion;
				m_fTime = 0.0f;
				m_fTimeOut = 5.0f;

				BodyDamage = BodyDamageLimit;

				return true;
			}
			else
			{
				return false;
			}
		}
		else
		{
			return false;
		}
	}

	public final boolean AddAntiBeamFieldDamage
	(
		final CardboardStarShot shot, 
		final CardboardStarParticle particle
	)
	{
		if (IsBusy())
		{
			AntiBeamField.Damage += shot.Power;

			AntiBeamField.m_adsr.Start();

			if (AntiBeamField.Damage >= AntiBeamField.DamageLimit)
			{
				AntiBeamField.Damage = AntiBeamField.DamageLimit;

				return true;
			}
			else
			{
				return false;
			}
		}
		else
		{
			return false;
		}		
	}
	
}
