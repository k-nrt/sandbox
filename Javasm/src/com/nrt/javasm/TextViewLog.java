package com.nrt.javasm;

import android.widget.*;
import android.os.*;

public class TextViewLog implements TextLog
{
	public TextView m_textView = null;
	public Handler m_handler = null;

	public TextViewLog(TextView textView)
	{
		m_textView = textView;
		m_handler = new Handler()
		{
			public void handleMessage( Message msg )
			{
				m_textView.append( (String) msg.obj );
			}	
		};
	}

	@Override
	public void AppendText( String strText )
	{
		Message msg = m_handler.obtainMessage();
		msg.obj = strText;

		m_handler.sendMessage( msg );
	}
}
