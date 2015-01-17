package com.nrt.javasm;

public class ExpressionAddressParser extends ExpressionParser
{

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

		if( expression.Tokens.size() < 3 )
		{
			return false;
		}

		if( expression.Tokens.get(0).Type != Token.EType.ParenthesesOpen )
		{
			return false;
		}

		if( expression.Tokens.get(expression.Tokens.size()-1).Type != Token.EType.ParenthesesClose )
		{
			return false;
		}

		return IsMatch(expression.Tokens, 1, expression.Tokens.size()-2 );
	}

	@Override
	public String Dump()
	{
		return "Address Expression Parser";
	}
}

