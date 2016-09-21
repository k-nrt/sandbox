package com.nrt.model;

import android.opengl.GLES20;

import com.nrt.render.*;
import com.nrt.math.Float3;
import com.nrt.math.Float4;
import com.nrt.math.Float4x4;
import com.nrt.basic.Loader;

public class ModelRender
{
	public static class AmbientLight
	{
		public final Float3 Color = new Float3( 0.05f, 0.05f, 0.05f );
	}
	
	public static class DirectionalLight
	{
		public final Float3 Color = new Float3( 2.0f );
		public final Float3 Direction = Float3.Normalize( new Float3(), Float3.Local( 1.0f, 1.0f, -1.0f ) );
	}
	
	public static class ThreeSphereLight
	{
		public final Float3 SkyColor = new Float3( 0.0f, 0.0f, 0.05f );
		public final Float3 HorizonColor = new Float3( 0.3f, 0.3f, 0.4f );
		public final Float3 EarthColor = new Float3( 0.0f, 0.0f, 0.05f );
		public final Float3 SkyDirection = new Float3( 0.0f, 1.0f, 0.0f );
	}
	
	public static class Fog
	{
		public float Near = 0.0f;
		public float Far = 10000.0f;
		public final Float4 Color = new Float4( 0.0f );
	}
	
	public final AmbientLight AmbientLight = new AmbientLight();
	public final DirectionalLight DirectionalLight = new DirectionalLight();
	public final ThreeSphereLight ThreeSphereLight = new ThreeSphereLight();
	public final Fog Fog = new Fog();
		
	public ModelShadow Shadow = new ModelShadow();
	
	public SamplerState SamplerStateNearest = null;
		
	public final int kMaxTransforms = 64;
	public final float[] m_arrayTransformsX = new float[kMaxTransforms*4];
	public final float[] m_arrayTransformsY = new float[kMaxTransforms*4];
	public final float[] m_arrayTransformsZ = new float[kMaxTransforms*4];
	
	public ModelShaderSet ShaderSet = null;
	public ModelShaderPluginParameterList ShaderParameter = null;
	
	public ModelShaderSet DefaultShaderSet = null;
	public DelayResourceQueueMarker Marker = new DelayResourceQueueMarker("ModelRender");
	public ModelRender( DelayResourceQueue drq, Loader loader )
	{			
		DefaultShaderSet = new ModelShaderSet( drq, loader, "nrt_model_render.glsl", null, null );
		
		ShaderSet = DefaultShaderSet;
		
		SamplerStateNearest = new SamplerState
		(
			MagFilter.Nearest, MinFilter.Nearest,
			Wrap.ClampToEdge, Wrap.ClampToEdge 
		);
		
		drq.Add(Marker);
	}
	
	public void SetShaderSet( ModelShaderSet shaderSet )
	{
		if( shaderSet != null )
		{
			ShaderSet = shaderSet;
		}
		else
		{
			ShaderSet = DefaultShaderSet;
		}
	}
	
	public void SetShaderParameter( ModelShaderPluginParameterList shaderParameter )
	{
		ShaderParameter = shaderParameter;
	}
	
	public void SetShadowParameter( ModelShadow shadow )
	{
		Shadow = shadow;
	}
	
	public final void Draw
	(
		final GfxCommandContext gfxc, 
		final MatrixCache mc,
		final Model model, 
		final Float4x4[] modelMatrices, 
		final ModelShaderSet.EShadow eShadow 
	)
	{
		if(!Marker.Done)
		{
			return;
		}
		
		if( gfxc == null || mc == null )
		{
			return;
		}
		
		for( int i = 0 ; i < model.Joints.length ; i++ )
		{
			Float4x4 m = modelMatrices[i];
			
			m_arrayTransformsX[i*4+0] = m.Values[0];
			m_arrayTransformsX[i*4+1] = m.Values[4];
			m_arrayTransformsX[i*4+2] = m.Values[8];
			m_arrayTransformsX[i*4+3] = m.Values[12];
		
			m_arrayTransformsY[i*4+0] = m.Values[1];
			m_arrayTransformsY[i*4+1] = m.Values[5];
			m_arrayTransformsY[i*4+2] = m.Values[9];
			m_arrayTransformsY[i*4+3] = m.Values[13];
			
			m_arrayTransformsZ[i*4+0] = m.Values[2];
			m_arrayTransformsZ[i*4+1] = m.Values[6];
			m_arrayTransformsZ[i*4+2] = m.Values[10];
			m_arrayTransformsZ[i*4+3] = m.Values[14];
		}
		
		for( int i = 0 ; i < model.Materials.length ; i++ )
		{
			DrawRigidSkin( gfxc, mc, i, model.Materials[i], model.Meshes, model.Joints.length, eShadow );
		}
	}
	
	public final void DrawRigidSkin
	(
		final GfxCommandContext gfxc, 
		final MatrixCache mc, 
		final int iMaterial, final ModelMaterial material, 
		final ModelMesh[] meshes, final int nbJoints, 
		final ModelShaderSet.EShadow eShadow 
	)
	{
		if(!Marker.Done)
		{
			return;
		}
		
		if(gfxc == null || mc == null)
		{
			return;
		}
		
		ModelShader shader = ShaderSet.GetShader
		(
			ModelShaderSet.EDeformation.RigidSkin,
			eShadow,
			material.Technique 
		);
		
		gfxc.SetProgram( shader.Program );
		SetBaseUniforms( gfxc, mc, shader, iMaterial, material );
		
		for( int i = 0 ; i < material.MeshCount ; i++ )
		{
			int nJointOffset = i*16;
			int nbMeshJoints = nbJoints - nJointOffset;
			DrawRigidSkinMesh
			( 
				gfxc, meshes[material.MeshStart + i],
				(nbMeshJoints > 16 ? 16 : nbMeshJoints), nJointOffset,
				shader 
			);
		}
	}
	
	public final void DrawRigidSkinMesh
	(
		final GfxCommandContext gfxc, 
		final ModelMesh mesh,
		final int nbJoints, 
		final int nJointOffset, 
		final ModelShader shader 
	)
	{
		if(!Marker.Done)
		{
			return;
		}
		
		if(gfxc == null )
		{
			return;
		}
		
		gfxc.SetFloat4Array( shader.u_transformsX, nbJoints, m_arrayTransformsX, nJointOffset*4);
		gfxc.SetFloat4Array( shader.u_transformsY, nbJoints, m_arrayTransformsY, nJointOffset*4);
		gfxc.SetFloat4Array( shader.u_transformsZ, nbJoints, m_arrayTransformsZ, nJointOffset*4);
		
		gfxc.SetFloat( shader.u_indexOffset, (float) nJointOffset );
		
		gfxc.SetVertexBuffer( mesh.VertexBuffer );
		gfxc.EnableVertexStream( mesh.Stream,0 );
		gfxc.SetIndexBuffer( mesh.IndexBuffer );
		gfxc.DrawElements( Primitive.Triangles, mesh.Indices.length, IndexFormat.UnsignedShort, 0 );	
		gfxc.DisableVertexStream( mesh.Stream );
	}
	
	
	
	private final void SetBaseUniforms
	(
		final GfxCommandContext gfxc, 
		final MatrixCache mc, 
		final ModelShader shader,
		final int iMaterial, 
		final ModelMaterial material
	)
	{
		if( !Marker.Done )
		{
			return;
		}
		
		if( gfxc == null || mc == null )
		{
			return;
		}
		
		gfxc.SetMatrix( shader.u_worldViewProjection, mc.GetWorldViewProjection().Values );
		gfxc.SetMatrix( shader.u_world, mc.GetWorld().Values );
		gfxc.SetMatrix( shader.u_viewPosition, mc.GetViewInverse().Values );

		gfxc.SetFloat3( shader.u_ambient, material.Ambient.X, material.Ambient.Y, material.Ambient.Z );
		gfxc.SetFloat3( shader.u_diffuse, material.Diffuse.X, material.Diffuse.Y, material.Diffuse.Z );
		gfxc.SetFloat3( shader.u_specular, material.Specular.X, material.Specular.Y, material.Specular.Z );
		gfxc.SetFloat3( shader.u_emission, material.Emission.X, material.Emission.Y, material.Emission.Z );

		gfxc.SetFloat( shader.u_power, material.Power );

		gfxc.SetFloat3( shader.u_lightAmbient, AmbientLight.Color );

		gfxc.SetFloat3( shader.u_lightColor, DirectionalLight.Color );
		gfxc.SetFloat3( shader.u_lightDirection, DirectionalLight.Direction );

		gfxc.SetFloat3( shader.u_skyColor, ThreeSphereLight.SkyColor );
		gfxc.SetFloat3( shader.u_horizonColor, ThreeSphereLight.HorizonColor );
		gfxc.SetFloat3( shader.u_earthColor, ThreeSphereLight.EarthColor );
		gfxc.SetFloat3( shader.u_skyDirection, ThreeSphereLight.SkyDirection );
		
		gfxc.SetFloat( shader.u_fogNear, Fog.Near );
		gfxc.SetFloat( shader.u_fogFar, Fog.Far );
		gfxc.SetFloat4( shader.u_fogColor, Fog.Color );
		
		if( Shadow != null )
		{
			gfxc.SetMatrix( shader.u_shadowProjection, Shadow.GetWorldViewProjection().Values );
			gfxc.SetFloat3( shader.u_shadowDirection, Shadow.GetShadowDirection() );
			gfxc.SetTexture( shader.u_samplerShadow, Shadow.Texture );
			gfxc.SetSamplerState( shader.u_samplerShadow, SamplerStateNearest );
		}
		
		if( shader.Uniforms != null && ShaderParameter != null )
		{
			ShaderParameter.OnUpdateParameter( gfxc, shader.Uniforms, iMaterial, material );
		}
	}
	
	public final void Draw
	(
		final GfxCommandContext gfxc,
		final MatrixCache mc,
		final Model model, 
		final ModelShaderSet.EShadow eShadow 
	)
	{
		if( !Marker.Done )
		{
			return;
		}

		if( gfxc == null || mc == null )
		{
			return;
		}
		for( int i = 0 ; i < model.Materials.length ; i++ )
		{
			Draw( gfxc, mc, i, model.Materials[i], model.Meshes, eShadow );
		}
	}
	
	public final void Draw
	(
		final GfxCommandContext gfxc,
		final MatrixCache mc, 
		final int iMaterial, final ModelMaterial material,
		final ModelMesh[] meshes, 
		final ModelShaderSet.EShadow eShadow
	)
	{
		if(!Marker.Done)
		{
			return;
		}
		
		if(gfxc==null||mc==null)
		{
			return;
		}
		
		ModelShader shader = ShaderSet.GetShader( 
			ModelShaderSet.EDeformation.Rigid,
			eShadow,
			material.Technique );
		
		gfxc.SetProgram( shader.Program );
		SetBaseUniforms( gfxc, mc, shader, iMaterial, material );
		
		for( int i = 0 ; i < material.MeshCount ; i++ )
		{
			Draw( gfxc,  meshes[material.MeshStart + i] );
		}
	}
	
	public final void Draw( final  GfxCommandContext gfxc, final ModelMesh mesh )
	{
		if( gfxc == null )
		{
			return;
		}
		
		gfxc.SetVertexBuffer( mesh.VertexBuffer );
		gfxc.EnableVertexStream( mesh.Stream,0 );
		gfxc.SetIndexBuffer( mesh.IndexBuffer );
		gfxc.DrawElements( Primitive.Triangles, mesh.Indices.length, IndexFormat.UnsignedShort, 0 );	
		gfxc.DisableVertexStream( mesh.Stream );
	}
}
