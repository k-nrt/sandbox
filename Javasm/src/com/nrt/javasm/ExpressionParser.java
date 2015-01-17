package com.nrt.javasm;

import java.util.List;
import java.util.ArrayList;

public class ExpressionParser implements OperandParser
{
	protected static Token.EKeyword[] Keywords = FindKeywords();

	public enum EType
	{
		Immediate8,
		Immediate16,
		RelativeAddress,
		RestrictNumber,
	};

	public EType Type = EType.Immediate8;
	public int RestrictValue = 0;

	public ExpressionParser(){}
	public ExpressionParser(EType eType)
	{
		Type = eType;
	}

	public ExpressionParser(EType eType, int value )
	{
		Type = eType;
		RestrictValue = value;
	}

	protected boolean IsMatch( List<Token> tokens, int start, int count )
	{
		for( int i = 0 ; i < count ; i++ )
		{

			Token token = tokens.get(start+i);

			for( Token.EKeyword keyword : Keywords )
			{
				if( keyword.IsMatch( token.Name ) )
				{
					return false;
				}
			}
		}

		return true;
	}

	@Override
	public boolean IsMatch(Expression expression)
	{
		if( expression == null )
		{
			return false;
		}

		if( expression.Tokens == null )
		{
			return false;
		}

		return IsMatch( expression.Tokens, 0, expression.Tokens.size() );
		/*
		 for( Token token : expression.Tokens )
		 {
		 for( Token.EKeyword keyword : Keywords )
		 {
		 if( keyword.IsMatch( token.Name ) )
		 {
		 return false;
		 }
		 }
		 }
		 return true;
		 */
	}

	@Override
	public String Dump()
	{

		return "Expression Parser";
	}		

	private static Token.EKeyword[] FindKeywords()
	{
		List<Token.EKeyword> listKeywords = new ArrayList<Token.EKeyword>();

		for( Token.EKeyword keyword : Token.EKeyword.values() )
		{
			if( Expression.IsExpressionKeywordType( keyword ) == false )
			{
				listKeywords.add( keyword );
			}
			/*
			if( keyword == Token.EKeyword.AND ||
			   keyword == Token.EKeyword.OR ||
			   keyword == Token.EKeyword.XOR )
			{
				//. 演算子とかぶるのではじく
				continue;
			}

			switch( keyword.Type )
			{
				case Number:
				case Hex:
				case Bin:
				case Operator:				
				case ParenthesesOpen:
				case ParenthesesClose:
				case Unknown:
					break;

				case Register:
				case RegisterPair:
				case Opcode:
				case Condition:
				case PseudoCode:
				case Comma:
				case LabelSuffix:
					listKeywords.add( keyword );
					break;
			}
			*/
		}

		return listKeywords.toArray( new Token.EKeyword[listKeywords.size()] );
	}
}
