package com.nrt.ui;

import com.nrt.math.Float3;
import com.nrt.input.FramePointer;
import com.nrt.input.DevicePointer;

import com.nrt.render.BasicRender;
import com.nrt.input.FramePointer;
import com.nrt.basic.*;

public class UiRoundButton extends UiButton
{
	public final Float3 EnterCenter = new Float3();
	public float EnterRadius = 0.0f;
	public final Float3 LeaveCenter = new Float3();
	public float LeaveRadius = 0.0f;
	
	
	public UiRoundButton(float x, float y, float radius)
	{
		this( Float3.Local( x, y, 0.0f ), radius, Float3.Local( x, y, 0.0f ), radius );
	}

	public UiRoundButton(Float3 f3EnterCenter, float fEnterRadius, Float3 f3LeaveRadius, float fLeaveRadius)
	{
		super();
		SetGeometry( f3EnterCenter, fEnterRadius, f3LeaveRadius, fLeaveRadius );
	}

	@Override
	public void Resize(Rect rectEnter, Rect rectLeave)
	{
		Float3 f3EnterCenter = Float3.Local( rectEnter.X + rectEnter.Width*0.5f, rectEnter.Y + rectEnter.Height*0.5f, 0.0f  );
		float fEnterRadius = ( rectEnter.Width < rectEnter.Height ? rectEnter.Width*0.5f : rectEnter.Height*0.5f );
		Float3 f3LeaveCenter = Float3.Local( rectLeave.X + rectLeave.Width*0.5f, rectLeave.X + rectLeave.Width*0.5f, 0.0f );
		float fLeaveRadius = ( rectLeave.Width < rectLeave.Height ? rectLeave.Width*0.5f : rectLeave.Height*0.5f );
		SetGeometry( f3EnterCenter, fEnterRadius, f3LeaveCenter, fLeaveRadius );
		
	}

	@Override
	public Rect GetEnterRect()
	{
		return new Rect(
			EnterCenter.X - EnterRadius, 
			EnterCenter.Y - EnterRadius, 
			EnterRadius*2.0f, EnterRadius*2.0f );
	}

	@Override
	public Rect GetLeaveRect()
	{
		return new Rect(
			LeaveCenter.X - LeaveRadius, 
			LeaveCenter.Y - LeaveRadius, 
			LeaveRadius*2.0f, LeaveRadius*2.0f );
	}

	public void SetGeometry(Float3 f3EnterCenter, float fEnterRadius, Float3 f3LeaveRadius, float fLeaveRadius)
	{
		EnterCenter.Set( f3EnterCenter );
		EnterRadius = fEnterRadius;
		LeaveCenter.Set( f3LeaveRadius );
		LeaveRadius = fLeaveRadius;

	}

	public void SetGeometry(float x, float y, float radius)
	{
		SetGeometry( Float3.Local( x, y, 0.0f ), radius, Float3.Local( x, y, 0.0f ), radius );
	}

	public void SetGeometry(float x, float y, float radiusEnter, float radiusLeave)
	{
		SetGeometry( Float3.Local( x, y, 0.0f ), radiusEnter, Float3.Local( x, y, 0.0f ), radiusLeave );
	}
	
	@Override
	public boolean IsEnter(float x, float y)
	{
		x -= EnterCenter.X;
		y -= EnterCenter.Y;
		float length = (float) Math.sqrt((double) (x * x + y * y));
		if (length < EnterRadius)
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	@Override
	public boolean IsLeave(float x, float y)
	{
		x -= LeaveCenter.X;
		y -= LeaveCenter.Y;
		float length = (float) Math.sqrt((double) (x * x + y * y));
		if (length < LeaveRadius)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	public void OnRender(BasicRender br)
	{
		if (m_nbPrevPointers < m_nbPointers)
		{
			br.SetColor(1, 0, 0, 1);
		}
		else if (m_nbPointers > 0)
		{
			br.SetColor(0, 0, 1, 1);			
		}
		else
		{
			br.SetColor(0, 1, 0, 1);
		}
		br.Arc(EnterCenter.X, EnterCenter.Y, EnterRadius, 16);
	}
}
