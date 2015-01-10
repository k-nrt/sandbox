package nrt.render;



public class Sampler extends Uniform
{
	public int TextureUnit = -1;

	@Override
	public void Initialize(DelayResourceQueue queue, Program program, int iTextureUnit, String strName)
	//throws ThreadForceDestroyException
	{
		// TODO: Implement this method
		TextureUnit = iTextureUnit;
		super.Initialize(queue, program, strName);
	}

	
	
	
	
	public Sampler()
	{}
/*
	public Sampler( Program program, int iTextureUnit, String name) throws ThreadForceDestroyException
	{
		Initialize( null, program, iTextureUnit, name );
		//Create( this, program, iTextureUnit, name );
		//super(program, name);
		//TextureUnit = iTextureUnit;
	}
*/
	public Sampler( DelayResourceQueue queue, Program program, int iTextureUnit, String name)
	//throws ThreadForceDestroyException
	{
		Initialize( queue, program, iTextureUnit, name );
		//Create( this, program, iTextureUnit, name );
		//super(program, name);
		//TextureUnit = iTextureUnit;
	}
	
	/*
	public static void Create( Sampler sampler, Program program, int iTextureUnit, String name)
	{
		Uniform.Create( sampler, program, name );
		sampler.TextureUnit = iTextureUnit;
	}
	*/
}

