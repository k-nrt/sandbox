package com.nrt.render;

import android.opengl.GLES30;

import com.nrt.basic.DebugLog;
import android.app.*;
import com.nrt.framework.*;


public class Uniform extends RenderResource
{
	public Program Program = null;
	public String Name = null;

	public int Index = -1;

	public void Initialize( ResourceQueue queue, Program program, String strName )
	{
		Program = program;
		Name = strName;
		Index = -1;

		if( queue == null )
		{
			Apply();
		}
		else
		{
			queue.Add( this );
		}
	}

	@Override
	public void Apply()
	{
		if (Program.Name == 0)
		{
			return;
		}

		Index = GLES30.glGetUniformLocation(Program.Name, Name);
		if( Index < 0 )
		{
			Subsystem.Log.WriteLine( String.format( "uniform not found %d %s", Index, Name ));
		}
		else
		{
			Subsystem.Log.WriteLine( String.format( "apply uniform %d %s", Index, Name ));
		}
	}

	public Uniform()
	{}

	public Uniform( ResourceQueue queue, Program program, String name)
	{
		Initialize( queue, program, name );
	}
}

