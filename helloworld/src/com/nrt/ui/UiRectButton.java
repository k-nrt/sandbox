package nrt.ui;

import nrt.basic.Rect;
import nrt.input.FramePointer;
import nrt.input.DevicePointer;

import nrt.render.BasicRender;

public class UiRectButton implements UiItem
{
	public final Rect Enter = new Rect();
	public final Rect Leave = new Rect();

	public final FramePointer.Pointer[] Pointers = new FramePointer.Pointer[DevicePointer.Pointers.length];
	public int m_nbPointers = 0;
	public int m_nbPrevPointers = 0;

	@Override
	public void Resize(Rect rectEnter, Rect rectLeave)
	{
		Enter.Set( rectEnter );
		Leave.Set( rectLeave );
		// TODO: Implement this method
	}

	@Override
	public Rect GetEnterRect()
	{
		return Enter;
	}

	@Override
	public Rect GetLeaveRect()
	{
		return Leave;
	}
	
	public UiRectButton(Rect rect)
	{
		this( rect, rect );
		//Enter = rect.clone();
		//Leave = rect.clone();
	}

	public UiRectButton(Rect rectEnter, Rect rectLeave)
	{
		SetGeometry( rectEnter, rectLeave );
		//Enter = rectEnter.clone();
		//Leave = rectLeave.clone();
	}

	public void SetGeometry(Rect rectEnter, Rect rectLeave)
	{
		Enter.Set( rectEnter );
		Leave.Set( rectLeave );
	}

	public void SetGeometry(Rect rect)
	{
		SetGeometry( rect, rect );
	}

	public boolean IsEnter(float x, float y)
	{
		return Enter.IsIntersect(x, y);
	}

	public boolean IsLeave(float x, float y)
	{
		return Leave.IsIntersect(x, y);
	}

	public boolean OnDown(int id, FramePointer.Pointer pointer)
	{
		Pointers[id] = pointer;
		return true;
	}

	public boolean OnEnter(int id, FramePointer.Pointer pointer)
	{
		Pointers[id] = pointer;
		return true;
	}

	public void OnMove(int id, FramePointer.Pointer pointer)
	{
		Pointers[id] = pointer;
	}
	public void OnLeave(int id, FramePointer.Pointer pointer)
	{
		Pointers[id] = null;
	}
	public void OnUp(int id, FramePointer.Pointer pointer)
	{
		Pointers[id] = null;
	}

	public void OnUpdate(float fElapsedTime)
	{
		m_nbPrevPointers = m_nbPointers;
		m_nbPointers = 0;
		for (int i = 0 ; i < Pointers.length ; i++)
		{
			if (Pointers[i] != null)
			{
				m_nbPointers++;
			}
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
		br.Rectangle(Enter);
	}

	public boolean IsPush()
	{
		if (m_nbPrevPointers <= 0 && m_nbPointers > 0)
		{
			return true;
		}
		else
		{
			return false;	
		}
	}

	public boolean IsDown()
	{
		return (m_nbPointers > 0 ? true : false);
	}
}