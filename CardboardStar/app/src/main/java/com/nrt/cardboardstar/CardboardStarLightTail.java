package com.nrt.cardboardstar;

import com.nrt.math.Float3;
import com.nrt.math.Float3x3;
import com.nrt.math.Float4x4;
import com.nrt.math.Transform3;

import com.nrt.clipper.Box;

public final class CardboardStarLightTail
{
	public Transform3[] Transforms = null;
	public float[] Radius = null;
	public int LastPosition = 0;
	public int Length = 0;

	public final Box m_boxMinMax = new Box();
	public final Float3 Min = new Float3();
	public final Float3 Max = new Float3();

	private int GetTransformIndex(int index)
	{
		return  (LastPosition - 1 - index + Transforms.length) % Transforms.length;
	}


	private final void UpdateBoundingBox()
	{
		int ii = GetTransformIndex(0);		
		float r = Radius[0];
		float x = Transforms[ii].W.X;
		float y = Transforms[ii].W.Y;
		float z = Transforms[ii].W.Z;
		Min.Load(x - r, y - r, z - r);
		Max.Load(x + r, y + r, z + r);

		for (int i = 1 ; i < Length ; i++)
		{			
			ii = GetTransformIndex(i);
			r = Radius[i];
			x = Transforms[ii].W.X;
			y = Transforms[ii].W.Y;
			z = Transforms[ii].W.Z;
			Min.Min(Min, Float3.Local(x - r, y - r, z - r));
			Max.Max(Max, Float3.Local(x + r, y + r, z + r));
		}

		m_boxMinMax.Update(Min, Max, Float4x4.Identity());
	}

	public final Transform3 LastTransform()
	{
		return Transforms[LastPosition];
	}

	public CardboardStarLightTail()
	{
	}

	public CardboardStarLightTail(int nbSegments, float fRadius, final Transform3 transformInitial)
	{
		Transforms = new Transform3[nbSegments];
		Radius = new float[nbSegments];

		for (int i = 0 ; i < nbSegments ; i++)
		{
			Transforms[i] = new Transform3();
			Transforms[i].Load(transformInitial);
			if (i <= 0 || (nbSegments - 1) <= i)
			{
				Radius[i] = 0.0f;
			}
			else
			{
				Radius[i] = fRadius;
			}
		}

		UpdateBoundingBox();
	}

	public CardboardStarLightTail(float[] arrayRadius, final Transform3 transformInitial)
	{
		int nbSegments = arrayRadius.length;
		Transforms = new Transform3[nbSegments];
		Radius = new float[nbSegments];

		for (int i = 0 ; i < nbSegments ; i++)
		{
			Transforms[i] = new Transform3();
			Transforms[i].Load(transformInitial);
			Radius[i] = arrayRadius[i];
		}

		UpdateBoundingBox();
	}

	public final void Reset(final Transform3 transformInitial)
	{
		LastPosition = 0;
		Length = 0;
		Update(transformInitial);
	}

	public final void Update(final Transform3 transformNew )
	{
		Transforms[LastPosition].Load(transformNew);
		LastPosition = (LastPosition + 1) % Transforms.length;
		if (Length < Transforms.length)
		{
			Length++;
		}
		UpdateBoundingBox();

	}

	public final void Update(final Float3 v3Position, final Float3 v3PrevPosition)
	{
		Float3 v3Forward = Float3.SubNormalize(Float3.Local(), v3Position, v3PrevPosition);
		Float3 v3Up = Float3.Normalize(Float3.Local(), GetTransform(0).XYZ.Y);
		Float3 v3Left = Float3.CrossNormalize(Float3.Local(), v3Up, v3Forward);
		Float3.Cross(v3Up, v3Forward, v3Left);

		Transform3 transform = Transform3.Local().Load(
			v3Left, v3Up, v3Forward,
			v3Position);

		//new Float4x4(v3Left.Xyz0(), v3Up.Xyz0(), v3Forward.Xyz0(), v3Position.Xyz1());
		Update(transform);



		//System.Error.WriteLine( String.format( "update %f %f %f", v3Position.X, v3Position.Y, v3Position.Z ) ); 
	}

	public final Transform3 GetTransform(int index)
	{
		int i = GetTransformIndex(index);
		return Transforms[i];
	}
}


