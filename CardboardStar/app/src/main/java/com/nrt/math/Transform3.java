package com.nrt.math;
import java.util.zip.*;

public final class Transform3
{
	public final Rotation3 XYZ = new Rotation3(); 
	public final Float3 W = new Float3(); 
	
	public final Transform3 Load3x4( float[] v, int o )
	{
		XYZ.Load3x3(v,o);
		W.X=v[o+9]; W.Y=v[o+10]; W.Z=v[o+11];
		return this;
	}

	public final Transform3 Load4x4( float[] v, int o )
	{
		XYZ.Load4x4(v,o);
		W.X=v[o+12]; W.Y=v[o+13]; W.Z=v[o+14];
		return this;
	}
	public final Transform3 Load( final Float4x4 a )
	{
		return Load4x4(a.Values,0);
	}
	
	
	public final Transform3 Load( final Transform3 t )
	{
		XYZ.Load(t.XYZ);
		W.Load(t.W);
		return this;
	}

	public final Transform3 Load( 
		float xx, float xy, float xz,
		float yx, float yy, float yz,
		float zx, float zy, float zz,
		float wx, float wy, float wz )
	{
		XYZ.Load(
			xx,xy,xz,
			yx,yy,yz,
			zx,zy,zz);
		W.Load(wx,wy,wz);
		return this;
	}	
	
	public final Transform3 Load(final Float3 x, final Float3 y, final Float3 z, final Float3 w)
	{
		XYZ.Load(x,y,z);
		W.Load(w);
		return this;
	}	
	
	public final Transform3 LoadTranslation(final Float3 a)
	{
		XYZ.LoadIdentity();
		W.Load(a);
		return this;
	}
	
	public final Transform3 LoadTranslation(float x, float y, float z)
	{
		XYZ.LoadIdentity();
		W.Load(x,y,z);
		return this;
	}
	
	public final Transform3 LoadIdentity()
	{
		XYZ.LoadIdentity();
		W.Load(0.0f,0.0f,0.0f);
		return this;
	}
	
	public final Transform3 LoadRotationX( float rad )
	{
		XYZ.LoadRotationX(rad);
		W.Load(0.0f,0.0f,0.0f);
		return this;
	}

	public final Transform3 LoadRotationY( float rad )
	{
		XYZ.LoadRotationY(rad);
		W.Load(0.0f,0.0f,0.0f);
		return this;
	}
	public final Transform3 LoadRotationZ( float rad )
	{
		XYZ.LoadRotationZ(rad);
		W.Load(0.0f,0.0f,0.0f);
		return this;
	}

	public final Transform3 LoadRotation( final Quaternion q )
	{
		XYZ.LoadRotation(q);
		W.Load(0.0f,0.0f,0.0f);
		return this;
	}

	public final Transform3 LoadRotation( final Float3 axis, float angle )
	{
		return LoadRotation
		(
			Quaternion.LoadRotationAxis( Quaternion.Local(), axis, angle ) 
		);
	}
	
	public final Transform3 LoadInverse( final Transform3 t)
	{
		Load(Float4x4.Invert(Float4x4.Local(), Float4x4.Local(t)));
		
		//XYZ.LoadTranspose(t.XYZ);
		//W.Mul(t.W,XYZ);
		
		return this;
	}
	
	public final Transform3 Mul( final Transform3 a, final Transform3 b)
	{
		float xx = a.XYZ.X.X*b.XYZ.X.X + a.XYZ.X.Y*b.XYZ.Y.X + a.XYZ.X.Z*b.XYZ.Z.X;
		float xy = a.XYZ.X.X*b.XYZ.X.Y + a.XYZ.X.Y*b.XYZ.Y.Y + a.XYZ.X.Z*b.XYZ.Z.Y;
		float xz = a.XYZ.X.X*b.XYZ.X.Z + a.XYZ.X.Y*b.XYZ.Y.Z + a.XYZ.X.Z*b.XYZ.Z.Z;
		
		float yx = a.XYZ.Y.X*b.XYZ.X.X + a.XYZ.Y.Y*b.XYZ.Y.X + a.XYZ.Y.Z*b.XYZ.Z.X;
		float yy = a.XYZ.Y.X*b.XYZ.X.Y + a.XYZ.Y.Y*b.XYZ.Y.Y + a.XYZ.Y.Z*b.XYZ.Z.Y;
		float yz = a.XYZ.Y.X*b.XYZ.X.Z + a.XYZ.Y.Y*b.XYZ.Y.Z + a.XYZ.Y.Z*b.XYZ.Z.Z;
		
		float zx = a.XYZ.Z.X*b.XYZ.X.X + a.XYZ.Z.Y*b.XYZ.Y.X + a.XYZ.Z.Z*b.XYZ.Z.X;
		float zy = a.XYZ.Z.X*b.XYZ.X.Y + a.XYZ.Z.Y*b.XYZ.Y.Y + a.XYZ.Z.Z*b.XYZ.Z.Y;
		float zz = a.XYZ.Z.X*b.XYZ.X.Z + a.XYZ.Z.Y*b.XYZ.Y.Z + a.XYZ.Z.Z*b.XYZ.Z.Z;
		
		float wx = a.W.X*b.XYZ.X.X + a.W.Y*b.XYZ.Y.X + a.W.Z*b.XYZ.Z.X + b.W.X;
		float wy = a.W.X*b.XYZ.X.Y + a.W.Y*b.XYZ.Y.Y + a.W.Z*b.XYZ.Z.Y + b.W.Y;
		float wz = a.W.X*b.XYZ.X.Z + a.W.Y*b.XYZ.Y.Z + a.W.Z*b.XYZ.Z.Z + b.W.Z;
		
		return Load(
			xx, xy, xz,
			yx, yy, yz,
			zx, zy, zz,
			wx, wy, wz );
	}
	
	private static LocalList<Transform3> m_locals = new LocalList<Transform3>( CreateLocals( 1024 ) );

	private static Transform3[] CreateLocals( int nbLocals )
	{
		Transform3[] result = new Transform3[nbLocals];
		for( int i = 0 ; i < result.length ; i++ )
		{
			result[i] = new Transform3();
		}

		return result;
	}

	public static Transform3 Local() { return m_locals.Local(); }	
}
