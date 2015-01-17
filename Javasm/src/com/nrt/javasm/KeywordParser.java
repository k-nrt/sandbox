package com.nrt.javasm;


public class KeywordParser implements OperandParser
{
	public Token.EKeyword[] Keywords = null;

	public KeywordParser( String ... arrayKeywords )
	{
		Keywords = new Token.EKeyword[arrayKeywords.length];
		for( int i = 0 ; i < arrayKeywords.length ; i++ )
		{
			Keywords[i] = Token.EKeyword.Match( arrayKeywords[i] );
		}
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

		if( expression.Tokens.size() != Keywords.length )
		{
			return false;
		}

		for( int i = 0 ; i < expression.Tokens.size() ; i++ )
		{
			if( Keywords[i] == null )
			{
				return false;
			}

			if( Keywords[i].IsMatch( expression.Tokens.get(i).Name.toUpperCase() ) == false )
			{
				return false;
			}
		}

		return true;
	}

	@Override
	public String Dump()
	{
		String strResult = " KeywordParser";
		for( Token.EKeyword keyword : Keywords )
		{
			if( keyword == null )
			{
				strResult += " null";
			}
			else
			{
				strResult += " " + keyword.toString();
			}
		}
		// TODO: Implement this method
		return strResult;
	}			
}
