package com.nrt.framework;


import java.io.*;
import android.content.res.*;

import java.util.List;
import java.util.ArrayList;
import android.view.*;
import android.graphics.*;
import android.widget.TextView;
import android.os.Handler;

import java.util.Random;

import com.nrt.basic.*;
import com.nrt.render.*;
//import com.nrt.model.*;
//import com.nrt.font.*;
//import com.nrt.input.*;
//import com.nrt.helloworld.*;

import com.nrt.framework.AppContext;
import com.nrt.font.*;


public class Subsystem
{
	public static boolean IsInitialized = false;
	
	//. アプリで唯一じゃないとじゃないとマズいもの.
	public static ResourceQueue ResourceQueue = new ResourceQueue();
	public static TextViewLog Log = new TextViewLog();
	
	public static FrameTimer FrameTimer = null; 
	
	//. アクティビティから貰ってくるもの.
	public static ContentLoader ContentLoader = null;
	
	public static AppContext AppContext = null;
	
	static ThreadGroup s_threadGroup = new ThreadGroup( "app thread group" );
	static AppThread s_appThread = null;
	
	public static Object AppMutex = new Object();
	
	public static void Initialize( AssetManager assetManager, TextView textView, Handler handler, AppContext appContext )
	{
		ContentLoader = new ContentLoader( assetManager );
		Log.SetTextView( handler, textView );
		AppContext = appContext;
		
		s_appThread = new AppThread(s_threadGroup,"AppThread");
		s_appThread.start();
	}
	
	public static Render Render = null;
	public static MatrixCache MatrixCache = null;
	public static BasicRender BasicRender = null;
	public static BitmapFont BitmapFont = null;	
	public static ResourceLoader ResourceLoader = null;
	
	public static BitmapFontRender BitmapFontRender = null;
	
	static class AppThread extends Thread
	{		
		public AppThread( ThreadGroup threadGroup, String strName )
		{
			super( threadGroup, strName );
		}

		@Override
		public void run()
		{			
			FrameTimer = new FrameTimer();
			ResourceLoader = new ResourceLoader( 3, Log );
			
			Render = new Render();		
			MatrixCache = new MatrixCache();
			BasicRender = new BasicRender(Render, MatrixCache, 1024 * 1024 * 4, 1024 * 1024);
			BitmapFont = new BitmapFont( m_patterns );
			ResourceQueue.Add( MinimumMarker );
			IsInitialized = true;
			
			BitmapFontRender = new BitmapFontRender(ResourceQueue,BasicRender);
			BitmapFontRender.SetBitmapFont(ResourceQueue,BitmapFont);
			ResourceQueue.Add(SubsystemReadyMarker);
			
			while(!SubsystemReadyMarker.Done || !MinimumMarker.Done)
			{
				Thread.yield();
			}
			
			
			
			
			
			synchronized(AppMutex)
			{
				AppContext.OnCreate();
			}
			
			for(;;)
			{		
				synchronized(AppMutex)
				{
					AppContext.OnUpdate(FrameTimer.SafeFrameElapsedTime);
				}

				if( isInterrupted() )
				{
					break;
				}
				
				Thread.yield();
			}

			Subsystem.Log.WriteLine(this.getName() + " thread end.");
			super.run();
		}		
	}
	
	//public static FontRender DebugFont = null; 

	/*
	
	public static ModelRender ModelRender = null;

	public static FramePointer FramePointer = null;

	public static Rand Rand = new Rand();
	
	public static Debug Debug = null;
	*/

	
	public static ResourceQueueMarker MinimumMarker = new ResourceQueueMarker("MinimumSubsystem" );
	/*
	public static DelayResourceQueueMarker DebugFontReadyMarker = new DelayResourceQueueMarker("DebugFont");
	*/
	public static ResourceQueueMarker SubsystemReadyMarker = new ResourceQueueMarker("SubsystemReady");
	
	public static void OnCreate()
	{
		
		
		//FramePointer = new FramePointer();
		//Timer = new FrameTimer();		
		
		//ModelRender = new ModelRender(null, BasicRender, Loader);

		//DelayResourceQueue = new DelayResourceQueue();

		//BitmapFont = new BitmapFont( DelayResourceQueue, m_patterns, BasicRender );

		
		/*


		DelayResourceLoader.RegisterJob
		( 
			"SubSystem Model", DelayResourceQueue,
			new DelayResourceLoader.Job()
			{
				@Override public void OnLoadContent(DelayResourceQueue drq)
				{
					Debug = new Debug( Render, drq, MatrixCache );
					ModelRender = new ModelRender( drq, BasicRender, Loader);
					drq.Add( SubSystemReadyMarker );
				}
			}
		);

		DelayResourceLoader.RegisterJob
		(
			"SubSystem DebugFont", DelayResourceQueue,
			new DelayResourceLoader.Job()
			{
				@Override public void OnLoadContent(DelayResourceQueue drq)
				{
					Font fontDebug = new Font(drq, 1024, 16, 1, 1);		
					DebugFont = new FontRender(drq, BasicRender);
					DebugFont.SetFont(fontDebug);
					DebugFont.SetSize(16);
					DebugFont.SetFontColor(0xffffffff);
					DebugFont.SetBoarderColor(0xff000000);		
					drq.Add( DebugFontReadyMarker );		
				}
			}
		);
		*/
	}
	
	/*
	 public static void OnLoadContent( DelayResourceQueue drq, int iJob )
	 {
	 if( iJob == 0 )
	 {
	 Debug = new Debug( Render, drq, MatrixCache );
	 ModelRender = new ModelRender( drq, BasicRender, Loader);
	 drq.Add( SubSystemReadyMarker );
	 }
	 else
	 {
	 Font fontDebug = new Font(drq, 1024, 16, 1, 1);		
	 DebugFont = new FontRender(drq, BasicRender);
	 DebugFont.SetFont(fontDebug);
	 DebugFont.SetSize(16);
	 DebugFont.SetFontColor(0xffffffff);
	 DebugFont.SetBoarderColor(0xff000000);		
	 drq.Add( DebugFontReadyMarker );		
	 }
	 }
	 */

	private static final BitmapFont.Pattern[] m_patterns =
	{
		new BitmapFont.Pattern(
			"0123456789",
			new String[]
			{
				"-000----0----000---000-----0--00000--000--00000--000---000--",
				"0---0---0---0---0-0---0---00--0-----0---0-----0-0---0-0---0-",
				"0---0---0-------0-----0--0-0--0-----0--------0--0---0-0---0-",
				"0---0---0-----00----00--0--0--0000--0000----0----000---0000-",
				"0---0---0----0--------0-0--0------0-0---0---0---0---0-----0-",
				"0---0---0---0-----0---0-00000-0---0-0---0---0---0---0-----0-",
				"-000----0---00000--000-----0---000---000----0----000---000--",
				"------------------------------------------------------------",
			}
		),
		new BitmapFont.Pattern(
			"ABCDEFGHIJ",
			new String[]
			{
				"-ooo--0000---000--0000--00000-00000--000--0---0--000----000-",
				"o---o-0---0-0---0-0---0-0-----0-----0---0-0---0---0------0--",
				"o---o-0---0-0-----0---0-0-----0-----0-----0---0---0------0--",
				"ooooo-0000--0-----0---0-0000--0000--0--00-00000---0------0--",
				"o---o-0---0-0-----0---0-0-----0-----0---0-0---0---0------0--",
				"o---o-0---0-0---0-0---0-0-----0-----0---0-0---0---0---0--0--",
				"o---o-0000---000--0000--00000-0------000--0---0--000---00---",
				"------------------------------------------------------------",
			}
		),
		new BitmapFont.Pattern(
			"KLMNOPQRST",
			new String[]
			{
				"0---0-0-----0---0-0---0--000--0000---000--0000---000--00000-",
				"0--0--0-----00-00-00--0-0---0-0---0-0---0-0---0-0---0---0---",
				"0-0---0-----0-0-0-00--0-0---0-0---0-0---0-0---0-0-------0---",
				"00----0-----0---0-0-0-0-0---0-0000--0---0-0000---000----0---",
				"0-0---0-----0---0-0--00-0---0-0-----0-0-0-0-0-------0---0---",
				"0--0--0-----0---0-0--00-0---0-0-----0--0--0--0--0---0---0---",
				"0---0-00000-0---0-0---0--000--0------00-0-0---0--000----0---",
				"------------------------------------------------------------",
			}
		),
		new BitmapFont.Pattern(
			"UVWXYZ./",
			new String[]
			{
				"0---0-0---0-0---0-0---0-0---0-00000-----------0-",
				"0---0-0---0-0---0-0---0-0---0-----0----------0--",
				"0---0-0---0-0---0--0-0---0-0-----0-----------0--",
				"0---0-0---0-0---0---0-----0-----0-----------0---",
				"0---0-0---0-0-0-0--0-0----0----0-----------0----",
				"0---0--0-0--00-00-0---0---0---0------00----0----",
				"-000----0---0---0-0---0---0---00000--00---0-----",
				"------------------------------------------------",
			}
		),
	};

}










	

