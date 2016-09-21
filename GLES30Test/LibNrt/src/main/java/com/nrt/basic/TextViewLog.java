package com.nrt.basic;
import java.util.concurrent.ConcurrentLinkedQueue;

public class TextViewLog
{
	public android.os.Handler m_handler = null;
	public android.widget.TextView m_textView = null;
	
	public ConcurrentLinkedQueue<String> m_queueStrings = new ConcurrentLinkedQueue<String>();

	public static class TextHandler implements Runnable
	{
		public String Text = null;
		public android.widget.TextView m_textView = null;

		public TextHandler( String strText, android.widget.TextView textView )
		{
			Text = strText;
			m_textView = textView;
		}

		public void run()
		{
			if( m_textView != null )
			{
				m_textView.append(Text);
			}
		}
	};
	
	public TextViewLog()
	{
		
	}

	public void SetTextView( android.os.Handler handler, android.widget.TextView textView )
	{
		m_handler = handler;
		m_textView = textView;

		Write("\n");
		
		while(!m_queueStrings.isEmpty())
		{
			String strText = m_queueStrings.poll();
			Write(strText);
		}
	}

	public void Write( String strText )
	{
		if( m_textView != null )
		{
			m_handler.post( new TextHandler( strText, m_textView ) );
		}
		else
		{
			m_queueStrings.add(strText);
		}
	}

	public void WriteLine( String strText )
	{
		if( m_textView != null )
		{
			m_handler.post( new TextHandler( strText + "\n", m_textView ) );
		}
		else
		{
			m_queueStrings.add(strText+"\n");
		}
	}
}


