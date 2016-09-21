package com.nrt.ui;

import com.nrt.math.Float3;
import com.nrt.math.Float4x4;
import com.nrt.math.Quaternion;
import com.nrt.math.FMath;

import com.nrt.input.FramePointer;

import com.nrt.render.BasicRender;
import com.nrt.render.MatrixCache;
import com.nrt.input.DevicePointer;

public class UiDirectionPad extends UiRoundButton
{
	/*
	public static class PointerInfo
	{
		public float Time = 0.0f;
		public final Float3 FirstPosition = new Float3();
		public final Float3 Position = new Float3();

		public FramePointer.Pointer Pointer = new FramePointer.Pointer();
		public boolean PrevOwner = false;

		public final Float3 Direction = new Float3(0.0f);

		public enum EStatus
		{
			Up,
			Tap,
			Hold,
			Slide,
			LeaveFlick,
			ReleaseFlick,
		};

		public EStatus Status = EStatus.Up;

		public float HoldTime = 0.0f;

		public void Update(float fElapsedTime, FramePointer.Pointer pointer, boolean owner)
		{	
			float fTapTime = 0.15f;
			float fHoldDistance = 16.0f;
			float fFlickSpeed = 4.0f;

			Pointer = pointer;
			if (owner)
			{
				Position.Set( pointer.Position );
				if (pointer.Push)
				{
					Time = 0.0f;
					HoldTime = 0.0f;
					Status = EStatus.Up;
					FirstPosition.Set( pointer.Position);
					Direction.Set( 0.0f );
				}
				else if (pointer.Down)
				{
					Time += fElapsedTime;

					float fDistance = Float3.Distance(FirstPosition, pointer.Position);

					if (fDistance > fHoldDistance)
					{
						Status = EStatus.Slide;
						HoldTime = 0.0f;
					}
					else
					{
						HoldTime += fElapsedTime;
					}

					if (Status == EStatus.Slide)
					{
						Float3.SubNormalize( Direction, pointer.Position, FirstPosition);
					}

					if (HoldTime > fTapTime)
					{
						Status = EStatus.Hold;
					}

				}
			} 
			else if (PrevOwner && pointer.Release)
			{
				//System.Error.WriteLine("release");
				float fDistance = Float3.Distance(FirstPosition, pointer.Position);
				float fSpeed = pointer.History.AverageSpeed;
				fSpeed /= 60.0f;

				if (Status == EStatus.Up && Time <= fTapTime && fDistance < fHoldDistance)
				{
					Status = EStatus.Tap;
					Time = 0.0f;
				}
				else if( fSpeed > fFlickSpeed )
				{
					Float3.SubNormalize( Direction, pointer.Position, FirstPosition);
					//System.Error.WriteLine( String.format("Release flick %f %f %f", fSpeed, Direction.X, Direction.Y) );
					Status = EStatus.ReleaseFlick;
					Time = 0.0f;
				}
				else
				{
					Status = EStatus.Up;
					Time = 0.0f;
				}
			}
			else if(PrevOwner && pointer.Down )
			{
				Float3.SubNormalize( Direction, pointer.Position, FirstPosition);	
				//System.Error.WriteLine( String.format("leave flick %f %f", Direction.X, Direction.Y) );
				Status = EStatus.LeaveFlick;
				Time = 0.0f;					
			}
			else
			{
				Status = EStatus.Up;
			}


			PrevOwner  = owner;
		}
	}
	*/

	public UiPointerGesture[] PointerGestures = new UiPointerGesture[DevicePointer.Pointers.length];

	public boolean Down = false;
	public boolean Tap = false;
	public boolean Hold = false;

	public final Float3 Direction = new Float3(0.0f);
	public boolean Slide = false;

	public boolean ReleaseFlick = false;
	public boolean LeaveFlick = false;
	
	public float Time = 0.0f;
	

	public UiDirectionPad(float x, float y, float radius)
	{
		super(x, y, radius);

		for (int i = 0 ; i < PointerGestures.length ; i++)
		{
			PointerGestures[i] = new UiPointerGesture();
		}
	}

	public final Quaternion Rotation = Quaternion.LoadIdentity( new Quaternion() );
	public void OnUpdate(float fElapsedTime)
	{
		super.OnUpdate(fElapsedTime);

		int nbDowns = 0;
		int nbTaps = 0;			
		int nbHolds = 0;
		int nbSlides = 0;
		int nbReleaseFlicks = 0;
		int nbLeaveFlicks = 0;
		final Float3 f3Direction = Float3.Local(0.0f);
		Quaternion.LoadIdentity( Rotation );
		for (int i = 0 ; i < PointerGestures.length ; i++)
		{
			final UiPointerGesture pointerGesture = PointerGestures[i];
			pointerGesture.Update(fElapsedTime, Pointers[i], Owners[i]);

			if (pointerGesture.Status == UiPointerGesture.EStatus.Tap)
			{
				nbTaps++;

			}

			if(Pointers[i].Down && Owners[i] )
			{
				nbDowns++;
			}

			if (pointerGesture.Status == UiPointerGesture.EStatus.Hold)
			{
				nbHolds++;
			}

			boolean isCalcDirection = false;
			if (pointerGesture.Status == UiPointerGesture.EStatus.Slide)
			{
				nbSlides++;
				isCalcDirection = true;
			}

			if (pointerGesture.Status == UiPointerGesture.EStatus.ReleaseFlick)
			{
				nbReleaseFlicks++;
				isCalcDirection = true;
			}

			if (pointerGesture.Status == UiPointerGesture.EStatus.LeaveFlick)
			{
				nbLeaveFlicks++;
				isCalcDirection = true;
			}

			if( isCalcDirection)
			{
				Float3.Add(f3Direction, f3Direction, pointerGesture.Direction);

				final Float3 v3Prev = Float3.Local().Set( 
					(pointerGesture.FirstPosition.X - EnterCenter.X) / EnterRadius,
					(pointerGesture.FirstPosition.Y - EnterCenter.Y) / EnterRadius,
					0.0f);//new Vector3( (PrevPoint.Position - Center)/Radius, 0.0f );

				final Float3 v3Current = Float3.Local().Set(
					(pointerGesture.Position.X - EnterCenter.X) / EnterRadius, 
					(pointerGesture.Position.Y - EnterCenter.Y) / EnterRadius,
					0.0f);//new Float3( (Point.Position - Center)/Radius, 0.0f );

				float len = (v3Prev.X * v3Prev.X + v3Prev.Y * v3Prev.Y);
				if( len > 1.0f )
				{
					Float3.Normalize( v3Prev, v3Prev );
				}
				else
				{
					v3Prev.Z = FMath.Sqrt(1.0f - len );
					Float3.Normalize( v3Prev, v3Prev);
				}

				len = (v3Current.X * v3Current.X + v3Current.Y * v3Current.Y);
				if( len > 1.0f )
				{
					Float3.Normalize( v3Current, v3Current );
				}
				else
				{
					v3Current.Z = FMath.Sqrt(1.0f - len);
					Float3.Normalize( v3Current, v3Current);
				}


				Quaternion.LoadRotation( Rotation, v3Current, v3Prev );
			}

		}

		Down = ((nbDowns > 0) ? true : false );
		Tap = ((nbTaps > 0) ? true : false);
		Hold = ((nbHolds > 0) ? true : false);

		Slide = ((nbSlides > 0) ? true : false);
		ReleaseFlick = ((nbReleaseFlicks > 0 && nbDowns <= 0) ? true : false);
		LeaveFlick = ((nbLeaveFlicks > 0 && nbDowns <= 0 ) ? true : false);
		Slide = ((nbSlides > 0) ? true : false);
		if (Slide || ReleaseFlick || LeaveFlick )
		{
			Float3.Normalize( Direction, f3Direction);
//				System.Error.WriteLine( String.format( "%f %f", Direction.X, Directinon.Y ) );
		}
		
		Time += fElapsedTime;

		//System.Error.WriteLine(Boolean.toString(Hold));
	}

	public void OnRender(BasicRender br)
	{
		UiTrackBall.Render(br,Time,EnterCenter,EnterRadius,Float4x4.Rotation( Float4x4.Local(), Rotation ));
	}
}
