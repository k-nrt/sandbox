package com.nrt.javasm;

public class NumericConstant
{
	public Token Name = null;
	public int Value = 0;
	public Expression Expression = null;
	
	public NumericConstant()
	{
	}
	
	public NumericConstant( Line line )
	{
		Name = line.ConstantLabel;
		Expression = line.Expressions.get(0);		
	}
}
