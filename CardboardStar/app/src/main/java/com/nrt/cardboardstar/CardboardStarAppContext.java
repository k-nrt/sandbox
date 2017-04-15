package com.nrt.cardboardstar;

import java.io.*;
import android.opengl.GLES20;
import com.google.vrtoolkit.cardboard.*;
import com.nrt.framework.*;
import com.nrt.math.*;
import com.nrt.model.*;
import com.nrt.render.*;
import com.nrt.ui.*;
import com.nrt.basic.*;
import android.text.style.*;
import com.nrt.clipper.*;

import com.nrt.collision.*;

public class CardboardStarAppContext
implements AppContext, CardboardAppContext
{
	Float4x4 m_matrixHeadTransform = new Float4x4();
	Float4x4 m_matrixHeadView = new Float4x4();

	Float4x4 m_matrixEyeViewLeftRaw = new Float4x4();
	Float4x4 m_matrixEyeViewRightRaw = new Float4x4();
	
	Float4x4 m_matrixEyeViewLeft = new Float4x4();
	Float4x4 m_matrixEyeViewRight = new Float4x4();

	Float4x4 m_matrixEyeView = new Float4x4();
	Float4x4 m_matrixEyeProjection = new Float4x4();
	
	public RenderTexture m_textureShadow = null;
	public RenderBuffer m_bufferShadowDepth = null;
	public FrameBuffer m_frameBufferShadow = null;
	
	StarField m_starField = null;
	StarField.RenderContext m_starFieldRenderContextLeft = new StarField.RenderContext();
	StarField.RenderContext m_starFieldRenderContextRight = new StarField.RenderContext();

	UiForm m_form = new UiForm();
	CardboardStarUiButton m_buttonLeft = new CardboardStarUiButton(0, 0, 100, 100);
	CardboardStarUiButton m_buttonRight = new CardboardStarUiButton(0, 0, 100, 100);

	public boolean m_isInitialized = false;
	Model m_modelStarShip = null;
	public CollisionMesh m_collisionStarShip = null;
	
	DelayResourceQueueMarker ReadyMarker = new DelayResourceQueueMarker("GameContextReady");


	public CardboardStarPlayer m_player = new CardboardStarPlayer();

	public CardboardStarLightTailRender m_lightTailRender = null;
	
	public Frustum m_frustum = new Frustum();
	
	public CardboardStarStarShip[] m_starShips = null;
	
	public final int kMaxStarShips = 4;
	
	public CardboardStarParticle m_particle = null;
	public CardboardStarGunShot m_shotStarShips[] = null;
	
	public CardboardStarTargetList m_targetList = null;
	
	public VirtualPad m_virtualPad = new VirtualPad();
	
	public CardboardStarAppContext()
	{
		m_form.Add(m_buttonLeft);
		m_form.Add(m_buttonRight);
		
		m_virtualPad.RegisterItems( m_form );
	}



	@Override
	public void OnCreate(DelayResourceQueue drq)
	{
		if (m_isInitialized == false)
		{
			m_isInitialized = true;

			m_textureShadow = new RenderTexture( drq, RenderTextureFormat.RGBA, 512, 512 );
			m_bufferShadowDepth = new RenderBuffer( drq, RenderBufferFormat.DEPTH_COMPONENT16, 512, 512 );
			m_frameBufferShadow = new FrameBuffer( drq, m_textureShadow, m_bufferShadowDepth, null );
			

			m_modelStarShip = LoadModel(drq, "starship1.ssgmodel");
			m_collisionStarShip = LoadCollision("starship1.ssgcollision");
			
			
			m_starField = new StarField(drq);
			m_lightTailRender = new CardboardStarLightTailRender( drq, 16, 8);
			
			m_starShips = new CardboardStarStarShip[kMaxStarShips];
			for (int i = 0 ; i < m_starShips.length ; i++)
			{
				m_starShips[i] = new CardboardStarStarShip();
			}
			
			for( CardboardStarStarShip starShip : m_starShips )
			{
				RenderTexture textureShadow = new RenderTexture( drq, RenderTextureFormat.RGBA, 512, 512 );
				FrameBuffer frameBufferShadow = new FrameBuffer( drq, textureShadow, m_bufferShadowDepth, null );

				starShip.Model.SetShadowBuffer( frameBufferShadow );
			}
			
			m_particle = new CardboardStarParticle(drq);
			
			m_targetList = new CardboardStarTargetList();
			
			float[] arrayEnemyRadiusKernel = 
			{
				0.0f,1.0f,0.5f, 0.0f, //0.25f, 0.25f, 0.25f, 0.0f,
			};
			m_shotStarShips = new CardboardStarGunShot[32];
			for (int i = 0 ; i < m_shotStarShips.length ; i++)
			{
				m_shotStarShips[i] = new CardboardStarGunShot(10.0f, arrayEnemyRadiusKernel);
				m_shotStarShips[i].ColorIn.Set(2.0f, 1.0f, 1.5f, 1.0f);
				m_shotStarShips[i].ColorOut.Set(0.0f, 0.0f, 0.0f, 1.0f);
				m_shotStarShips[i].Power = 10.0f;
			}
			
			drq.Add(ReadyMarker);
		}
	}

	public Model LoadModel(DelayResourceQueue drq, String strPath)
	{
		InputStream stream = SubSystem.Loader.OpenAsset(strPath);
		Model.Loader loader = new Model.Loader(stream);
		Model model = loader.ReadModel(drq);
		loader.Close();	
		return model;
	}

	public CollisionMesh LoadCollision(String strPath)
	{
		InputStream stream = SubSystem.Loader.OpenAsset(strPath);
		CollisionLoader collisionLoader = new CollisionLoader(stream);
		CollisionMesh collisionMesh = collisionLoader.ReadMesh();
		collisionLoader.Close();

		return collisionMesh;
	}
	@Override
	public void OnSurfaceChanged(int width, int height)
	{
		// TODO: Implement this method
		//m_virtualPad.OnScreenSize( width, height, SubSystem.DisplayDPI );
	}

	public int m_windowWidth = 0;
	public int m_windowHeight = 0;
	@Override
	public void OnWindowSizeChanged(int width, int height, int oldWidth, int oldHeight)
	{
		m_windowWidth = width;
		m_windowHeight = height;

		m_buttonRight.Resize(width / 2.0f, 0, width / 2, height - 1);
		m_buttonLeft.Resize(0, 0, width / 2, height - 1);
		
		m_virtualPad.OnScreenSize( width, height, SubSystem.DisplayDPI );
	}




	boolean m_cardboardTrigger = false;
	@Override
	public void OnCardboardTrigger()
	{
		m_cardboardTrigger = true;
		// TODO: Implement this method
	}


	boolean m_isTriggered = false;

	float m_fRot = 0.0f;

	@Override
	public void OnUpdate()
	{
		if (m_isInitialized == false)
		{
			return;
		}
		

		if (!ReadyMarker.Done)
		{
			return;
		}
		
		float fElapsedTime = SubSystem.Timer.FrameElapsedTime;
		m_fRot += 10.0f * fElapsedTime;
		m_fRot %= 360.0f;

		m_isTriggered = m_cardboardTrigger;
		m_cardboardTrigger = false;
		// TODO: Implement this method
		m_form.Update(SubSystem.FramePointer, SubSystem.Timer.FrameElapsedTime);
		//m_virtualPad.Update(SubSystem.FramePointer, SubSystem.Timer.FrameElapsedTime);

		m_player.Update
		(
			SubSystem.Timer.SafeFrameElapsedTime, 
			m_matrixHeadTransform, 
			m_buttonRight,
			m_buttonLeft
		);

		m_targetList.Player.Update(m_player.Transform,m_player.Velocity,true);
		
		int nbIdleShips = 0;
		for (CardboardStarStarShip ship : m_starShips)
		{
			if (ship.IsIdle())
			{
				nbIdleShips++;
			}
		}


		for (int i = 0 ; i < m_starShips.length ; i++)
		{
			
			if (nbIdleShips >= m_starShips.length)
			{
				m_starShips[i].Spawn(
					Transform3.Local().LoadTranslation(
						2000.0f * i + SubSystem.Rand.Float(-1000.0f, 1000.0f),
						SubSystem.Rand.Float(-2000.0f, 2000.0f),
						SubSystem.Rand.Float(-2000.0f, 2000.0f)),
					m_modelStarShip, m_collisionStarShip);
			}


			m_starShips[i].Update(fElapsedTime, m_particle, m_targetList.Player, m_shotStarShips);
			
			
			//Float3 f3 = Float4x4.MulXYZ1( Float3.Local(), Float3.Local( 0.0f, 0.0f, 1000.0f ), m_starShips[i].Transform );

			
			//m_lightGlare.Update
			//(
			//	i,
			//	(m_starShips[i].IsBusy() || m_starShips[i].IsExplosion()),
			//	f3 
			//);
			
		}
		
		for(CardboardStarGunShot shot : m_shotStarShips)
		{
			shot.Update(fElapsedTime);
		}
		
	}

	@Override
	public void OnNewFrame(HeadTransform headTransform)
	{
		Float4x4.Identity(m_matrixHeadTransform);
		headTransform.getUpVector(m_matrixHeadTransform.Values, 4);
		headTransform.getRightVector(m_matrixHeadTransform.Values, 0);
		headTransform.getForwardVector(m_matrixHeadTransform.Values, 8);
		headTransform.getTranslation(m_matrixHeadTransform.Values, 12);

		//final Float4x4 matrix = Float4x4.Local();
		headTransform.getHeadView(m_matrixHeadView.Values, 0);

		//Float4x4.Invert(m_matrixHeadTransform, m_matrixHeadView);

		final Float3 f3Left = m_matrixHeadTransform.GetAxisX(Float3.Local());
		m_matrixHeadTransform.SetAxisX(Float3.Mul(f3Left, f3Left, Float3.Local(1.0f, -1.0f, 1.0f)));

		final Float3 f3Up = m_matrixHeadTransform.GetAxisY(Float3.Local());
		m_matrixHeadTransform.SetAxisY(Float3.Mul(f3Up, f3Up, Float3.Local(-1.0f, 1.0f, -1.0f)));

		final Float3 f3Forward = m_matrixHeadTransform.GetAxisZ(Float3.Local());
		m_matrixHeadTransform.SetAxisZ(Float3.Mul(f3Forward, f3Forward, Float3.Local(-1.0f, 1.0f, -1.0f)));

		final Float3 f3Position = m_matrixHeadTransform.GetAxisW(Float3.Local());
		m_matrixHeadTransform.SetAxisW(Float3.Mul(f3Position, f3Position, Float3.Local(1.0f, -1.0f, 1.0f)));
		
		/*
		 Float4x4.Mul
		 (
		 m_matrixHeadTransform,
		 Float4x4.Rotation(Float4x4.Local(),
		 m_matrixHeadTransform.GetAxisY(Float3.Local()),
		 FMath.PI
		 ),
		 m_matrixHeadTransform
		 );
		 */

		if (m_isInitialized)
		{
			//headTransform.getHeadView(m_matrixHeadTransform.Values, 0);
			Float4x4 matrixView = Float4x4.Local();
			headTransform.getHeadView(matrixView.Values, 0);
		}
	}

	@Override
	public void OnDrawEye(Eye eye)
	{
		float fElapsedTime = SubSystem.Timer.FrameElapsedTime;
		m_matrixEyeView.Set(eye.getEyeView(), 0);
		
		Float4x4.Mul(m_matrixEyeView, Float4x4.Invert(Float4x4.Local(),m_matrixHeadView),m_matrixEyeView);
		
		
		
		float fZNear = 100.0f;
		float fZFar = 10000.0f;
		m_matrixEyeProjection.Set(eye.getPerspective(fZNear, fZFar), 0);

		/*
		final Float3 f3Up =  m_player.HeadUp(Float3.Local());
		final Float3 f3Eye = m_player.HeadPosition(Float3.Local());

		float fDistance = Float3.Length(f3Eye);
		if (fDistance < 100.0f)
		{
			fDistance = 100.0f;
		}
		final Float3 f3At = Float3.Mad(Float3.Local(), m_player.HeadForward(Float3.Local()), fDistance, f3Eye);
		*/
		if (eye.getType() == Eye.Type.MONOCULAR)
		{
			m_matrixEyeViewLeftRaw.Set(m_matrixEyeView);
			
			Float4x4.Mul(m_matrixEyeViewLeft,
						 m_player.HeadCancelView,
						 m_matrixEyeView);

			if (m_isInitialized)
			{
				m_starFieldRenderContextLeft.Update(fElapsedTime, m_matrixEyeViewLeft.Values, m_matrixEyeProjection.Values);
			}
			RenderScene(EEye.Left);
		}
		else if (eye.getType() == Eye.Type.LEFT)
		{
			m_matrixEyeViewLeftRaw.Set(m_matrixEyeView);
			//m_matrixEyeViewLeft.Set(m_matrixEyeView);
			
			Float4x4.Mul(m_matrixEyeViewLeft,
						 m_player.HeadCancelView,
						 m_matrixEyeView);
			
			//m_matrixEyeViewLeft.Set(m_player.HeadCancelView);
			
			if (m_isInitialized)
			{
				m_starFieldRenderContextLeft.Update(fElapsedTime, m_matrixEyeViewLeft.Values, m_matrixEyeProjection.Values);
			}
			RenderScene(EEye.Left);
		}
		else if (eye.getType() == Eye.Type.RIGHT)
		{
			m_matrixEyeViewRightRaw.Set(m_matrixEyeView);
			//m_matrixEyeViewRight.Set(m_matrixEyeView);
			
			Float4x4.Mul(m_matrixEyeViewRight,
						 m_player.HeadCancelView,
						 m_matrixEyeView);
			
			//m_matrixEyeViewRight.Set(m_player.HeadCancelView);
			
			if (m_isInitialized)
			{
				m_starFieldRenderContextRight.Update(fElapsedTime, m_matrixEyeViewRight.Values, m_matrixEyeProjection.Values);
			}
			RenderScene(EEye.Right);
		}
	}

	@Override
	public void OnRender()
	{
		RenderScene(EEye.Mono);
	}

	enum EEye
	{
		Mono,
		Left,
		Right,
	};
	
	Box m_boxScissor = new Box();
	
	RasterizerState m_rasterizerStateBackFaceCulling = new RasterizerState
	(true, CullFace.Back, FrontFace.CCW);
	
	DepthStencilState m_depthStencilStateLessWriteEnable = new DepthStencilState
	(true,DepthFunc.Less,true);

	BlendState m_blendStateAdditive = new BlendState
	(true, BlendFunc.SrcAlpha, BlendFunc.One);
	
	BlendState m_blendStateTransparent = new BlendState
	(true, BlendFunc.SrcAlpha, BlendFunc.OneMinusSrcAlpha);
	
	private void RenderScene(EEye eye)
	{
		float fElapsedTime = SubSystem.Timer.FrameElapsedTime;

		GfxCommandContext gfxc = SubSystem.GfxCommandContext;
		
		if (m_isTriggered)
		{  
			//GLES20.glClearColor(1.0f, 0.0f, 0.1f, 1.f); 
			gfxc.SetClearColor(1.0f, 0.0f, 0.1f, 1.0f);
		}
		else
		{
			//GLES20.glClearColor(0.0f, 0.0f, 0.1f, 1.f); 

			gfxc.SetClearColor(0.0f, 0.0f, 0.1f, 1.0f);
		}
		
		
		//GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
		gfxc.Clear(EClearBuffer.ColorDepth);
		

		if (m_isInitialized == false)
		{
			return;
		}

		if (SubSystem.SubSystemReadyMarker.Done == false)
		{
			return;
		}
		int nSurfaceWidth = SubSystem.Render.ScanOutWidth;
		int nSurfaceHeight = SubSystem.Render.ScanOutHeight;

		BasicRender br = SubSystem.BasicRender;
		MatrixCache mc = SubSystem.MatrixCache;

		mc.SetProjection(m_matrixEyeProjection);
		
		if (eye == EEye.Left)
		{  
			mc.SetView(m_matrixEyeViewLeft);
			m_frustum.Update(m_matrixEyeProjection, m_matrixEyeViewLeft);
		}
		if (eye == EEye.Right)
		{  
			mc.SetView(m_matrixEyeViewRight);
			m_frustum.Update(m_matrixEyeProjection, m_matrixEyeViewRight);
		}

		mc.SetWorld(Float4x4.Local().SetIdentity());
		
		
		//m_frustum.Update(matrixProjection, matrixView);
		

		//GLES20.glEnable(GLES20.GL_BLEND);
		//GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE);

		gfxc.SetBlendState(m_blendStateAdditive);
		
		if (ReadyMarker.Done)
		{
			if (eye == EEye.Left)
			{  
				m_starField.Render(br, m_starFieldRenderContextLeft);
			}                       
			else if (eye == EEye.Right)
			{       
				m_starField.Render(br, m_starFieldRenderContextRight);
			}	   
		}

		//GLES20.glEnable(GLES20.GL_CULL_FACE);
		//GLES20.glEnable(GLES20.GL_DEPTH_TEST);
		//GLES20.glDisable(GLES20.GL_BLEND);

		gfxc.SetRasterizerState( m_rasterizerStateBackFaceCulling);
		gfxc.SetDepthStencilState( m_depthStencilStateLessWriteEnable );
		gfxc.SetBlendState( null );
		
		br.Begin( Primitive.TriangleFan, BasicRender.EShader.Color);
		br.SetColor(0x0000ffff);
		br.SetVertex(0, 0, 0);
		br.SetVertex(0, 5, 0);
		br.SetVertex(5, 5, 0);
		br.End();

		if (ReadyMarker.Done)
		{

			ModelRender mr = SubSystem.ModelRender;
			
			for(CardboardStarStarShip ship : m_starShips)
			{
				if(ship.IsBusy())
				{
					if( IsFrustumCulling
					(
						m_modelStarShip.Min, 
						m_modelStarShip.Max, 
						Float4x4.Local( ship.Transform )
					) == false )
					{
						mc.SetWorld(Float4x4.Local().Load(ship.Transform));
						mr.Draw(gfxc,mc,m_modelStarShip, ModelShaderSet.EShadow.Disable);
					}
				}
			}
			
			
			mc.SetWorld(Float4x4.Local().Identity());
			
			for (CardboardStarGunShot shot : m_player.Shots)
			{
				if (shot.IsBusy() || shot.IsRelease())
				{
					//br.SetColor(1.0f, 0.0f, 0.0f, 1.0f);
					//shot.LightTail.m_boxMinMax.Draw(br);
					if( IsFrustumCulling( shot.LightTail.m_boxMinMax ) == false )
					{					
						m_lightTailRender.Render(br, shot.LightTail, m_frustum,  shot.RenderRadius, shot.ColorIn, shot.ColorOut);
					}
				}
			}
			
			for (CardboardStarGunShot shot : m_shotStarShips)
			{
				if (shot.IsBusy() || shot.IsRelease())
				{
					//br.SetColor(1.0f, 1.0f, 0.0f, 1.0f);
					//shot.LightTail.m_boxMinMax.Draw(br);
					if( IsFrustumCulling( shot.LightTail.m_boxMinMax ) == false )
					{
						m_lightTailRender.Render(br, shot.LightTail, m_frustum,  shot.RenderRadius, shot.ColorIn, shot.ColorOut);
					}
				}
			}
		}

		mc.SetWorld(
			Float4x4.Mul(Float4x4.Local(),
			Float4x4.Translation(0.0f, 00.0f, 200.0f),
			Float4x4.Local().Load(m_player.WorldHeadCancelTransform)));
		
		br.SetColor(0x00ff00ff);
		br.Arc(0.0f, 0.0f, 20.0f, 16);
		br.Arc(0.0f, 10.0f, 5.0f, 16);
		br.Arc(10.0f, 0.0f, 5.0f, 16);

		mc.SetWorld(
			Float4x4.Mul(Float4x4.Local(),
						 Float4x4.Translation(0.0f, 00.0f, 200.0f),
						 Float4x4.Local().Load(m_player.Transform)));
		br.SetColor(0x0000ffff);
		br.Arc(0.0f, 0.0f, 20.0f, 16);
		br.Arc(0.0f, 10.0f, 5.0f, 16);
		br.Arc(10.0f, 0.0f, 5.0f, 16);
		
		mc.SetWorld(
			Float4x4.Mul(Float4x4.Local(),
						 Float4x4.Translation(0.0f, 00.0f, 200.0f),
						 Float4x4.Local().Load(m_player.WorldHeadTransform)));
		br.SetColor(0xff0000ff);
		br.Arc(0.0f, 0.0f, 20.0f, 16);
		br.Arc(0.0f, 10.0f, 5.0f, 16);
		br.Arc(10.0f, 0.0f, 5.0f, 16);
		
		//mc.SetWorld(m_matrixHeadView);
		//Float4x4 matrixOrtho = Float4x4.Local();
		//Matrix.orthoM(matrixOrtho.Values, 0, 0, nSurfaceWidth, nSurfaceHeight, 0, -1.0f, 1.0f);

		/*
		 mc.SetView(Float4x4.Identity(Float4x4.Local()));
		 mc.SetProjection(Float4x4.Ortho(Float4x4.Local(),
		 0, nSurfaceWidth, nSurfaceHeight, 0, -1.0f, 1.0f));
		 mc.SetWorld(Float4x4.Identity(Float4x4.Local()));
		 //mc.Update();

		 GLES20.glDisable(GLES20.GL_CULL_FACE);
		 GLES20.glDisable(GLES20.GL_DEPTH_TEST);

		 br.Begin(GLES20.GL_TRIANGLES, BasicRender.EShader.Color);

		 float fToCm = 1.0f / 2.54f;
		 br.SetColor(0x000055ff);
		 br.SetVertex(0, 0, 0);
		 br.SetVertex(0, 480.0f * fToCm, 0);
		 br.SetVertex(480.0f * fToCm, 480.0f * fToCm, 0);
		 br.End();
		 */
		/*
		 BitmapFont bf = SubSystem.BitmapFont;

		 bf.SetSize(16.0f);
		 bf.SetColor(0x00ff00ff);
		 float size = bf.m_fSize;
		 bf.Begin();
		 bf.Draw(0.0f, 0.0f, 0.0f, String.format("FPS:%3.3f", 1.0f / fElapsedTime));
		 long freeMem = Runtime.getRuntime().freeMemory();
		 long totalMem = Runtime.getRuntime().totalMemory();
		 bf.Draw(0.0f, size, 0.0f, String.format("MEM:%d/%d", (int) (totalMem - freeMem), (int) totalMem));
		 bf.Draw(0.0f, size * 2.0f, 0.0f, String.format("SCANOUT:%dx%d", 
		 nSurfaceWidth, nSurfaceHeight));

		 bf.Draw(0.0f, size * 3.0f, 0.0f, String.format("DPI %d", SubSystem.DisplayDPI));

		 //sfr.Draw( 0.0f, fr.m_fSize*2.0f, 0.0f, String.format( "%f %f %f", m_f3Euler.X, m_f3Euler.Y, m_f3Euler.Z ) );
		 bf.End();
		 */
		// TODO: Implement this method
	}
	
	private Box m_boxWorld = new Box();
	boolean IsFrustumCulling( final Float3 f3MinLocal, final Float3 f3MaxLocal, final Float4x4 transform )
	{
		m_boxWorld.Update( f3MinLocal, f3MaxLocal, transform );
		return IsFrustumCulling(m_boxWorld);
	}
	
	boolean IsFrustumCulling( final Box boxWorld )
	{
		boxWorld.ScissorBackFace( m_boxScissor, m_frustum.Surfaces );
		
		if( 0 < m_boxScissor.PolygonCount )
		{
			return false;
		}
		else
		{
			return true;
		}
	}

	@Override
	public void OnFinishFrame(Viewport viewPort)
	{
		if (SubSystem.MinimumMarker.Done)
		{






			BasicRender br = SubSystem.BasicRender;
			MatrixCache mc = br.GetMatrixCache();

			//GLES20.glClearColor(0.0f, 0.0f, 0.1f, 1.f); 
			//GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);


			//Float4x4 matrixOrtho = Float4x4.Local();
			//Matrix.orthoM(matrixOrtho.Values, 0, 0, viewPort.width, viewPort.height, 0, -1.0f, 1.0f);

			mc.SetView(Float4x4.Identity(Float4x4.Local()));
			mc.SetProjection(Float4x4.Ortho(Float4x4.Local(),
											0, viewPort.width, viewPort.height, 0, -1.0f, 1.0f
											));
			mc.SetWorld(Float4x4.Identity(Float4x4.Local()));

			if (m_isInitialized)
			{
				m_form.Render(br);
			}
		}
	}
}
