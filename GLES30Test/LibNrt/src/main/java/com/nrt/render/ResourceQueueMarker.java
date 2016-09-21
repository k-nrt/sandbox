package com.nrt.render;
import com.nrt.framework.*;

public class ResourceQueueMarker extends RenderResource
{
	public boolean Done = false;
	public String Name = null;
	public ResourceQueueMarker(String strName)
	{
		Name = strName;
	}

	public void Reset()
	{
		Done = false;
	}

	@Override
	public void Apply()
	{
		Done = true;

		Subsystem.Log.WriteLine( Name + " marker is done." );
	}
	
	public void WaitForDone()
	{
		while(Done==false)
		{
			Thread.yield();
		}
	}
}

