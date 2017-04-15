package com.nrt.ui;

import com.nrt.math.Float3;
import com.nrt.input.FramePointer;
import com.nrt.input.DevicePointer;
import com.nrt.basic.Rect;
import com.nrt.render.BasicRender;

public abstract class UiButton implements UiItem
{
	public FramePointer.Pointer[] Pointers = new FramePointer.Pointer[DevicePointer.Pointers.length];
	public boolean[] Owners = new boolean[DevicePointer.Pointers.length];

	public boolean EnableEnter = true;

	public enum EStatus
	{
		Down,
		Enter,
		Move,
		Leave,
		Up,
	}

	public EStatus[] Status = new EStatus[DevicePointer.Pointers.length];

	public int m_nbPointers = 0;
	public int m_nbPrevPointers = 0;
	
	public UiButton()
	{
		for (int i = 0 ; i < Pointers.length ; i++)
		{
			Pointers[i] = new FramePointer.Pointer();
			Owners[i] = false;
			Status[i] = EStatus.Up;
		}
	}
	
	@Override
	public boolean OnDown(int id, FramePointer.Pointer pointer)
	{
		Pointers[id] = pointer;
		Owners[id] = true;
		Status[id] = EStatus.Down;
		return true;
	}

	@Override
	public boolean OnEnter(int id, FramePointer.Pointer pointer)
	{
		if (EnableEnter)
		{
			Pointers[id] = pointer;
			Owners[id] = true;
			Status[id] = EStatus.Enter;
			return true;
		}
		else
		{
			return false;
		}
	}

	@Override
	public void OnMove(int id, FramePointer.Pointer pointer)
	{
		if (Owners[id] == true)
		{
			Pointers[id] = pointer;
			Owners[id] = true;
			Status[id] = EStatus.Move;
		}				
	}
	
	@Override
	public void OnLeave(int id, FramePointer.Pointer pointer)
	{
		if (Owners[id])
		{
			Pointers[id] = pointer;
			Owners[id] = false;
			Status[id] = EStatus.Leave;			
		}
	}
	
	@Override
	public void OnUp(int id, FramePointer.Pointer pointer)
	{
		if (Owners[id])
		{
			Pointers[id] = pointer;
			Owners[id] = false;
			Status[id] = EStatus.Up;
		}
	}

	@Override
	public void OnUpdate(float fElapsedTime)
	{
		m_nbPrevPointers = m_nbPointers;
		m_nbPointers = 0;
		for (int i = 0 ; i < Pointers.length ; i++)
		{
			if (Pointers[i].Down && Owners[i])
			{
				m_nbPointers++;
			}
		}
		//System.Error.WriteLine( Status[0].toString() );
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

	public boolean IsRelease()
	{
		if (m_nbPrevPointers > 0 && m_nbPointers <= 0)
		{
			return true;
		}
		else
		{
			return false;	
		}

	}

}
