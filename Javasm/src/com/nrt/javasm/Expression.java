package com.nrt.javasm;

import java.util.List;
import java.util.ArrayList;

public final class Expression
{
	public List<Token> Tokens = new ArrayList<Token>();
	public boolean Addressing = false;

	public Expression()
	{
	}

	public int AddTokens( List<Token> listTokens, int i )
	{
		for( ; i < listTokens.size() ; i++ )
		{
			Token token = listTokens.get(i);
			if( token.Name.compareTo( "," ) == 0 )
			{
				break;
			}
			else
			{
				Tokens.add( token );
			}
		}

		if( Tokens.size() > 2 )
		{			
			String strTop = Tokens.get(0).Name;
			String strLast = Tokens.get(Tokens.size()-1).Name;

			if( strTop.compareTo( "(" ) == 0 && strLast.compareTo( ")" ) == 0 )
			{
				//Tokens.remove( Tokens.size()-1);
				//Tokens.remove( 0 );
				Addressing = true;
			}
		}

		return i;
	}
	
	public static boolean IsExpressionKeywordType( Token.EKeyword eKeyword )
	{
		if( eKeyword == null )
		{
			return false;
		}
		
		if( eKeyword == Token.EKeyword.AND ||
			eKeyword == Token.EKeyword.OR ||
			eKeyword == Token.EKeyword.XOR )
		{
			//. 演算子とかぶってるので OK.
			return true;
		}

		switch( eKeyword.Type )
		{
		case Number:
		case Hex:
		case Bin:
		case Operator:				
		case ParenthesesOpen:
		case ParenthesesClose:
		case Unknown:
			return true;

		case String:
		case Register:
		case RegisterPair:
		case Opcode:
		case Condition:
		case PseudoCode:
		case Comma:
		case LabelSuffix:
			return false;
		}
		
		return false;
	}
}

