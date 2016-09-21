package com.nrt.input;

import com.nrt.math.Float3;
import com.nrt.math.*;

public class PositionHistory
{
	public Float3[] Positions = null;
	public Float3[] Velocities = null;
	public float[] Speeds = null; 
	public float[] Rotations = null;
	public float[] RotationWeights = null;
	public float[] ElapsedTimes = null;
	public final Float3 AverageVelocity = new Float3();
	public float AverageSpeed = 0.0f;
	public float AverageRotation = 0.0f;
	public int Pos = 0;
	public int Histories = 0;

	public PositionHistory( int nbHistories )
	{
		Positions = new Float3[nbHistories];
		Velocities = new Float3[nbHistories];
		ElapsedTimes = new float[nbHistories];
		Speeds = new float[nbHistories];
		Rotations = new float[nbHistories];
		RotationWeights = new float[nbHistories];
		for( int i = 0 ; i < Positions.length ; i++ )
		{
			Positions[i] = new Float3();
			Velocities[i] = new Float3();
		}
	}

	public void Reset()
	{
		AverageVelocity.Load(0.0f);
		AverageSpeed = 0.0f;
		AverageRotation = 0.0f;
		Pos = 0;
		Histories = 0;
	}

	public void UpdatePosition( Float3 f3Position, float fElapsedTime )
	{
		if( Histories <= 0 )
		{
			Positions[Pos].Set( f3Position );
			ElapsedTimes[Pos] = 0.0f;
			Speeds[Pos] = 0.0f;
			Velocities[Pos].Load(0.0f);
			Rotations[Pos] = 0.0f;
			RotationWeights[Pos] = 0.0f;
		}
		else
		{
			int Prev = (Pos - 1 + Positions.length)%Positions.length;
			Positions[Pos].Set( f3Position );
			ElapsedTimes[Pos] = fElapsedTime;
			Velocities[Pos].Div( Float3.Local().Sub(Positions[Pos],Positions[Prev]), fElapsedTime);
			Speeds[Pos] = Float3.Distance( Positions[Pos], Positions[Prev] )/fElapsedTime;
			
			final Float3 f3V0 = Float3.Local().Load(Velocities[Pos]);
			float fLength0 = f3V0.Length();
			
			final Float3 f3V1 = Float3.Local().Load(Velocities[Prev]);
			float fLength1 = f3V1.Length();
			
			if( 20.0f < fLength0 && 20.0f < fLength1 )
			{
				f3V1.Normalize();
				f3V0.Normalize();
				final Float3 f3Axis = Float3.Local().CrossNormalize(f3V0,f3V1);
				float fDot = Float3.Dot(f3V0,f3V1);
			
				float w0 = FMath.Clamp((fLength0-20.0f)/80.0f,0.0f,1.0f);
				float w1 = FMath.Clamp((fLength1-20.0f)/80.0f,0.0f,1.0f);
				RotationWeights[Pos] = (w0+w1)/2.0f;
				if( f3Axis.Z < 0 )
				{
					Rotations[Pos] = -FMath.Acos( fDot );
				}
				else if( f3Axis.Z == 0.0f )
				{
					Rotations[Pos] = 0.0f;
				}
				else
				{
					Rotations[Pos] = FMath.Acos( fDot );
				}
			}
			else
			{
				Rotations[Pos] = 0.0f;
			}
		}

		if( Histories < Positions.length )
		{
			Histories++;
		}

		Pos = (Pos+1)%Positions.length;

		AverageSpeed = 0.0f;
		AverageVelocity.Load(0.0f);
		AverageRotation = 0.0f;
		
		float rw = 0.0f;
		for( int i = 0 ; i < Histories ; i++ )
		{
			AverageSpeed += Speeds[i];
			AverageRotation += Rotations[i]*RotationWeights[i];
			AverageVelocity.Add(AverageVelocity,Velocities[i]);
			rw += RotationWeights[i];
		}
		
		if( 0 < Histories )
		{
			AverageSpeed /= (float) Histories;
			AverageRotation /= rw;
			AverageVelocity.Div(AverageVelocity,(float) Histories);
		}
	}
	
	public Float3 GetPosition(int historyIndex )
	{
		historyIndex = (Positions.length*2 + Pos - 1 - (historyIndex%Positions.length))%Positions.length;
		return Positions[historyIndex];
	}
}
	
