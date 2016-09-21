package com.nrt.cardboardstar;


import com.google.vrtoolkit.cardboard.CardboardView;
import com.google.vrtoolkit.cardboard.CardboardDeviceParams;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.nrt.framework.SubSystem;
import com.nrt.input.DevicePointer;

class CardboardStarView extends CardboardView
{
	public CardboardStarView(Context context)
	{
		super(context);
	}

	public CardboardStarView(Context context, AttributeSet attributeSet)
	{
		super(context,attributeSet);

		setVRModeEnabled(true);
		
		CardboardDeviceParams p = new CardboardDeviceParams(CardboardDeviceParams.cardboardV1DeviceParams());
		p.setScreenToLensDistance(72.5f/1000.0f);
		//p.setInterLensDistance(70.0f/1000.0f);
		
		updateCardboardDeviceParams(p);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh)
	{
		// TODO: Implement this method
		super.onSizeChanged(w, h, oldw, oldh);
		((CardboardAppContext) SubSystem.AppFrame.AppContext).OnWindowSizeChanged(w,h,oldw,oldh);
	}

	@Override
	public boolean onTouchEvent(MotionEvent e)
	{
		DevicePointer.OnTouchEvent(e);
		return true;//super.onTouchEvent(e);
	}
}

