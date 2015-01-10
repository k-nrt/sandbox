package nrt.render;
import nrt.framework.*;

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
	}

	@Override
	public void Apply()
	{
		Done = true;
		
		SubSystem.Log.WriteLine( Name + " marker is done." );
	}
}
