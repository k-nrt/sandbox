package com.nrt.javasm;

import java.util.List;
import java.util.ArrayList;

public final class Line
{
	public List<Token> Tokens = new ArrayList<Token>();
	public int LineNumber = 0;

	public Token ConstantLabel = null;
	public Token AddressLabel = null;
	public Token Opcode = null;

	public List<Expression> Expressions = new ArrayList<Expression>();
	
	public Instruction Instruction = null;
	public PseudoCode PseudoCode = null;

	public int Address = 0;
	
	public Line(int iLineNumber)
	{
		LineNumber = iLineNumber;
	}	

	public boolean Parse()
	{
		if( Tokens.size() <= 0 )
		{
			return true;
		}

		int i = 0;
		if( Tokens.size() > 1 )
		{
			Token token = Tokens.get(1);

			if( token.Type == Token.EType.LabelSuffix )
			{
				AddressLabel = Tokens.get(0);
				i = 2;
			}
			else if( token.Keyword == Token.EKeyword.Equal )
			{
				ConstantLabel = Tokens.get(0);
				i = 1;
			}
		}

		if( (Tokens.size() - i) <= 0 )
		{
			return true;
		}

		Token token = Tokens.get(i);

		Opcode = token;
		i++;
		for( ; i < Tokens.size() ; i++ )
		{
			Expression exp = new Expression();
			i = exp.AddTokens( Tokens, i );

			Expressions.add( exp );
		}
		
		return true;
	}
}

