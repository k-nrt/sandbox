package com.nrt.inputtest;

import android.inputmethodservice.Keyboard;
import android.content.Context;

public class MyKeyboard extends Keyboard
{

	public MyKeyboard(Context context, int xmlLayoutResId)
	{
		super(context, xmlLayoutResId);
	}

	public MyKeyboard
	(
		Context context, int layoutTemplateResId, 
		CharSequence characters, int columns, int horizontalPadding
	)
	{
		super(context, layoutTemplateResId, characters, columns, horizontalPadding);
	}
}

