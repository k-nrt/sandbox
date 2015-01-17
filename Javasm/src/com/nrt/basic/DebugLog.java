package com.nrt.basic;

import java.util.*;


public class DebugLog
{
	public static DebugLog Error = new DebugLog( 45 );

	public String[] Buffers = null;
	public int Position = 0;
	public int RenderPosition = 0;
	
	public class Log
	{
		public int X = 0;
		public int Y = 0;
		public String Text = "";
		
		public Log() {}
		public Log( int x, int y, String strText )
		{
			X = x;
			Y = y;
			Text = strText;
		}
	}
	
	public List<Log> m_listLogs = new ArrayList();

	public DebugLog(int nBufferSize)
	{
		Buffers = new String[nBufferSize + 1];
		for (int i = 0 ; i < Buffers.length ; i++)
		{
			Buffers[i] = "";
		}		

		RenderPosition = -Buffers.length + 1;
	}

	public void Write(String strTarget)
	{
		Buffers[Position] += strTarget;
	}

	public void WriteLine(String strLog)
	{
		Buffers[Position] += strLog;
		NextLine();
	}

	public void WriteLines(List<String> listLogs)
	{
		for (int i = 0 ; i < listLogs.size() ; i++)
		{
			WriteLine(listLogs.get(i));
		}
	}

	public void Write( int x, int y, String strText )
	{
		m_listLogs.add( new Log( x, y, strText ) );
	}
	
	/*
	public void Render(FontRender fr)
	{
		fr.Begin();
		Float3 f3Position = new Float3(0.0f);
		for (int i = 0 ; i < Buffers.length - 1 ; i++)
		{
			int rp = (RenderPosition < 0 ? 0 : RenderPosition);
			int ii = (rp + i) % Buffers.length;

			fr.Draw(f3Position, Buffers[ii]);
			f3Position.Y += fr.m_font.m_nFontSize;
		}
		fr.End();
	}	
	*/

//	boolean m_isScroll = false;

	public void NextLine()
	{
		Position++;
		if (Position >= Buffers.length)
		{
			Position = 0;
		}
		Buffers[Position] = "";

		RenderPosition++;
		if (RenderPosition >= Buffers.length)
		{
			RenderPosition = 0;
		}
	}
}
