package nrt.render;
import android.opengl.*;
public enum IndexFormat
{
	UnsignedShort(GLES20.GL_UNSIGNED_SHORT),
	UnsignedInt(GLES20.GL_UNSIGNED_INT);
	
	public int Value = 0;
	private IndexFormat(int value)
	{
		Value = value;
	}
}


