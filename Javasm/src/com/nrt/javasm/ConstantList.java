package com.nrt.javasm;

import java.util.List;
import java.util.ArrayList;
import java.lang.reflect.*;

public class ConstantList
{
	public List<Constant> Constants = new ArrayList<Constant>();
	
	
	public void Create( List<Line> listLines )
	{
		for( Line line : listLines )
		{
			Constant constant = CreateConstant( line );
			
			if( constant != null )
			{
				Constants.add( constant );
			}
		}
		
		for( Constant constant : Constants )
		{
			Evaluate( constant );
		}		
	}
	
	public Constant CreateConstant( Line line )
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
			constant.Type = Constant.EType.Address;
			//constant.Expression = line.Expressions.get(0);
			return constant;
		}

		return null;
	}
	
	public Constant.EType DetectType( Constant constant, Constant constantBase )
	{
		if( constant == constantBase )
		{
			return Constant.EType.Unknown;
		}
		
		for( Token token : constant.Expression.Tokens )
		{
			switch( token.Type )
			{
				case Number:
				case Hex:
				case Bin:
					return Constant.EType.Value;
					
				case String:
					return Constant.EType.String;

				case Register:
				case RegisterPair:				
				case Condition:
				case Opcode:
				case PseudoCode:
				case Comma:
				case LabelSuffix:
				case Unknown:
					return Constant.EType.Unknown;
					
				case Operator:
				case ParenthesesOpen:
				case ParenthesesClose:
					break;
					
				case UserKeyword:
					{
						Constant c = FindConstant( token );
						if( c.Type != Constant.EType.Unknown )
						{
							return c.Type;
						}
						else
						{
							return DetectType( c, constantBase );
						}
					}
			}
		}
		
		return Constant.EType.Unknown;
	}
	
	public Constant FindConstant( Token token )
	{
		for( Constant constant : Constants )
		{
			if( constant.Name.Name.compareTo( token.Name ) == 0 )
			{
				return constant;
			}
		}
		
		return null;
	}		
	
	public boolean Evaluate( Constant constant )
	{		
		for( Token token : constant.Expression.Tokens )
		{
			switch( token.Type )
			{
			case Number:
			case Hex:
			case Bin:
				if( constant.Type == Constant.EType.Unknown )
				{
					constant.Type = Constant.EType.Value;
					break;
				}
				else
				{
					return false;
				}
				
			case String:
				if( constant.Type == Constant.EType.Unknown )
				{
					constant.Type = Constant.EType.String;
					break;
				}
				else
				{
					return false;
				}
				
			case Register:
			case RegisterPair:
			case Condition:
			
			case Opcode:
			case PseudoCode:
			case Comma:
			case LabelSuffix:
			case Unknown:
				return false;
					
			
			case Operator:					
			case ParenthesesOpen:
			case ParenthesesClose:
				break;

			case UserKeyword:
				{
					Constant c = FindConstant( token );
					
				}
				break;
			}
			

			
		}
		
		return true;
	}
	
}
