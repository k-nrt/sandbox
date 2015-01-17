package com.nrt.javasm;

import java.util.List;
import java.util.ArrayList;

public class StringConstantList
{
	public List<Constant> Constants = new ArrayList<Constant>();
	public List<Constant> EvaluatedConstants = new ArrayList<Constant>();
	
	public void Evaluate( List<Line> listLines )
	{
		for( Line line : listLines )
		{
			if( line.ConstantLabel != null && line.Expressions.size() == 1 )
			{
				Expression exp = line.Expressions.get(0);
				
			}
		}
	}
	
}
