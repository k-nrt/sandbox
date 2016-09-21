package com.nrt.render;

import android.util.Log;
import android.opengl.GLES30;
import java.util.*;
import com.nrt.framework.*;

public class Shader extends RenderResource
{
	public static List<String> Error = new ArrayList<String>();

	public enum EType
	{
		Unknown(0),
		Vertex(GLES30.GL_VERTEX_SHADER),
		Fragment(GLES30.GL_FRAGMENT_SHADER);

		public int Value = 0;
		EType( int value )
		{
			Value = value;
		}
	}

	EType Type = EType.Unknown;
	String Source = null;

	public Shader()
	{}

	public void Initialize( ResourceQueue queue, EType type, String strSource ) // throws ThreadForceDestroyException
	{
		Type = type;
		Source = strSource;

		if( queue == null )
		{
			Subsystem.Log.WriteLine(strSource);
		}
		else
		{
			queue.Add( this );
		}
	}

	@Override
	public void Apply()
	{
		Subsystem.Log.WriteLine( "apply shader" );
		Name = GLES30.glCreateShader(Type.Value);
		if (Name == 0)
		{
			Subsystem.Log.WriteLine( "can not create shader" );
			return; 
		}
		//. シェーダーをコンパイル 
		GLES30.glShaderSource(Name, Source);
		GLES30.glCompileShader(Name);

		//. コンパイルが成功したか調べる
		int[] res = new int[1];
		GLES30.glGetShaderiv(Name, GLES30.GL_COMPILE_STATUS, res, 0);
		if (res[0] == 0)
		{
			//. 失敗してる
			Subsystem.Log.WriteLine( Type.name() );
			String[] logs = GLES30.glGetShaderInfoLog( Name ).split( "\n" );

			for( String log : logs )
			{
				Subsystem.Log.WriteLine( log );
			}

			Name = 0;
			return;
		}
	}

	public Shader( ResourceQueue queue, EType eType, String[] arrayLines ) // throws ThreadForceDestroyException
	{
		Initialize( queue, eType, ConbineSourceLines( arrayLines ) );
	}

	public static String ConbineSourceLines( String[] arrayLines )
	{
		String strSrc = new String();
		for (int i = 0 ; i < arrayLines.length ; i++)
		{
			strSrc += arrayLines[i];
			strSrc += "\n";
		}
		return strSrc;
	}
}

