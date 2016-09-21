package com.nrt.ui;

import com.nrt.math.Float3;
import com.nrt.input.FramePointer;

public class UiPointerGesture
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
			Position.Set(pointer.Position);
			if (pointer.Push)
			{
				Time = 0.0f;
				HoldTime = 0.0f;
				Status = EStatus.Up;
				FirstPosition.Set(pointer.Position);
				Direction.Set(0.0f);
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
					Float3.SubNormalize(Direction, pointer.Position, FirstPosition);
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
			else if (fSpeed > fFlickSpeed)
			{
				Float3.SubNormalize(Direction, pointer.Position, FirstPosition);
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
		else if (PrevOwner && pointer.Down)
		{
			Float3.SubNormalize(Direction, pointer.Position, FirstPosition);	
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

