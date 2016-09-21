package com.nrt.framework;


import android.content.res.*;
import android.os.*;
import android.widget.*;
import android.opengl.GLES20;

import com.nrt.basic.*;
import com.nrt.font.*;
import com.nrt.input.*;
import com.nrt.model.*;
import com.nrt.render.*;

import com.nrt.render.Debug;
import java.util.concurrent.atomic.*;

public class SubSystem
{
	//. アプリで唯一じゃないとじゃないとマズいもの.
	public static DelayResourceQueue DelayResourceQueue = new DelayResourceQueue();

	//. アクティビティから貰ってくるもの.
	public static Loader Loader = new Loader();
	public static TextViewLog Log = new TextViewLog();
	
	public static Render Render = new Render();
	public static GfxCommandContext GfxCommandContext = new GfxCommandContext();
	
	public static GfxCommandBuffer[] GfxCommandBuffers = new GfxCommandBuffer[12];
	public static FrameLinearVertexBuffer[] VertexBuffers = new FrameLinearVertexBuffer[12];
	public static FrameLinearIndexBuffer[]  IndexBuffers = new FrameLinearIndexBuffer[12];
	
	public static FrameTimer Timer = new FrameTimer(); 
	
	public static AppFrame AppFrame = new AppFrame();
	
	public static int DisplayDPI = 0;
	//public static AppContext AppContext = null;
	//public static AtomicLong UpdateFrame = new AtomicLong();
	//public static AtomicLong RenderFrame = new AtomicLong();
	
	//public static ThreadGroup s_threadGroup = new ThreadGroup("AppMain");
	//public static AppThread s_appThread = new AppThread(s_threadGroup,"AppMain");
	
	public static boolean IsInitialized = false;
	
	public static int FlippedFrames = 0;

	//public static Object AppMutex = new Object();
	//public static AtomicBoolean AppCreated = new AtomicBoolean();
	
	public static void Initialize
	(
		AssetManager assetManager,
		TextView textView, 
		int displayDPI,
		Handler handler,
		AppContext appContext
	)
	{
		Loader.SetAssetManager( assetManager );
		Log.SetTextView( handler, textView );
		
		DisplayDPI = displayDPI;
		if(IsInitialized==false)
		{
			AppFrame.StartContext( appContext );
			//s_appThread.start();		
		}
		
		for( int i = 0 ; i < GfxCommandBuffers.length ; i++ )
		{
			GfxCommandBuffers[i] = new GfxCommandBuffer(1024*32, 1024*32, 1024*128, 1024*32);
		}
		//GfxCommandContext.CommandBuffer = GfxCommandBuffers[0];
		
		IsInitialized = true;
	}
	
	public static GfxCommandBuffer GetCommandBuffer()
	{
		return GfxCommandBuffers[FlippedFrames%GfxCommandBuffers.length];
	}
	
	public static FrameLinearVertexBuffer GetVertexBuffer()
	{
		return VertexBuffers[FlippedFrames%VertexBuffers.length];
	}
	
	public static FrameLinearIndexBuffer GetIndexBuffer()
	{
		return IndexBuffers[FlippedFrames%IndexBuffers.length];
	}
	
	public static void FlipBuffers()
	{
		FlippedFrames++;
	}
	
	public static BitmapFont BitmapFont = null;	
	public static FontRender DebugFont = null; 

	public static BasicRender BasicRender = null;
	public static MatrixCache MatrixCache = null;

	public static ModelRender ModelRender = null;

	public static FramePointer FramePointer = null;

	public static Rand Rand = new Rand();
	
	//public static DebugLog Error = new DebugLog(45);

	public static Debug Debug = null;
	
	public static DelayResourceLoader DelayResourceLoader = null;


	public static DelayResourceQueueMarker MinimumMarker = new DelayResourceQueueMarker("MinimumSubSystem" );
	public static DelayResourceQueueMarker DebugFontReadyMarker = new DelayResourceQueueMarker("DebugFont");
	
	public static DelayResourceQueueMarker SubSystemReadyMarker = new DelayResourceQueueMarker("SubSystemReady");
	
	public static void OnCreate()
	{
		FramePointer = new FramePointer();
		
		MatrixCache = new MatrixCache();
		BasicRender = new BasicRender( DelayResourceQueue );//, 1024 * 1024 * 4, 1024 * 1024);
		BitmapFont = new BitmapFont( DelayResourceQueue, m_patterns);
	
		for( int i = 0 ; i < VertexBuffers.length ; i++ )
		{
			VertexBuffers[i] = new FrameLinearVertexBuffer(DelayResourceQueue, 1*1024*1024, 1*1024*1024);
		}

		for( int i = 0 ; i < IndexBuffers.length ; i++ )
		{
			IndexBuffers[i] = new FrameLinearIndexBuffer(DelayResourceQueue, 1*1024*1024, 1*1024*1024);
		}
		
		DelayResourceQueue.Add( MinimumMarker );
		
		DelayResourceLoader = new DelayResourceLoader( 3, Log );

		DelayResourceLoader.RegisterJob
		( 
			"SubSystem Model", DelayResourceQueue,
			new DelayResourceLoader.Job()
			{
				@Override public void OnLoadContent(DelayResourceQueue drq)
				{
					Debug = new Debug( drq );
					ModelRender = new ModelRender( drq, Loader);
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
	}
	
	
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
			"UVWXYZ./-+",
			new String[]
			{
				"0---0-0---0-0---0-0---0-0---0-00000-----------0-------------",
				"0---0-0---0-0---0-0---0-0---0-----0----------0----------0---",
				"0---0-0---0-0---0--0-0---0-0-----0-----------0----------0---",
				"0---0-0---0-0---0---0-----0-----0-----------0---00000-00000-",
				"0---0-0---0-0-0-0--0-0----0----0-----------0------------0---",
				"0---0--0-0--00-00-0---0---0---0------00----0------------0---",
				"-000----0---0---0-0---0---0---00000--00---0-----------------",
				"------------------------------------------------------------",
			}
		),
	};
}










	
