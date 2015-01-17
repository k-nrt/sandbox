package com.nrt.basic;

public class Rect //implements Cloneable
{
	public float X = 0.0f;
	public float Y = 0.0f;
	public float Width = 0.0f;
	public float Height = 0.0f;
	
	public Rect(){}
	public Rect(float x, float y, float w, float h)
	{
		X = x;
		Y = y;
		Width = w;
		Height = h;
	}

	public boolean IsIntersect(float x, float y)
	{
		if (X <= x && x < (X + Width) && Y <= y && y < (Y + Height))
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	public Rect Set( Rect s )
	{
		X = s.X;
		Y = s.Y;
		Width = s.Width;
		Height = s.Height;
		return this;
	}

	public Rect Set(float x, float y, float w, float h)
	{
		X = x;
		Y = y;
		Width = w;
		Height = h;
		return this;
	}
	/*
	public Rect clone()
	{
		Rect result;
		try
		{
			result = (Rect) super.clone();

			result.X = X;
			result.Y = Y;
			result.Width = Width;
			result.Height = Height;

			return result;	

		}
		catch ( CloneNotSupportedException e )
		{
			return null;
		}
	}
	*/
}
