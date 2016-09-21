package com.nrt.cardboardstar;

import com.google.vrtoolkit.cardboard.HeadTransform;
import com.google.vrtoolkit.cardboard.Eye;
import com.google.vrtoolkit.cardboard.Viewport;

interface CardboardAppContext
{
	public void OnNewFrame(HeadTransform headTransform);

	public void OnDrawEye(Eye eye);
	
	public void OnFinishFrame(Viewport viewPort);
	public void OnCardboardTrigger();
	
	public void OnWindowSizeChanged(int width, int height, int oldWidth, int oldHeight);
	
	
}
