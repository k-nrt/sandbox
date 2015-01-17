package com.nrt.basic;

import java.nio.ByteOrder;
import com.nrt.math.Float4;

public class RgbaColor
{
	public static int FromRgba( Float4 rgba )
	{
		return FromRgba( rgba.X, rgba.Y, rgba.Z, rgba.W );
	}

	public static int FromRgba( float r, float g, float b, float a)
	{
		return FromRgba(
			(int) (r*255.0f) & 0xff, 
			(int) (g*255.0f) & 0xff, 
			(int) (b*255.0f) & 0xff, 
			(int) (a*255.0f) & 0xff );
	}
	
	public static int FromRgba(int r, int g, int b, int a)
	{
		if (ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN)
		{
			return a | b << 8 | g << 16 | r << 24;
		}
		else
		{
			return r | g << 8 | b << 16 | a << 24;
		}
	}

	public static int FromRgba(int rgba)
	{
		if (ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN)
		{
			return rgba;
		}
		else
		{
			return ((rgba >> 24) & 0xff)
				| ((rgba >> 8) & 0xff00 )
				| ((rgba << 8) & 0xff0000)
				| ((rgba << 24) & 0xff000000);
		}
	}

	public static int Black = 0xff000000;
	public static int Red = 0xff0000ff;
	public static int Green = 0xff00ff00;
	public static int Blue = 0xffff0000;
	public static int Yellow = 0xff00ffff;
	public static int Cyan = 0xffffff00;
	public static int Magenta = 0xffff00ff;
	public static int White = 0xffffffff;




}
