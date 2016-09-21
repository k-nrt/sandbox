package com.nrt.math;

public class Intersection
{
	public final Float3 Position = new Float3(0.0f);
	public final Float3 Normal = new Float3(0.0f);
	public float Distance = -1.0f;

	public Intersection() {}
	public Intersection( final Float3 f3Position, final Float3 f3Normal, float fDistance )
	{
		Position.Load( f3Position );
		Normal.Load( f3Normal );
		Distance = fDistance;
	}
	
	public final void Clean()
	{
		Position.Load( 0.0f );
		Normal.Load( 0.0f );
		Distance = -1.0f;
	}
	
	public final Intersection Load( final Intersection intersection )
	{
		Position.Load( intersection.Position );
		Normal.Load( intersection.Normal );
		Distance = intersection.Distance;
		return this;
	}

	public final Intersection Load( final Float3 f3Position, final Float3 f3Normal, float fDistance )
	{
		Position.Load( f3Position );
		Normal.Load( f3Normal );
		Distance = fDistance;
		return this;
	}
	
	public final void LoadMin( final Float3 f3Position, final Float3 f3Normal, float fDistance )
	{
		if( fDistance < 0.0f )
		{
			return;
		}
		
		if( Distance < 0.0f || fDistance < Distance )
		{
			Position.Load( f3Position );
			Normal.Load( f3Normal );
			Distance = fDistance;
		}
	}

	public final void LoadMax( final Float3 f3Position, final Float3 f3Normal, float fDistance )
	{
		if( fDistance < 0.0f )
		{
			return;
		}

		if( Distance < 0.0f || fDistance > Distance )
		{
			Position.Load( f3Position );
			Normal.Load( f3Normal );
			Distance = fDistance;
		}
	}
	
	public static final Intersection Min( final Intersection r, final Intersection a, final Intersection b )
	{
		if( a.Distance < 0.0f )
		{
			return r.Load( b );
		}
		else if( b.Distance < 0.0f )
		{
			return r.Load( a );
		}
		else if( a.Distance < b.Distance )
		{
			return r.Load( a );
		}
		else
		{
			return r.Load( b );
		}
	}

	public static final Intersection Max( final Intersection r, final Intersection a, final Intersection b )
	{
		if( a.Distance < 0.0f )
		{
			return r.Load( b );
		}
		else if( b.Distance < 0.0f )
		{
			return r.Load( a );
		}
		else if( a.Distance > b.Distance )
		{
			return r.Load( a );
		}
		else
		{
			return r.Load( b );
		}
	}
	
	public static final float DistancePointToPoint( final Float3 a, final Float3 b )
	{
		float x = a.X - b.X;
		float y = a.Y - b.Y;
		float z = a.Z - b.Z;
		return FMath.Sqrt( x*x + y*y + z*z );
	}
	
	public static final float DistanceEdgeToPoint( final Float3 f3Start, final Float3 f3End, final Float3 f3Point, final Intersection intersection )
	{
		final Float3 f3Direction = Float3.Local();
		Float3.Sub( f3Direction, f3End, f3Start );
		float fLength = Float3.Length( f3Direction );

		if( fLength <= 0.0f )
		{
			return DistancePointToPoint( f3Start, f3Point );
		}
		else
		{
			Float3.Normalize( f3Direction, f3Direction );
			final Float3 f3StartToPoint = Float3.Sub( Float3.Local(), f3Point, f3Start );
			float fDot = Float3.Dot( f3StartToPoint, f3Direction );
			if( fDot < 0.0f )
			{
				intersection.Position.Load( f3Start );
				Float3.SubNormalize( intersection.Normal, f3Point, f3Start );
				intersection.Distance = DistancePointToPoint( f3Start, f3Point );
				return intersection.Distance;
			}
			else if( fDot > fLength )
			{
				intersection.Position.Load( f3End );
				Float3.SubNormalize( intersection.Normal, f3Point, f3End );
				intersection.Distance = DistancePointToPoint( f3End, f3Point );
				return intersection.Distance;
			}
			else
			{
				float a = DistancePointToPoint( f3Point, f3Start );
				float fDistance = FMath.Sqrt( a*a - fDot*fDot );
				Float3.Mad( intersection.Position, f3Direction, fDot, f3Start );
				Float3.SubNormalize( intersection.Normal, f3Point, intersection.Position );
				intersection.Distance = fDistance;

				//System.Error.WriteLine( "d" );

				return fDistance;
			}
		}
	}
	
	private static LocalList<Intersection> m_locals = new LocalList<Intersection>( CreateLocals( 256 ) );

	private static Intersection[] CreateLocals( int nbLocals )
	{
		Intersection[] result = new Intersection[nbLocals];
		for( int i = 0 ; i < result.length ; i++ )
		{
			result[i] = new Intersection();
		}

		return result;
	}

	public static Intersection Local() { return m_locals.Local(); }	
}

