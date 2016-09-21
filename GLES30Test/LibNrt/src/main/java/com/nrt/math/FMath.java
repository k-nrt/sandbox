package com.nrt.math;

import java.lang.Math;

public class FMath
{
	public static final float PI = (float) Math.PI;
	public static final float RD = (float) (Math.PI*2.0);

	public static final float ToRad( float deg ) { return (deg*PI)/180.0f; }

	public static final float Abs( float a ) { return ( (0.0f < a) ? a : -a );} 
	public static final float Pow( float x, float y ) { return (float) Math.pow( (double) x, (double) y ); }
	public static final float Cos( float rad ) { return (float) Math.cos( (double) rad ); }
	public static final float Sin( float rad ) { return (float) Math.sin( (double) rad ); }
	public static final float Acos( float c ) { return (float) Math.acos( (double) c ); }
	public static final float Asin( float s ) { return (float) Math.asin( (double) s ); }
	public static final float Atan2( float y, float x ) { return (float) Math.atan2( y, x ); }
	public static final float Sqrt(float a ) { return (float) Math.sqrt( (double) a ); }
	public static final float Floor( float a ) { return (float) Math.floor( (double) a); }

	public static final float Lerp( float f0, float f1, float fLerp )
	{
		return f0*(1.0f-fLerp)+f1*fLerp;
	}

	public static final float Min( float a, float b ) { return ( (a < b) ? a : b ); }
	public static final float Max( float a, float b ) { return ( (b < a) ? a : b ); }

	public static final float Clamp( float a, float fMin, float fMax )
	{
		return ( a < fMin ? fMin : ( a > fMax ? fMax : a ) );
	}

	public static final float Fraction( float a )
	{
		int i = (int) a;
		if( (float) i < a )
		{
			return a - (float)i;
		}
		else
		{
			return -((float)i) - a;
		}
	}
}

	

