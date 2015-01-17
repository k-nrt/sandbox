package com.nrt.javasm;

import java.util.List;
import java.util.ArrayList;

public class IndexRegisterAddressParser extends ExpressionParser
	{
		protected Token.EKeyword IndexRegister = null;

		public IndexRegisterAddressParser( Token.EKeyword eIndexRegister )
		{
			IndexRegister = eIndexRegister;
		}

		@Override
		public boolean IsMatch(Expression expression)
		{
			if( expression == null )
			{
				return false;
			}

			List<Token> listTokens = expression.Tokens; 

			if( listTokens == null )
			{
				return false;
			}

			if( listTokens.size() < 3 )
			{
				return false;
			}

			if( listTokens.get(0).Type != Token.EType.ParenthesesOpen )
			{
				return false;
			}

			if( listTokens.get(1).Keyword != IndexRegister )
			{
				return false;
			}			

			if( listTokens.get(listTokens.size()-1).Type != Token.EType.ParenthesesClose )
			{
				return false;
			}

			if( listTokens.size() == 3 )
			{
				return true;
			}

			if( listTokens.get(2).Keyword != Token.EKeyword.Plus &&
			   listTokens.get(2).Keyword != Token.EKeyword.Minus )
			{
				return false;		
			}

			return IsMatch( listTokens, 3, listTokens.size()-4);
		}

		@Override
		public String Dump()
		{
			return "IndexOffsetAddressParser";
		}


	}
