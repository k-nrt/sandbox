package com.nrt.render;
import com.nrt.framework.*;
import android.util.Log;
public class DelayResourceQueueMarker extends RenderResource
{
	public boolean Done = false;
	public String Name = null;
	public DelayResourceQueueMarker(String strName)
	{
		Name = strName;
	}
	
	public void Reset()
	{
		Done = false;
		SubSystem.Log.WriteLine( Name + " reset marker.");
	}

	@Override
	public void Apply()
	{
		Done = true;
		
		SubSystem.Log.WriteLine( Name + " marker is done." );
	}
}
