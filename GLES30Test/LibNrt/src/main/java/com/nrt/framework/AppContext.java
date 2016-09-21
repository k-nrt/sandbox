package com.nrt.framework;

public interface AppContext
{
	public void OnCreate();
	public void OnSurfaceChanged(int width, int height);
	public void OnUpdate(float fElapsedTime);
	public void OnRender(float fElapsedTime);
}
