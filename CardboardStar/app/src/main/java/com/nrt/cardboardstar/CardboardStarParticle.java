package com.nrt.cardboardstar;
import android.opengl.*;

import com.nrt.render.*;
import com.nrt.math.Float3;
import com.nrt.math.Float4x4;
import com.nrt.framework.*;


public class CardboardStarParticle
{
	CardboardStarSimpleParticle m_particles0 = null;
	CardboardStarSimpleParticle m_particles1 = null;
	StaticTexture m_texture = null;
	
	BlendState m_blendStateAdd = null;
	BlendState m_blendStateTransparent = null;
	DepthStencilState m_depthStateMaskDisable = null;
	//DepthStencilState m_depthStateDef = null;
	
	public CardboardStarParticle( DelayResourceQueue drq )
	//throws ThreadForceDestroyException
	{
		//SubSystem.Loader.LoadPng( "smokes.png" );

		m_texture = new StaticTexture( drq, SubSystem.Loader.LoadPng( "smokes.png" ), true );
		//m_texture = new StaticTexture();
		//GameMain.PngTextureLoader l = new GameMain.PngTextureLoader(m_texture,"smokes.png");
		//SubSystem.Render.DelayResourceQueue.Add( SubSystem.Loader.LoadPng( "smokes.png" ) );
		//StaticTexture.Create( m_texture, l.m_rawImage );
		//StaticTexture.Create( m_texture, SubSystem.Loader.LoadPng( "smokes.png" ) );
		//SubSystem.Render.DelayResourceQueue.Add( new GameMain.PngTextureLoader(m_texture,"smokes.png"));

		m_particles0 = new CardboardStarSimpleParticle( drq, SubSystem.BasicRender, 128, m_texture, 4, 4 );
		m_particles1 = new CardboardStarSimpleParticle( drq, SubSystem.BasicRender, 128, m_texture, 4, 4 );
		
		m_blendStateAdd = new BlendState( true, BlendFunc.SrcAlpha, BlendFunc.One );
		m_blendStateTransparent = new BlendState( true, BlendFunc.SrcAlpha, BlendFunc.OneMinusSrcAlpha );
		m_depthStateMaskDisable = new DepthStencilState( true, DepthFunc.Less, false );
	}

	public void Update( float fElapsedTime )
	{
		m_particles0.Update( fElapsedTime );
		m_particles1.Update( fElapsedTime );
	}

	public void Render( GfxCommandContext gfxc )
	{
		/*
		 if( m_texture.Name <= 0 )
		 {
		 return;
		 }
		 */

		//Render r = SubSystem.Render;
		MatrixCache mc = SubSystem.MatrixCache;
		mc.SetWorld( Float4x4.Identity() );

		//GLES20.glDepthMask( false );
		gfxc.SetDepthStencilState( m_depthStateMaskDisable );

//		gc.SetDepthFunc( DepthFuncMode.Less, false ); 
		//	gc.Enable( EnableMode.Blend );
//				gc.SetBlendFunc( new BlendFunc( BlendFuncMode.Add, BlendFuncFactor.SrcAlpha, BlendFuncFactor.One ) );
//		gc.SetBlendFuncAlpha( BlendFuncMode.Add, BlendFuncFactor.One, BlendFuncFactor.Zero );

		//GLES20.glEnable( GLES20.GL_BLEND );
		//GLES20.glBlendFunc( GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA );

		gfxc.SetBlendState( m_blendStateTransparent );
		if( 0 < m_texture.Name )
		{
			//gc.SetBlendFuncRgb( BlendFuncMode.Add, BlendFuncFactor.SrcAlpha, BlendFuncFactor.OneMinusSrcAlpha );
			m_particles0.Render( gfxc );

			//gc.SetBlendFuncRgb( BlendFuncMode.Add, BlendFuncFactor.SrcAlpha, BlendFuncFactor.One );
			//GLES20.glBlendFunc( GLES20.GL_SRC_ALPHA, GLES20.GL_ONE );
			gfxc.SetBlendState( m_blendStateAdd );
			m_particles1.Render( gfxc );
		}

		//gc.Disable( EnableMode.Blend );
		//GLES20.glDisable( GLES20.GL_BLEND );
		//GLES20.glBlendFunc( GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA );

		gfxc.SetBlendState( null );
		
		//GLES20.glDepthMask( true );
		gfxc.SetDepthStencilState( null );


		//gc.SetDepthFunc( DepthFuncMode.Less, true );
	}


	public void SpawnSmoke( Float3 v3Position, Float3 v3Velocity, float fRadius )
	{
		/*
		 if( System.Rand.Float() < 0.0f )
		 {
		 //return;
		 }
		 */

		float r = SubSystem.Rand.Float( 0.5f, 1.5f )*fRadius;
		float t = SubSystem.Rand.Float( 0.8f, 2.0f );
		CardboardStarSimpleParticle.ParticleDescriptor desc = CardboardStarSimpleParticle.ParticleDescriptor.Local(
			v3Position, v3Velocity,
			SubSystem.Rand.Float()*(float)Math.PI, SubSystem.Rand.Float( (float)Math.PI*0.15f, (float)Math.PI*0.6f ),
			SubSystem.Rand.Integer( 0, 2 ), 0,
			0.1f*t, r,
			0.0f*t, r*1.2f,
			0.1f*t,
			1.5f*t,1.4f*r
		);

		m_particles0.Spawn( desc );

		//desc = Simpledesc.Clone();
		desc.AngularVelocity = - desc.AngularVelocity;
		desc.Angle = SubSystem.Rand.Float()*(float)Math.PI;
		m_particles0.Spawn( desc );
	}


	public void SpawnFlash( Float3 v3Position, Float3 v3Velocity, float fRadius )
	{
		float r = SubSystem.Rand.Float( 0.5f, 1.5f )*fRadius;
		float t = SubSystem.Rand.Float( 1.0f, 1.0f );

		CardboardStarSimpleParticle.ParticleDescriptor desc = CardboardStarSimpleParticle.ParticleDescriptor.Local(
			v3Position,	v3Velocity,
			SubSystem.Rand.Float()*(float)Math.PI, SubSystem.Rand.Float( (float)Math.PI*0.0f, (float)Math.PI*0.0f ),

			0, 2,
			0.1f*t, 1.1f*r,
			0.0f*t, 1.0f*r,
			0.0f*t, 
			0.2f*t, 2.0f*r
		);

		m_particles1.Spawn( desc );

		//particle.AngularVelocity = - particle.AngularVelocity;
		//particle.Angle = Rand.Float()*FMath.PI;
		//m_particles.Spawn( particle );
	}


	public void SpawnExplosion( Float3 v3Position, Float3 v3Velocity, float fRadius )
	{
		float r = SubSystem.Rand.Float( 0.5f, 1.5f )*fRadius;
		float t = SubSystem.Rand.Float( 1.0f, 1.2f );

		CardboardStarSimpleParticle.ParticleDescriptor desc = CardboardStarSimpleParticle.ParticleDescriptor.Local(
			v3Position, v3Velocity,
			SubSystem.Rand.Float()*(float)Math.PI, SubSystem.Rand.Float( (float)Math.PI*0.0f, (float)Math.PI*0.1f ),
			SubSystem.Rand.Integer( 0, 3 ), 1,
			0.1f*t, 0.1f*r,
			0.1f*t, 1.0f*r,
			0.0f*t, 
			0.8f*t, 1.2f*r
		);

		m_particles1.Spawn( desc );

		//desc = desc.Clone();
		desc.AngularVelocity = - desc.AngularVelocity;
		desc.Angle = SubSystem.Rand.Float()*(float)Math.PI;
		m_particles1.Spawn( desc );
	}


	public void SpawnSmallExplosion( Float3 v3Position, float fRadius )
	{
		fRadius *= 0.01f;
		//Vector3 v30 = v3Position + (Rand.Vector3()*2.0f - 1.0f)*50.0f*fRadius;
		//Vector3 v31 = v3Position + (Rand.Vector3()*2.0f - 1.0f)*50.0f*fRadius;
		Float3 v30 = Float3.Add( Float3.Local(), v3Position, Float3.Rand( -50.0f*fRadius, 50.0f*fRadius ) );
		Float3 v31 = Float3.Add( Float3.Local(), v3Position, Float3.Rand( -50.0f*fRadius, 50.0f*fRadius ) );


		SpawnSmoke( v30, Float3.Rand( -50.0f*fRadius, 50.0f*fRadius ), 120.0f*fRadius );
		SpawnSmoke( v31, Float3.Rand( -50.0f*fRadius, 50.0f*fRadius ), 120.0f*fRadius );
		SpawnSmoke( Float3.Add( Float3.Local(), v3Position, Float3.Rand( -50.0f*fRadius, 50.0f*fRadius )), Float3.Rand( -50.0f*fRadius, 50.0f*fRadius ), 120.0f*fRadius );
		SpawnSmoke( Float3.Add( Float3.Local(), v3Position, Float3.Rand( -50.0f*fRadius, 50.0f*fRadius )), Float3.Rand( -50.0f*fRadius, 50.0f*fRadius ), 120.0f*fRadius );

		SpawnFlash( v3Position, new Float3( 0.0f ), 1000.0f*fRadius );

		SpawnExplosion( v30, Float3.Rand( -20.0f*fRadius, 20.0f*fRadius ), 100.0f*fRadius );
		SpawnExplosion( v31, Float3.Rand( -20.0f*fRadius, 20.0f*fRadius ), 100.0f*fRadius );
	}

}


