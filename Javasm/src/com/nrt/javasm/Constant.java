package com.nrt.javasm;

public class Constant
{
	public enum EType
	{
		Unknown,
		Address,
		Value,
		String,
	}
	
	public EType Type = EType.Unknown;
	
	public Token Name = null;	
	public Expression Expression = null;
	
	public boolean Evaluated = false;
	
	public int Value = 0;
	public String Text = null;

	
	public static Constant Create( Line line )
	{
		if( line.ConstantLabel != null )
		{
			Constant constant = new Constant();
			constant.Name = line.ConstantLabel;
			constant.Expression = line.Expressions.get(0);
			return constant;			
		}
		else if( line.AddressLabel != null )
		{
			Constant constant = new Constant();
			constant.Name = line.AddressLabel;
			constant.Type = EType.Address;
			//constant.Expression = line.Expressions.get(0);
			return constant;
		}
		
		return null;
	}
}

