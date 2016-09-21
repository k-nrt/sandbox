package com.nrt.math;

import java.util.Random;

import com.nrt.math.FMath;

public final class Float3
{
	public float X = 0.0f;
	public float Y = 0.0f;
	public float Z = 0.0f;
	//public float W = 0.0f;

	public Float3(){}

	public Float3( float xyz ) { X=Y=Z=xyz; }
	public Float3( float x, float y, float z ) { X=x; Y=y; Z=z; }
	public Float3( Float3 v ) { X=v.X; Y=v.Y; Z=v.Z; }

	public final Float3 Set( float xyz ) { X=Y=Z=xyz; return this; }
	public final Float3 Set( float x, float y, float z ) { X=x; Y=y; Z=z; return this; }
	public final Float3 Set( final Float3 v ) { X=v.X; Y=v.Y; Z=v.Z; return this;}
	public final Float3 Set( final Float4 v ) { X=v.X; Y=v.Y; Z=v.Z; return this;}
	
	public final Float3 Load( float xyz ) { X=Y=Z=xyz; return this; }
	public final Float3 Load( float x, float y, float z ) { X=x; Y=y; Z=z; return this; }
	public final Float3 Load( final Float3 v ) { X=v.X; Y=v.Y; Z=v.Z; return this;}
	public final Float3 Load( final Float4 v ) { X=v.X; Y=v.Y; Z=v.Z; return this;}
	
	//public Float3 Yzx() { return new Float3( Y, Z, X ); }
	//public Float3 Zxy() { return new Float3( Z, X, Y ); }
	//public Float4 Xyz0() { return new Float4( X, Y, Z, 0.0f ); }
	//public Float4 Xyz0( Float4 dst ) { return dst.Set( X, Y, Z, 0.0f );}

	//public Float4 Xyz1() { return new Float4( X, Y, Z, 1.0f ); }
	//public Float4 Xyz1( Float4 dst ) { return dst.Set( X, Y, Z, 1.0f ); }

	public final boolean IsZero()
	{
		if( X == 0.0f && Y == 0.0f && Z == 0.0f )
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	public final boolean IsNaN()
	{
		if( Float.isNaN(X) || Float.isNaN(Y) || Float.isNaN(Z) )
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	//public Float3 Zero() { X=Y=Z=0.0f; return this; }

	public final Float3 Add( final Float3 a, final Float3 b ) { X=a.X+b.X; Y=a.Y+b.Y; Z=a.Z+b.Z; return this; }
	public final Float3 Sub( final Float3 a, final Float3 b ) { X=a.X-b.X; Y=a.Y-b.Y; Z=a.Z-b.Z; return this; }
	public final Float3 Mul( final Float3 a, final Float3 b ) { X=a.X*b.X; Y=a.Y*b.Y; Z=a.Z*b.Z; return this; }
	public final Float3 Div( final Float3 a, final Float3 b ) { X=a.X/b.X; Y=a.Y/b.Y; Z=a.Z/b.Z; return this; }

	public final Float3 Add( final Float3 a, float b ) { X=a.X+b; Y=a.Y+b; Z=a.Z+b; return this; }
	public final Float3 Sub( final Float3 a, float b ) { X=a.X-b; Y=a.Y-b; Z=a.Z-b; return this; }
	public final Float3 Mul( final Float3 a, float b ) { X=a.X*b; Y=a.Y*b; Z=a.Z*b; return this; }
	public final Float3 Div( final Float3 a, float b ) { X=a.X/b; Y=a.Y/b; Z=a.Z/b; return this; }
	
	public final Float3 Add( float a, final Float3 b ) { X=a+b.X; Y=a+b.Y; Z=a+b.Z; return this; }
	public final Float3 Sub( float a, final Float3 b ) { X=a-b.X; Y=a-b.Y; Z=a-b.Z; return this; }
	public final Float3 Mul( float a, final Float3 b ) { X=a*b.X; Y=a*b.Y; Z=a*b.Z; return this; }
	public final Float3 Div( float a, final Float3 b ) { X=a/b.X; Y=a/b.Y; Z=a/b.Z; return this; }

	public static final Float3 Add( Float3 dst, Float3 a, Float3 b ) { dst.X=a.X+b.X; dst.Y=a.Y+b.Y; dst.Z=a.Z+b.Z; return dst; }
	public static final Float3 Sub( Float3 dst, Float3 a, Float3 b ) { dst.X=a.X-b.X; dst.Y=a.Y-b.Y; dst.Z=a.Z-b.Z; return dst; }
	public static final Float3 Mul( Float3 dst, Float3 a, Float3 b ) { dst.X=a.X*b.X; dst.Y=a.Y*b.Y; dst.Z=a.Z*b.Z; return dst; }
	public static final Float3 Div( Float3 dst, Float3 a, Float3 b ) { dst.X=a.X/b.X; dst.Y=a.Y/b.Y; dst.Z=a.Z/b.Z; return dst; }

	public static final Float3 Add( Float3 dst, Float3 a, float b ) { dst.X=a.X+b; dst.Y=a.Y+b; dst.Z=a.Z+b; return dst; }
	public static final Float3 Sub( Float3 dst, Float3 a, float b ) { dst.X=a.X-b; dst.Y=a.Y-b; dst.Z=a.Z-b; return dst; }
	public static final Float3 Mul( Float3 dst, Float3 a, float b ) { dst.X=a.X*b; dst.Y=a.Y*b; dst.Z=a.Z*b; return dst; }
	public static final Float3 Div( Float3 dst, Float3 a, float b ) { dst.X=a.X/b; dst.Y=a.Y/b; dst.Z=a.Z/b; return dst; }

	public static final Float3 Add( Float3 dst, float a, Float3 b ) { dst.X=a+b.X; dst.Y=a+b.Y; dst.Z=a+b.Z; return dst; }
	public static final Float3 Sub( Float3 dst, float a, Float3 b ) { dst.X=a-b.X; dst.Y=a-b.Y; dst.Z=a-b.Z; return dst; }
	public static final Float3 Mul( Float3 dst, float a, Float3 b ) { dst.X=a*b.X; dst.Y=a*b.Y; dst.Z=a*b.Z; return dst; }
	public static final Float3 Div( Float3 dst, float a, Float3 b ) { dst.X=a/b.X; dst.Y=a/b.Y; dst.Z=a/b.Z; return dst; }

	public final Float3 Mad( final Float3 t, final Float3 m, final Float3 a ) { X=t.X * m.X + a.X; Y= t.Y * m.Y + a.Y; Z=t.Z * m.Z + a.Z; return this;}
	public final Float3 Mad( final Float3 t, float m, final Float3 a ) { X=t.X * m + a.X; Y= t.Y * m + a.Y; Z=t.Z * m + a.Z; return this; }

	public static final Float3 Mad( final Float3 dst, final Float3 t, final Float3 m, final Float3 a ) { dst.X=t.X*m.X+a.X; dst.Y=t.Y*m.Y+a.Y; dst.Z=t.Z*m.Z+a.Z; return dst; }
	public static final Float3 Mad( final Float3 dst, final Float3 t, float m, final Float3 a ) { dst.X=t.X*m+a.X; dst.Y=t.Y*m+a.Y; dst.Z=t.Z*m+a.Z; return dst; }

	public final Float3 Neg( Float3 a ) { X= -a.X; Y= -a.Y; Z= -a.Z; return this; }

	public static final Float3 Neg( Float3 dst, Float3 a ) { return dst.Set( -a.X, -a.Y, -a.Z ); }

	public static final Float3 Mov( Float3 dst, Float3 a )
	{
		return dst.Set( a.X, a.Y, a.Z );
	}
	
	public final Float3 Min( final Float3 a, final Float3 b )
	{
		X=( a.X < b.X ? a.X : b.X);
		Y=( a.Y < b.Y ? a.Y : b.Y);
		Z=( a.Z < b.Z ? a.Z : b.Z);
		return this;
	}

	public static final Float3 Min( final Float3 dst, final Float3 a, final Float3 b )
	{
		return dst.Set( 
			( a.X < b.X ? a.X : b.X),
			( a.Y < b.Y ? a.Y : b.Y),
			( a.Z < b.Z ? a.Z : b.Z)
		);
	}

	public final Float3 Max( final Float3 a, final Float3 b )
	{
		X=( a.X > b.X ? a.X : b.X);
		Y=( a.Y > b.Y ? a.Y : b.Y);
		Z=( a.Z > b.Z ? a.Z : b.Z);
		return this;
	}
	
	public static final Float3 Max( final Float3 dst, final Float3 a, final Float3 b )
	{
		return dst.Set( 
			( a.X > b.X ? a.X : b.X),
			( a.Y > b.Y ? a.Y : b.Y),
			( a.Z > b.Z ? a.Z : b.Z)
		);
	}

	public final Float3 Clamp( final Float3 a, final Float3 min, final Float3 max )
	{
		X = ( a.X < min.X ? min.X : ( a.X > max.X ? max.X : a.X ) );
		Y = ( a.Y < min.Y ? min.Y : ( a.Y > max.Y ? max.Y : a.Y ) );
		Z = ( a.Z < min.Z ? min.Z : ( a.Z > max.Z ? max.Z : a.Z ) );
		return this;
	}
	
	public static final Float3 Clamp( Float3 dst, Float3 a, Float3 min, Float3 max )
	{
		float x = ( a.X < min.X ? min.X : ( a.X > max.X ? max.X : a.X ) );
		float y = ( a.Y < min.Y ? min.Y : ( a.Y > max.Y ? max.Y : a.Y ) );
		float z = ( a.Z < min.Z ? min.Z : ( a.Z > max.Z ? max.Z : a.Z ) );
		return dst.Set( x, y, z );
	}

	public static final float Dot( Float3 a, Float3 b )
	{
		return a.X*b.X+a.Y*b.Y+a.Z*b.Z;
	}

	public final Float3 Cross(final Float3 a, final Float3 b) 
	{
		float fX = a.Y*b.Z - a.Z*b.Y;
		float fY = a.Z*b.X - a.X*b.Z;
		float fZ = a.X*b.Y - a.Y*b.X;

		X = fX;
		Y = fY;
		Z = fZ;
		return this;
	}
	
	public final Float3 CrossNormalize(final Float3 a, final Float3 b) 
	{
		return Cross( a, b ).Normalize();
	}
	
	public final Float3 SubNormalize( Float3 a, Float3 b ) 
	{
		return Sub( a, b ).Normalize();
	}

	public static final Float3 Cross( Float3 dst, Float3 a, Float3 b)
	{
		return dst.Set(
			a.Y*b.Z - a.Z*b.Y,
			a.Z*b.X - a.X*b.Z,
			a.X*b.Y - a.Y*b.X );
	}	

	public static final Float3 CrossNormalize(Float3 dst, Float3 a, Float3 b)
	{
		float x = a.Y*b.Z - a.Z*b.Y;
		float y = a.Z*b.X - a.X*b.Z;
		float z = a.X*b.Y - a.Y*b.X;

		float l = x*x + y*y + z*z;
		if( l > 0.0f )
		{
			float rcp = 1.0f/FMath.Sqrt( l );
			dst.X = x*rcp;
			dst.Y = y*rcp;
			dst.Z = z*rcp;
		}
		else
		{
			dst.X=dst.Y=dst.Z=0.0f;
		}
		return dst;
	}


	public static final Float3 SubNormalize( Float3 dst, Float3 a, Float3 b )
	{
		float x = a.X - b.X;
		float y = a.Y - b.Y;
		float z = a.Z - b.Z;

		float l = x*x + y*y + z*z;
		if( l > 0.0f )
		{
			float rcp = 1.0f/FMath.Sqrt( l );
			dst.X = x*rcp;
			dst.Y = y*rcp;
			dst.Z = z*rcp;
		}
		else
		{
			dst.X=dst.Y=dst.Z=0.0f;
		}
		return dst;
	}

	public static final float Distance( Float3 a, Float3 b )
	{
		float x = a.X - b.X;
		float y = a.Y - b.Y;
		float z = a.Z - b.Z;
		return FMath.Sqrt( x*x + y*y + z*z );
	}

	public final Float3 Lerp( float lerp, final Float3 v0, final Float3 v1 )
	{
		X=v0.X*(1.0f-lerp) + v1.X*lerp;
		Y=v0.Y*(1.0f-lerp) + v1.Y*lerp;
		Z=v0.Z*(1.0f-lerp) + v1.Z*lerp;
		return this;
	}
	
	public static final Float3 Lerp( Float3 dst, float lerp, Float3 v0, Float3 v1 )
	{
		return dst.Set(
			v0.X*(1.0f-lerp) + v1.X*lerp,
			v0.Y*(1.0f-lerp) + v1.Y*lerp,
			v0.Z*(1.0f-lerp) + v1.Z*lerp );
	}
	
	
	
	
	
	public final Float3 LoadSlerp(float lerp, final Float3 v0, final Float3 v1)
	{
		float angle = FMath.Acos(Float3.Dot(v0,v1));
		float s = FMath.Sin(angle);
		float l0 = FMath.Sin(angle*(1.0f-lerp));
		float l1 = FMath.Sin(angle*lerp);
		
		if( 0.0f < s)
		{
			X = (l0*v0.X + l1*v1.X)/s;
			Y = (l0*v0.Y + l1*v1.Y)/s;
			Z = (l0*v0.Z + l1*v1.Z)/s;
			
			LoadNormalize(this);			
		}
		else
		{
			X = v0.X;
			Y = v0.Y;
			Z = v0.Z;
		}
		
		return this;		
	}
	
	public final Float3 Mul( final Float3 l, final Rotation3 r)
	{
		return Load(
			l.X*r.X.X + l.Y*r.Y.X + l.Z*r.Z.X,
			l.X*r.X.Y + l.Y*r.Y.Y + l.Z*r.Z.Y,
			l.X*r.X.Z + l.Y*r.Y.Z + l.Z*r.Z.Z);		
	}
	
	public final Float3 Mul( final Float3 l, final Transform3 t)
	{
		return Load(
			l.X*t.XYZ.X.X + l.Y*t.XYZ.Y.X + l.Z*t.XYZ.Z.X + t.W.X,
			l.X*t.XYZ.X.Y + l.Y*t.XYZ.Y.Y + l.Z*t.XYZ.Z.Y + t.W.Y,
			l.X*t.XYZ.X.Z + l.Y*t.XYZ.Y.Z + l.Z*t.XYZ.Z.Z + t.W.Z);
	}
	
	public final float Length()
	{
		return FMath.Sqrt(X*X+Y*Y+Z*Z);
	}
	
	public final Float3 Normalize()
	{
		float length = Length();
		if( 0.0f < length )
		{
			X = X/length;
			Y = Y/length;
			Z = Z/length;
		}
		else
		{
			X = 0.0f;
			Y = 0.0f;
			Z = 0.0f;
		}

		return this;
	}
	
	
	public final Float3 LoadNormalize(final Float3 a)
	{
		float length = a.Length();
		if( 0.0f < length )
		{
			X = a.X/length;
			Y = a.Y/length;
			Z = a.Z/length;
		}
		else
		{
			X = 0.0f;
			Y = 0.0f;
			Z = 0.0f;
		}
		
		return this;
	}

	public static final float Length( Float3 a )
	{
		return FMath.Sqrt( a.X*a.X + a.Y*a.Y + a.Z*a.Z ); 
	}

	public static final float LengthSquared( Float3 a )
	{
		return a.X*a.X + a.Y*a.Y + a.Z*a.Z; 
	}

//	public static Float3 Normalize( Float3 a )
//	{
//		float l = (float) Math.sqrt((double)( a.X*a.X + a.Y*a.Y + a.Z*a.Z ));
//
//		if( l > 0.0f )
//		{
//			return new Float3( a.X/l, a.Y/l, a.Z/l );
//		}
//		else
//		{
//			return new Float3( 0.0f );
//		}
//	}

	public static final Float3 Normalize( Float3 dst, Float3 a )
	{
		float l = a.X*a.X + a.Y*a.Y + a.Z*a.Z;
		if( l > 0.0f )
		{
			float rcp = 1.0f/FMath.Sqrt( l );
			dst.X = a.X*rcp;
			dst.Y = a.Y*rcp;
			dst.Z = a.Z*rcp;
		}
		else
		{
			dst.X=dst.Y=dst.Z=0.0f;
		}
		return dst;
	}


	private static final Random s_rand = new Random();
	public static final Float3 Rand( float min, float max )
	{
		float r = max - min;

		return new Float3(
			s_rand.nextFloat()*r+min,
			s_rand.nextFloat()*r+min,
			s_rand.nextFloat()*r+min
		);
	}

	public static final Float3 Rand( Float3 dst, float min, float max )
	{
		float r = max - min;

		return dst.Set(
			s_rand.nextFloat()*r+min,
			s_rand.nextFloat()*r+min,
			s_rand.nextFloat()*r+min
		);
	}



	public static final class Float3List
	{
		public final Float3[] Values = new Float3[256];
		public Float3List()
		{
			for( int i = 0 ; i < Values.length ; i++ )
			{
				Values[i] = new Float3();
			}
		}

		private int m_iPos = 0;
		public final Float3 Get()
		{
			if( m_iPos < Values.length )
			{
				m_iPos++;
				return Values[m_iPos-1];
			}
			else
			{
				m_iPos = 1;
				return Values[0];
			}
		}
	}

	private static final Float3List m_locals = new Float3List();

	public static final Float3 Local() { return m_locals.Get(); }
	public static final Float3 Local( float xyz ) { return m_locals.Get().Set( xyz ); }
	public static final Float3 Local( float x, float y, float z ) { return m_locals.Get().Set( x, y, z ); }
	
	public static final Float3 Local( final Float4 f4 ){return m_locals.Get().Set(f4.X,f4.Y,f4.Z);}

}

