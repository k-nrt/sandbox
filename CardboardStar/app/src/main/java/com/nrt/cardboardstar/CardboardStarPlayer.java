package com.nrt.cardboardstar;

import com.nrt.math.*;
import com.nrt.render.*;
public class CardboardStarPlayer
{
	public final Transform3 Transform = new Transform3();
	public final Float3 AngularVelocity = new Float3(0.0f);
	public final Float3 Velocity = new Float3(0.0f);
	
	//public final Float4x4 LocalHeadCancelTransform = new Float4x4();
	
	public final Transform3 LocalHeadTransform = new Transform3();
	public final Transform3 WorldHeadTransform = new Transform3();
	
	public final Transform3 LocalHeadCancelTransform = new Transform3();
	public final Transform3 WorldHeadCancelTransform = new Transform3();
	public final Float4x4 HeadCancelView = new Float4x4();
	
	public final Transform3 LocalHeadRotationBase = new Transform3();
	
	public static float MaxSpeed = 500.0f;
	
	public final Float3 Forward(){ return Transform.XYZ.Z;}
	public final Float3 Up(){return Transform.XYZ.Y;}
	public final Float3 Position(){return Transform.W;}
	
	public final Float3 HeadForward(){ return WorldHeadTransform.XYZ.Z;}
	public final Float3 HeadUp(){return WorldHeadTransform.XYZ.Y;}
	public final  Float3 HeadPosition(){return WorldHeadTransform.W;}
	
	
	public CardboardStarGunShot[] Shots = null;
	
	public enum EWeapon
	{
		MachineGun,
		AirToAirMissile,
		AntiFleetBeam,
		Max
	};

	public EWeapon m_eWeapon = EWeapon.MachineGun;
	public CardboardStarPlayerWeapon[] m_weaponDescs = null;//new CardboardStarPlayerWeapon[EWeapon.Max.ordinal()];
	
	public int m_nbFireRemains = 0;
	
	public static float AdjustDeadband(float fValue, float fDeadband)
	{
		if(0.0f < fValue )

		{
			return FMath.Max( fValue - fDeadband, 0.0f);
		}
		else
		{
			return FMath.Min(fValue + fDeadband, 0.0f);
		}
	}
	
	public CardboardStarPlayer()
	{
		Transform.LoadIdentity();
		//Float4x4.Identity(Transform);
		Transform.LoadTranslation(0.0f,0.0f,-2000.0f);
		
		//Float4x4.Identity(LocalHeadCancelTransform);
		//Float4x4.Identity(LocalHeadTransform);
		LocalHeadTransform.LoadIdentity();
		WorldHeadTransform.LoadIdentity();
		LocalHeadCancelTransform.LoadIdentity();
		WorldHeadCancelTransform.LoadIdentity();
		
		float[] arrayShotRadiusKernel =
		{
			0.0f,1.0f,0.5f, 0.0f, //0.25f, 0.25f, 0.25f, 0.0f,
		};

		Shots = new CardboardStarGunShot[20];
		for (int i = 0 ; i < Shots.length ; i++)
		{
			Shots[i] = new CardboardStarGunShot(5.0f, arrayShotRadiusKernel);
			Shots[i].Type = CardboardStarShot.EType.Physical;
			Shots[i].Power = 4.0f;
			Shots[i].ParticleRadius = 50.0f;
			Shots[i].ColorIn.Set(2.0f, 1.6f, 0.8f, 1.0f); 
			Shots[i].ColorOut.Set(0.5f, 0.0f, 0.0f, 0.0f); 
		}
		
		m_weaponDescs = new CardboardStarPlayerWeapon[]
		{
			new CardboardStarPlayerWeapon("GUN", 3, 0.05f,8000.0f, 400.0f, Shots),
			new CardboardStarPlayerWeapon("AAM", 4, 0.15f, 500.f, 0.0f, Shots),//Missiles),
			new CardboardStarPlayerWeapon("AFC", 1, 0.5f, 6000.0f, 0.0f, Shots),//Beams),
		};
	}
	
	public void Update
	(
		float fElapsedTime,
		final Float4x4 headTransform, 
		final CardboardStarUiButton buttonRight,
		final CardboardStarUiButton buttonLeft
	)
	{
		final Float3 f3HeadForward = headTransform.GetAxisZ(Float3.Local());
		final Float3 f3ZAxis = Float3.Local(0.0f,0.0f,1.0f);
		
		final Float3 f3RotationAxis = Float3.Local().Cross( f3HeadForward, f3ZAxis);
		/*
		if( 0.0f < Float3.Length(f3RotationAxis))
		{
			Float3.Normalize(f3RotationAxis,f3RotationAxis);
			float fDot = Float3.Dot(f3HeadForward,f3ZAxis);
			
			float fRot = FMath.Acos(fDot);
			
			Float4x4 f4x4Rot = Float4x4.Mov(Float4x4.Local(),headTransform);
			f4x4Rot.SetW(0.0f,0.0f,0.0f,1.0f);
			
			Float4x4.Mul
			(
				LocalHeadTransform,
				f4x4Rot,
				Float4x4.Rotation(Float4x4.Local(),f3RotationAxis,-fRot)
			);
			
			LocalHeadTransform.SetW(headTransform.Values, 12);
			
			//Float4x4.Invert(LocalHeadTransform, headTransform);
			
			//Float4x4.Mov(LocalHeadTransform, headTransform);
			//Float4x4.Identity(LocalHeadTransform);
		}
		*/

		boolean b = false;
		if( buttonRight.IsHold()) 
		{
			final Rotation3 headInverse = Rotation3.Local().Load(Float4x4.Invert(Float4x4.Local(),headTransform));
			//final Float4x4 headInverse = Float4x4.Invert(Float4x4.Local(),headTransform);
			
			LocalHeadCancelTransform.XYZ.SlerpZYX(0.1f,LocalHeadCancelTransform.XYZ,headInverse);
			//Slerp3x3ZYX(LocalHeadCancelTransform,0.05f, LocalHeadCancelTransform, headInverse);
		
			
			if( Float.isNaN(LocalHeadCancelTransform.XYZ.X.X))
			{
				LocalHeadCancelTransform.XYZ.Load(headInverse);
			}
			
			
			LocalHeadCancelTransform.W.Load(0.0f,0.0f,0.0f);
		}
		//Float4x4.Identity(LocalHeadCancelTransform);
		//Float4x4.Mul(LocalHeadTransform, LocalHeadCancelTransform, LocalHeadTransform);
		
		LocalHeadTransform.XYZ.Mul(Rotation3.Local().Load(headTransform),LocalHeadCancelTransform.XYZ);
		//LocalHeadTransform.SetAxisW(headTransform.GetAxisW(Float3.Local()));
		
		if(buttonRight.IsPush())
		{
			//Float4x4.Mov(LocalHeadRotationBase,headTransform);
			//LocalHeadRotationBase.SetAxisW(Float3.Local(0.0f,0.0f,0.0f));
			
			LocalHeadRotationBase.XYZ.Load(headTransform);
		}
		
		boolean isRotation = false;
		float fRX = 0.0f;
		float fRY = 0.0f;
		float fRZ = 0.0f;
		if( buttonLeft.IsDown() )
		{
			fRX = -buttonLeft.Velocity.Y/500.0f;
			fRY = -buttonLeft.Velocity.X/500.0f;		
			fRZ = -buttonLeft.Rotation*5.0f;
			
			isRotation = true;
		}
		
		if(false)//buttonRight.IsHold())
		{
			final Rotation3 rotation  = Rotation3.Local().Mul(
				Rotation3.Local().Load(headTransform),
				Rotation3.Local().LoadTranspose(LocalHeadRotationBase.XYZ));

			final Float3 f3Z = Float3.Local().Load(rotation.Z);
			f3Z.Y = 0.0f;
			f3Z.LoadNormalize(f3Z);
			float rx = FMath.Atan2(f3Z.X,f3Z.Z);

			final Float3 f3Y = Float3.Local().Load(rotation.Y);
			f3Y.X = 0.0f;
			f3Y.LoadNormalize(f3Y);
			float ry = FMath.Atan2(f3Y.Z, f3Y.Y);

			final Float3 f3X = Float3.Local().Load(rotation.X);
			f3X.Z = 0.0f;
			f3X.LoadNormalize(f3X);
			float rz = FMath.Atan2(f3X.Y, f3X.X);

			rx = AdjustDeadband(rx,FMath.ToRad(0.5f))*1.0f;
			ry = AdjustDeadband(ry,FMath.ToRad(0.5f))*1.0f;
			rz = AdjustDeadband(rz,FMath.ToRad(0.5f))*1.0f;			
			
			fRX += ry;
			fRY += rx;
			fRZ += rz;
			
			isRotation = true;
		}
		
		if(isRotation )
		{
			AngularVelocity.Lerp(0.9f, AngularVelocity, Float3.Local(fRX,fRY,fRZ));
		}
		else
		{
			AngularVelocity.Lerp(0.01f, AngularVelocity, Float3.Local(0.0f));
		}
		
		{
			if(AngularVelocity.IsNaN())
			{
				AngularVelocity.Load(0.0f);
			}
			
			AngularVelocity.Clamp
			( 
				AngularVelocity, 
				Float3.Local(-FMath.ToRad(45.0f)),
				Float3.Local(FMath.ToRad(45.0f))
			);		
			
			final Float3 f3Rotation = Float3.Local().Mul
			(
				AngularVelocity, 
				fElapsedTime
			);

			final Rotation3 r3 = Rotation3.Local().Mul
			(
				Rotation3.Local().LoadRotationY( f3Rotation.Y),
				Rotation3.Local().LoadRotationX( f3Rotation.X)
			);
			r3.Mul( Rotation3.Local().LoadRotationZ( f3Rotation.Z), r3);

			Transform.XYZ.Mul( r3, Transform.XYZ );
		}
		
		
		
		float fVX = Float3.Dot(Velocity, Transform.XYZ.X);
		float fVY = Float3.Dot(Velocity, Transform.XYZ.Y);
		float fVZ = Float3.Dot(Velocity, Transform.XYZ.Z);

		if (buttonRight.IsHold())
		{
			//. 前進.
			fVZ += 15.0f * 60.0f * fElapsedTime;

			//fVX = BreakControl(fVX, 5.0f * 60.0f * fElapsedTime);
			//fVY = BreakControl(fVY, 5.0f * 60.0f * fElapsedTime);
			
			
		}
		
		if(buttonRight.IsDown())
		{
			//fVX = VelocityControl( fVX, -buttonRight.Velocity.X*1.0f, 5.0f*60.0f*fElapsedTime );
			//fVY = VelocityControl( fVY, -buttonRight.Velocity.Y*1.0f, 5.0f*60.0f*fElapsedTime );
			
			fVX += -buttonRight.Velocity.X*1.0f;
			fVY += -buttonRight.Velocity.Y*1.0f;
		}
		else 
		{
			fVX = BreakControl(fVX, 0.5f * 60.0f * fElapsedTime);
			fVY = BreakControl(fVY, 0.5f * 60.0f * fElapsedTime);
			fVZ = BreakControl(fVZ, 0.5f * 60.0f * fElapsedTime);
		}
		
		//. Local to World.
		Velocity.Mul(Float3.Local(fVX,fVY,fVZ),Transform.XYZ);
		//Float3x3.Mul(Velocity, new Float3(fVX, fVY, fVZ), new Float3x3(Transform));

		//. Clamp speed.
		if (Float3.Length(Velocity) > MaxSpeed)
		{
			Velocity.Mul( Velocity.Normalize(), MaxSpeed);
		}

		//. Update position.
		Transform.W.Mad( Velocity, fElapsedTime, Transform.W );
		
		//. Update HeadTransform.
		//Float4x4.Mul(WorldHeadTransform, LocalHeadTransform, Transform);
		WorldHeadTransform.Mul(LocalHeadTransform,Transform);
		{
			//Float4x4.Mul(WorldHeadCancelTransform, LocalHeadCancelTransform, Transform);
			//Transform.LoadIdentity();
			//LocalHeadCancelTransform.LoadIdentity();
			WorldHeadCancelTransform.Mul(LocalHeadCancelTransform, Transform);
			//final Float3 f3Up =  WorldHeadCancelTransform.XYZ.Y;
			//final Float3 f3Eye = WorldHeadCancelTransform.W;
			//final Float3 f3Forward = WorldHeadCancelTransform.XYZ.Z;
			
			final Float3 f3Up =  WorldHeadTransform.XYZ.Y;
			final Float3 f3Eye = WorldHeadTransform.W;
			final Float3 f3Forward = WorldHeadTransform.XYZ.Z;
			
			float fDistance = Float3.Length(f3Eye);
			if (fDistance < 100.0f)
			{
				fDistance = 100.0f;
			}
			final Float3 f3At = Float3.Mad(Float3.Local(), 
				f3Forward, fDistance, f3Eye);
			
			Float4x4.LookAt(HeadCancelView,f3Eye,f3At,f3Up);
			
			if(Float.isNaN(HeadCancelView.Values[0] ))
			{
				Float4x4.Identity(HeadCancelView);
			}
		}
		
		
		CardboardStarPlayerWeapon weapon = m_weaponDescs[m_eWeapon.ordinal()];

		if (buttonRight.IsTap())
		{	
			m_nbFireRemains = weapon.Bursts;
		}

		if (m_nbFireRemains > 0)
		{	
			if (weapon.Spawn(Transform,Velocity))
			//if (weapon.Spawn(Transform3.Local().LoadIdentity(),Float3.Local(0.0f,0.0f,0.0f)))
			{
				m_nbFireRemains--;
			}
		}

		for (CardboardStarPlayerWeapon itWeapon : m_weaponDescs)
		{
			itWeapon.Update(fElapsedTime);
		}

		for (CardboardStarGunShot shot : Shots)
		{
			shot.Update(fElapsedTime);
		}
		
	}
	
	float VelocityControl(float fTarget, float fVelocity, float fAccel)
	{
		if (fTarget > fTarget)
		{
			fTarget -= fAccel;

			if (fTarget < fVelocity)
			{
				fTarget = fVelocity;
			}
		}
		else if (fTarget < fVelocity)
		{
			fTarget += fAccel;

			if (fTarget > fVelocity)
			{
				fTarget = fVelocity;
			}
		}

		return fTarget;
	}
	
	float BreakControl(float fTarget, float fBreak)
	{
		if (fTarget > 0.0f)
		{
			fTarget -= fBreak;

			if (fTarget < 0.0f)
			{
				fTarget = 0.0f;
			}
		}
		else if (fTarget < 0.0f)
		{
			fTarget += fBreak;

			if (fTarget > 0.0f)
			{
				fTarget = 0.0f;
			}
		}

		return fTarget;
	}
	
	public void Render(BasicRender br)
	{
		
	}
	/*
	public final static  Float3 Slerp(final Float3 f3Dst, float fLerp, final Float3 f3From, final Float3 f3To )
	{
		float fDot = Float3.Dot(f3From,f3To);
		final Float3 f3Axis = Float3.Cross(Float3.Local(),f3To,f3From);
		if(Float3.Length(f3Axis)<=0.0f)
		{
			return Float3.Mov(f3Dst,f3To);
		}
		
		Float3.Normalize(f3Axis,f3Axis);
		
		float fRad = FMath.Acos(fDot);
		//Float4x
		
		return Float3x3.Mul(f3Dst,f3From,
		Float3x3.Rotation(Float3x3.Local(),
			Quaternion.LoadRotationAxis(Quaternion.Local(),f3Axis,fRad*fLerp)));
	}
	
	public final static Float4x4 NormalizeZYX(Float4x4 dst, Float4x4 src)
	{
		final Float3 f3Z = src.GetAxisZ(Float3.Local());
		//Float3.Normalize(f3Z,f3Z);

		final Float3 f3Y = src.GetAxisY(Float3.Local());
		//Float3.Normalize(f3Y, f3Y);

		final Float3 f3X = Float3.Cross(Float3.Local(), f3Y, f3Z);
		Float3.Normalize(f3Z,f3Z);

		Float3.Cross(f3Y,f3Z,f3X);
		
		dst.SetAxisX(f3X);
		dst.SetAxisY(f3Y);
		dst.SetAxisZ(f3Z);
		//dst.SetAxisW(src.GetAxisW(Float3.Local()));
		
		return dst;
	}
	
	public final static Float4x4 Slerp3x3ZYX
	(
		Float4x4 dst, 
		float fLerp, Float4x4 f4x4From, Float4x4 f4x4To
	)
	{
		final Float3 f3Z = Slerp(Float3.Local(), fLerp, 
								f4x4From.GetAxisZ(Float3.Local()),
								f4x4To.GetAxisZ(Float3.Local()));
		Float3.Normalize(f3Z,f3Z);

		final Float3 f3Y = Slerp(Float3.Local(), fLerp, 
								f4x4From.GetAxisY(Float3.Local()),
								f4x4To.GetAxisY(Float3.Local()));
		Float3.Normalize(f3Y, f3Y);
		
		final Float3 f3X = Float3.Cross(Float3.Local(), f3Y, f3Z);
		Float3.Normalize(f3X,f3X);
		
		NormalizeZYX(dst,
			Float4x4.Local(f3X,f3Y,f3Z,Float3.Local(0.0f,0.0f,0.0f)));
	
		return dst;
	}
	*/
}
