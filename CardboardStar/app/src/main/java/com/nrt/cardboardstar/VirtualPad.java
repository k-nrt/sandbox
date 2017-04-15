package com.nrt.cardboardstar;

import com.nrt.input.*;
import com.nrt.ui.*;
import com.nrt.basic.*;
import com.nrt.render.*;
import android.text.style.*;
import android.widget.GridLayout.*;
import com.nrt.math.*;
import java.util.*;
import android.util.*;

public class VirtualPad
{	
	public enum Alignment
	{
		kLeftBottom,
		kLeftTop,
		kRightBottom,
		kRightTop
	};
	
	final static interface UiGridItem
	{
		public void SetGeometry
		(
			float pixX, float pixY,
			float pixEnterWidth, float pixEnterHeight,
			float pixLeaveWidth, float pixLeaveHeight
		);
	}
	
	final static class UiGridRoundButton extends UiRoundButton implements UiGridItem
	{
		public UiGridRoundButton(float x, float y, float enter)
		{
			super(x,y,enter);
		}
		
		@Override
		public void SetGeometry
		(
			float pixX, float pixY, 
			float pixEnterWidth, float pixEnterHeight,
			float pixLeaveWidth, float pixLeaveHeight
		)
		{
			float cx = pixX + pixEnterWidth*0.5f;
			float cy = pixY + pixEnterHeight*0.5f;
			float enter = FMath.Min(pixEnterWidth,pixEnterHeight)*0.5f;
			float leave = FMath.Min(pixLeaveWidth,pixLeaveHeight)*0.5f;
			
			SetGeometry(cx,cy, enter, leave);
		}	
	}
	
	final static class UiGridDirectionPad extends UiDirectionPad implements UiGridItem
	{
		public UiGridDirectionPad(float x, float y, float enter)
		{
			super(x,y,enter);
		}

		@Override
		public void SetGeometry
		(
			float pixX, float pixY, 
			float pixEnterWidth, float pixEnterHeight,
			float pixLeaveWidth, float pixLeaveHeight
		)
		{
			float cx = pixX + pixEnterWidth*0.5f;
			float cy = pixY + pixEnterHeight*0.5f;
			float enter = FMath.Min(pixEnterWidth,pixEnterHeight)*0.5f;
			float leave = FMath.Min(pixLeaveWidth,pixLeaveHeight)*0.5f;

			SetGeometry(cx,cy, enter, leave);
		}	
	}
	
	final static class UiGridRectButton extends UiRectButton implements UiGridItem
	{
		public UiGridRectButton(float x, float y, float width, float height)
		{
			super(x,y, width, height);
		}

		@Override
		public void SetGeometry
		(
			float pixX, float pixY, 
			float pixEnterWidth, float pixEnterHeight,
			float pixLeaveWidth, float pixLeaveHeight
		)
		{
			float ex = pixX;
			float ey = pixY;
			float ew = pixEnterWidth;
			float eh = pixEnterHeight;
			float dw = (pixLeaveWidth - pixEnterWidth)*0.5f;
			float dh = (pixLeaveHeight - pixEnterHeight)*0.5f;
			
			float lx = pixX - dw;
			float ly = pixY - dh;
			float lw = pixLeaveWidth;
			float lh = pixLeaveHeight;

			SetGeometry(ex,ey,ew,eh,lx,ly,lw,lh);
		}	
	}
	
	final static class UiItemPosition 
	{
		public UiGridItem Item = null;
		
		public Alignment Alignment = Alignment.kLeftBottom;
		public float X = 0.0f;
		public float Y = 0.0f;
		public float EnterWidth = 1.0f;
		public float EnterHeight = 1.0f;
		public float LeaveWidth = 1.0f;
		public float LeaveHeight = 1.0f;
		
		public UiItemPosition()
		{}
		
		public UiItemPosition(UiGridItem item, Alignment alignment, float x, float y, float enterSize, float leaveSize )
		{
			Item = item;
			Alignment = alignment;
			X = x;
			Y = y;
			EnterWidth = EnterHeight = enterSize;
			LeaveWidth = LeaveHeight = leaveSize;
		}
		
		public void UpdateLayout
		(
			float pixLeft, float pixRight, float pixTop, float pixBottom, float pixUnit
		)
		{
			switch(Alignment)
			{
				case kLeftBottom:
					Item.SetGeometry
					(
						pixLeft + X*pixUnit,
						pixBottom - (Y+1.0f)*pixUnit,
						EnterWidth*pixUnit, EnterHeight*pixUnit,
						LeaveWidth*pixUnit, LeaveHeight*pixUnit
					);
					break;
				
				case kRightBottom:
					Item.SetGeometry
					(
						pixRight - (X+1.0f)*pixUnit,
						pixBottom - (Y+1.0f)*pixUnit,
						EnterWidth*pixUnit, EnterHeight*pixUnit,
						LeaveWidth*pixUnit, LeaveHeight*pixUnit
					);
					break;
					
				case kLeftTop:
					Item.SetGeometry
					(
						pixLeft + X*pixUnit,
						pixTop + Y*pixUnit,
						EnterWidth*pixUnit, EnterHeight*pixUnit,
						LeaveWidth*pixUnit, LeaveHeight*pixUnit
					);
					break;

				case kRightTop:
					Item.SetGeometry
					(
						pixRight - (X+1.0f)*pixUnit,
						pixTop + Y*pixUnit,
						EnterWidth*pixUnit, EnterHeight*pixUnit,
						LeaveWidth*pixUnit, LeaveHeight*pixUnit
					);
					break;
			}
		}		
	};
	
	public final UiGridDirectionPad[] m_uiDPads = new UiGridDirectionPad[]
	{
		new UiGridDirectionPad(0,0,64.0f),
		new UiGridDirectionPad(0,0,64.0f)
	};
	
	public final UiGridItem[] m_uiButtons = new UiGridItem[]
	{
		new UiGridRoundButton(0,0,10.0f),
		new UiGridRoundButton(0,0,10.0f),
		new UiGridRoundButton(0,0,10.0f),
		new UiGridRoundButton(0,0,10.0f),
		new UiGridRoundButton(0,0,10.0f),
		new UiGridRoundButton(0,0,10.0f),
		new UiGridRoundButton(0,0,10.0f),
		new UiGridRoundButton(0,0,10.0f),
		new UiGridRectButton(0,0,10.0f,10.0f),
		new UiGridRectButton(0,0,10.0f,10.0f),
		new UiGridRectButton(0,0,10.0f,10.0f),
		new UiGridRectButton(0,0,10.0f,10.0f),
	};
	
	public enum EStick
	{
		Left,
		Right;
		
		int Index;
		EStick()
		{
			Index = ordinal();
		}
	};
	
	public enum EButton
	{		
		ButtonA,
		ButtonB,
		ButtonX,
		ButtonY,
		DPadLeft,
		DPadRight,
		DPadUp,
		DPadDown,
		BumperLeft,
		BumperRight,
		TriggerLeft,
		TriggerRight;
		
		int Index;
		EButton()

		{
			Index = ordinal();
		}
	};
	
	
	
	List<UiItemPosition> m_listItemPositions = new LinkedList<UiItemPosition>();//UiItemPosition[]
	
	
	public float m_fScreenWidth = 1280.0f;
	public float m_fScreenHeight = 800.0f;
	public float m_fScreenDpi = 189.0f;
	
	public float m_fFrameSafety = 0.9f;
	
	public VirtualPad()
	{
		
		m_listItemPositions.add(new UiItemPosition( m_uiDPads[EStick.Left.Index], Alignment.kLeftBottom, 2.0f, 2.0f, 2.0f, 8.0f ));
		m_listItemPositions.add(new UiItemPosition( m_uiDPads[EStick.Right.Index], Alignment.kRightBottom, 3.0f, 2.0f, 2.0f, 8.0f ));

		m_listItemPositions.add(new UiItemPosition(m_uiButtons[EButton.ButtonA.Index], Alignment.kRightBottom, 1.0f, 3.0f, 1.0f, 2.0f ));
		m_listItemPositions.add(new UiItemPosition(m_uiButtons[EButton.ButtonB.Index], Alignment.kRightBottom, 0.0f, 4.0f, 1.0f, 2.0f ));
		m_listItemPositions.add(new UiItemPosition(m_uiButtons[EButton.ButtonX.Index], Alignment.kRightBottom, 2.0f, 4.0f, 1.0f, 2.0f ));
		m_listItemPositions.add(new UiItemPosition(m_uiButtons[EButton.ButtonY.Index], Alignment.kRightBottom, 1.0f, 5.0f, 1.0f, 2.0f ));
		
		m_listItemPositions.add(new UiItemPosition(m_uiButtons[EButton.DPadLeft.Index], Alignment.kLeftBottom, 0.0f, 4.0f, 1.0f, 2.0f ));
		m_listItemPositions.add(new UiItemPosition(m_uiButtons[EButton.DPadRight.Index], Alignment.kLeftBottom, 2.0f, 4.0f, 1.0f, 2.0f ));
		m_listItemPositions.add(new UiItemPosition(m_uiButtons[EButton.DPadUp.Index], Alignment.kLeftBottom, 1.0f, 5.0f, 1.0f, 2.0f ));
		m_listItemPositions.add(new UiItemPosition(m_uiButtons[EButton.DPadDown.Index], Alignment.kLeftBottom, 1.0f, 3.0f, 1.0f, 2.0f ));

		m_listItemPositions.add(new UiItemPosition(m_uiButtons[EButton.BumperLeft.Index], Alignment.kLeftBottom, 1.0f, 6.0f, 1.0f, 1.0f ));
		m_listItemPositions.add(new UiItemPosition(m_uiButtons[EButton.BumperRight.Index], Alignment.kRightBottom, 1.0f, 6.0f, 1.0f, 1.0f ));
		m_listItemPositions.add(new UiItemPosition(m_uiButtons[EButton.TriggerLeft.Index], Alignment.kLeftBottom, 0.0f, 7.0f, 1.0f, 1.0f ));
		m_listItemPositions.add(new UiItemPosition(m_uiButtons[EButton.TriggerRight.Index], Alignment.kRightBottom, 0.0f, 7.0f, 1.0f, 1.0f ));
		
		
		UpdateLayout();
	}
	
	public void RegisterItems( UiForm form )
	{
		//form.Add(m_dpadLeft);
		//form.Add(m_dpadRight);
	
		for( UiItemPosition itemPosition : m_listItemPositions )
		{
			form.Add(itemPosition.Item);
		}
	}
	
	public void OnScreenSize(float fScreenWidth, float fScreenHeight, float fScreenDpi)
	{
		m_fScreenWidth = fScreenWidth;
		m_fScreenHeight = fScreenHeight;
		m_fScreenDpi = fScreenDpi;		
		UpdateLayout();
	}
	
	public void SetaFrameSafety( float frameSafety )
	{
		m_fFrameSafety = frameSafety;
		
	}
	
	private void UpdateLayout()
	{
		/*
		float mmDPadEnterSize = 20.0f;
		float mmDPadLeaveSize = 30.0f;
		float pixDPadEnterSize = ToPixel(mmDPadEnterSize);
		float pixDPadLeaveSize = ToPixel(mmDPadLeaveSize);
		*/
		float safetyWidth = m_fScreenWidth*m_fFrameSafety;
		float safetyHeight = m_fScreenHeight*m_fFrameSafety;
		
		float left = (m_fScreenWidth - safetyWidth)*0.5f;
		float right = m_fScreenWidth - left;
		float top = (m_fScreenHeight - safetyHeight)*0.5f;
		float bottom = m_fScreenHeight - top;
		
		/*
		m_dpadLeft.SetGeometry
		(
			left + pixDPadEnterSize*0.5f, 
			bottom - pixDPadEnterSize*0.5f,
			pixDPadEnterSize*0.5f,
			pixDPadLeaveSize*0.5f
		);
		
		m_dpadRight.SetGeometry
		(
			right - pixDPadEnterSize*0.5f, 
			bottom - pixDPadEnterSize*0.5f, 
			pixDPadEnterSize*0.5f,
			pixDPadLeaveSize*0.5f
		);
		*/
		float mmUnit = 8.0f;
		float pixUnit = ToPixel(mmUnit);
		
		for( UiItemPosition uiItemPosition : m_listItemPositions)
		{
			uiItemPosition.UpdateLayout(left,right,top,bottom, pixUnit);
		}
		
	}
	
	private float ToPixel( float mm )
	{
		float inch = mm/25.4f;
		return inch*m_fScreenDpi;
	}
	
	public UiButton GetButton(EButton button)

	{
		return (UiButton) m_uiButtons[button.Index];
	}
	
	public boolean IsPush(EButton button)
	{
		return GetButton(button).IsPush();
	}
	
	public boolean IsDown(EButton button)
	{
		return GetButton(button).IsDown();
		}
	
	public boolean IsRelease(EButton button)
	{
		return GetButton(button).IsRelease();
	}
	
	public boolean IsUp(EButton button)	
	{
		return !IsDown(button);
	}
	
	public UiGridDirectionPad GetStick(EStick stick)
	{
		return m_uiDPads[stick.Index];
	}
	
	public float GetX(EStick stick)
	{
		return GetStick(stick).Direction.X;
	}
	
	public float GetY(EStick stick)
	{
		return GetStick(stick).Direction.Y;
	}
	
	
	/*
	public void Update(FramePointer framePointer, float fElapsedTime)
	{
		m_form.Update(framePointer, fElapsedTime);
	}
	
	public void Render(BasicRender br)
	{
		m_form.Render(br);
	}
	*/
}

