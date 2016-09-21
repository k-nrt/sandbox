package com.nrt.render;
import com.nrt.basic.DebugLog;
import android.opengl.GLES30;

public class Program extends RenderResource
{
	public VertexShader VertexShader = null;
	public FragmentShader FragmentShader = null;
	public AttributeBinding[] AttributeBindings = null;

	public void Initialize
	(
		ResourceQueue queue,
		AttributeBinding[] attributeBindings, 
		VertexShader vs,
		FragmentShader fs
	)
	{
		VertexShader = vs;
		FragmentShader = fs;

		AttributeBindings = attributeBindings;

		if( queue == null )
		{
			Apply();
		}
		else
		{
			queue.Add( this );
		}
	}

	@Override
	public void Apply()
	{		
		if (VertexShader.Name == 0 || FragmentShader.Name == 0)
		{
			com.nrt.basic.DebugLog.Error.WriteLine( "can not create program" );
			return;
		}
		Name = GLES30.glCreateProgram();
		com.nrt.basic.DebugLog.Error.WriteLine( String.format( "apply program %d vs=%d fs=%d", Name, VertexShader.Name, FragmentShader.Name) );

		GLES30.glAttachShader(Name, VertexShader.Name); 
		GLES30.glAttachShader(Name, FragmentShader.Name);

		for (int i = 0 ; i < AttributeBindings.length ; i++)
		{
			GLES30.glBindAttribLocation(Name,
										AttributeBindings[i].Index,
										AttributeBindings[i].Name);
		}

		GLES30.glLinkProgram(Name);
	}

	public Program()
	{}

	public Program( ResourceQueue queue, AttributeBinding[] attributeBindings, VertexShader vs, FragmentShader fs)
	{
		Initialize( queue, attributeBindings, vs, fs );
	}
}


