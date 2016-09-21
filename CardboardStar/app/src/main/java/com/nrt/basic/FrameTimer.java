package com.nrt.basic;

import java.lang.System;
import java.util.concurrent.atomic.AtomicLong;

public class FrameTimer
{
	public long StartTime = 0;

	public float MinFrameElapsedTime = 1.0f / 60.0f;
	public float MaxFrameElapsedTime = 10.0f / 60.0f;

	public double FrameTime = 0.0;
	public double PrevFrameTime = 0.0;

	public float FrameElapsedTime = 1.0f / 60.0f;
	public float SafeFrameElapsedTime = 1.0f / 60.0f;

	public AtomicLong Frame = new AtomicLong();


	public FrameTimer()
	{
		StartTime = java.lang.System.nanoTime();
	}
	/*
	 public void Start()
	 {

	 }

	 public void Stop()
	 {

	 }
	 */
	public double GetCurrentTime()
	{
		return Math.abs((double) (java.lang.System.nanoTime() - StartTime)) * 0.000000001;
	}

	public void Update()
	{
		Frame.incrementAndGet();

		PrevFrameTime = FrameTime;
		FrameTime = GetCurrentTime();

		FrameElapsedTime = (float) (FrameTime - PrevFrameTime);

		SafeFrameElapsedTime = FrameElapsedTime;
		if (SafeFrameElapsedTime < MinFrameElapsedTime)
		{
			SafeFrameElapsedTime = MinFrameElapsedTime;
		}

		if (SafeFrameElapsedTime > MaxFrameElapsedTime)
		{
			SafeFrameElapsedTime = MaxFrameElapsedTime;
		}
	}


}

