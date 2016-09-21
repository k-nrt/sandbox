package com.nrt.framework;
import com.nrt.render.DelayResourceQueue;

public interface AppContext
{
	public void OnCreate(DelayResourceQueue drq );
	public void OnSurfaceChanged(int width, int height);
	public void OnUpdate();
	public void OnRender();
}

