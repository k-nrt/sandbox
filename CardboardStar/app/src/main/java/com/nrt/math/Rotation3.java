package com.nrt.math;

public class Rotation3
{
	public final Float3 X = new Float3(); 
	public final Float3 Y = new Float3(); 
	public final Float3 Z = new Float3(); 

	public final Rotation3 Load3x3( float[] v3x3, int o )
	{
		X.X=v3x3[o+0]; X.Y=v3x3[o+1]; X.Z=v3x3[o+2];
		Y.X=v3x3[o+3]; Y.Y=v3x3[o+4]; Y.Z=v3x3[o+5];
		Z.X=v3x3[o+6]; Z.Y=v3x3[o+7]; Z.Z=v3x3[o+8];
		return this;
	}
	
	public final Rotation3 Load4x4( float[] v4x4, int o )
	{
		X.X=v4x4[o+0]; X.Y=v4x4[o+1]; X.Z=v4x4[o+2];
		Y.X=v4x4[o+4]; Y.Y=v4x4[o+5]; Y.Z=v4x4[o+6];
		Z.X=v4x4[o+8]; Z.Y=v4x4[o+9]; Z.Z=v4x4[o+10];
		return this;
	}

	public final Rotation3 Load( final Float4x4 a )
	{
		return Load4x4(a.Values,0);
	}

	public final Rotation3 Load( final Rotation3 t )
	{
		X.X=t.X.X; X.Y=t.X.Y; X.Z=t.X.Z;
		Y.X=t.Y.X; Y.Y=t.Y.Y; Y.Z=t.Y.Z;
		Z.X=t.Z.X; Z.Y=t.Z.Y; Z.Z=t.Z.Z;
		return this;
	}

	public final Rotation3 Load( 
		float xx, float xy, float xz,
		float yx, float yy, float yz,
		float zx, float zy, float zz )
	{
		X.X=xx; X.Y=xy; X.Z=xz;
		Y.X=yx; Y.Y=yy; Y.Z=yz;
		Z.X=zx; Z.Y=zy; Z.Z=zz;
		return this;
	}	

	public Rotation3 Load(final Float3 x, final Float3 y, final Float3 z)
	{
		X.Set(x);
		Y.Set(y);
		Z.Set(z);
		return this;
	}
	
	public final Rotation3 LoadIdentity()
	{
		X.Set(1.0f, 0.0f, 0.0f);
		Y.Set(0.0f, 1.0f, 0.0f);
		Z.Set(0.0f, 0.0f, 1.0f);
		return this;
	}
	
	public final Rotation3 LoadRotationX( float rad )
	{
		float s = FMath.Sin( rad );
		float c = FMath.Cos( rad );
		return Load(
			1.0f, 0.0f, 0.0f,
			0.0f, c, s,
			0.0f, -s, c);
	}

	public final Rotation3 LoadRotationY( float rad )
	{
		float s = FMath.Sin( rad );
		float c = FMath.Cos( rad );
		return Load(
			c, 0.0f, -s,
			0.0f, 1.0f, 0.0f,
			s, 0.0f, c);
	}
	public final Rotation3 LoadRotationZ( float rad )
	{
		float s = FMath.Sin( rad );
		float c = FMath.Cos( rad );
		return Load(
			c, s, 0.0f, 
			-s, c, 0.0f, 
			0.0f, 0.0f, 1.0f);
	}
	
	public final Rotation3 LoadRotation( Quaternion q )
	{
		float qw = q.W, qx = q.X, qy = q.Y, qz = q.Z;

		float x2 = 2.0f * qx * qx;
		float y2 = 2.0f * qy * qy;
		float z2 = 2.0f * qz * qz;

		float xy = 2.0f * qx * qy;
		float yz = 2.0f * qy * qz;
		float zx = 2.0f * qz * qx;

		float wx = 2.0f * qw * qx;
		float wy = 2.0f * qw * qy;
		float wz = 2.0f * qw * qz;

		return Load(
			1.0f - y2 - z2,
			xy - wz,
			zx + wy,

			xy + wz,
			1.0f - z2 - x2,
			yz - wx,

			zx - wy,
			yz + wx,
			1.0f - x2 - y2);
	}
	
	public final Rotation3 NormalizeZYX()
	{
		Float3.Normalize(Z,Z);
		Float3.Normalize(Y,Y);
		Float3.Cross(X, Y, Z);
		Float3.Normalize(X,X);
		Float3.Cross(Y,Z,X);
		return this;
	}
	
	public final Rotation3 SlerpZYX(float fLerp, final  Rotation3 rotationFrom, Rotation3 rotationTo)
	{
		Z.LoadSlerp(fLerp,rotationFrom.Z, rotationTo.Z);
		Y.LoadSlerp(fLerp,rotationFrom.Y, rotationTo.Y);
		return NormalizeZYX();
	}
	
	public final Rotation3 LoadRotation( final Float3 axis, float angle )
	{
		return LoadRotation
		(
			Quaternion.LoadRotationAxis( Quaternion.Local(), axis, angle ) 
		);
	}
	
	public final Rotation3 LoadTranspose(final Rotation3 a)
	{
		return Load(
			a.X.X, a.Y.X, a.Z.X,
			a.X.Y, a.Y.Y, a.Z.Y,
			a.X.Z, a.Y.Z, a.Z.Z);
	}
	
	
	public final Rotation3 Mul( final Rotation3 a, final Rotation3 b)
	{
		float xx = a.X.X*b.X.X + a.X.Y*b.Y.X + a.X.Z*b.Z.X;
		float xy = a.X.X*b.X.Y + a.X.Y*b.Y.Y + a.X.Z*b.Z.Y;
		float xz = a.X.X*b.X.Z + a.X.Y*b.Y.Z + a.X.Z*b.Z.Z;

		float yx = a.Y.X*b.X.X + a.Y.Y*b.Y.X + a.Y.Z*b.Z.X;
		float yy = a.Y.X*b.X.Y + a.Y.Y*b.Y.Y + a.Y.Z*b.Z.Y;
		float yz = a.Y.X*b.X.Z + a.Y.Y*b.Y.Z + a.Y.Z*b.Z.Z;

		float zx = a.Z.X*b.X.X + a.Z.Y*b.Y.X + a.Z.Z*b.Z.X;
		float zy = a.Z.X*b.X.Y + a.Z.Y*b.Y.Y + a.Z.Z*b.Z.Y;
		float zz = a.Z.X*b.X.Z + a.Z.Y*b.Y.Z + a.Z.Z*b.Z.Z;

		return Load(			
			xx,xy,xz,
			yx,yy,yz,
			zx,zy,zz);
	}
	
	private static final LocalList<Rotation3> m_locals = new LocalList<Rotation3>( CreateLocals( 256 ) );

	private static final Rotation3[] CreateLocals( int nbLocals )
	{
		Rotation3[] result = new Rotation3[nbLocals];
		for( int i = 0 ; i < result.length ; i++ )
		{
			result[i] = new Rotation3();
		}

		return result;
	}

	public static final Rotation3 Local() { return m_locals.Local(); }
}
