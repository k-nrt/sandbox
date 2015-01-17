package com.nrt.javasm;

import java.util.List;
import java.util.ArrayList;

public final class Tokenizer
{
	public String Separators = " \n\t";	
	public String SeparatorTokens = ":,()+-*/%|&^~";
	
	public String[] SeparatorKeywords = { "<<", ">>" };
	

	public final List<Token> Tokens = new ArrayList<Token>();

	public Tokenizer()
	{}

	public final void Tokenize(String[] arrayLines)
	{
		int nDepth = 0;
		for (int i = 0 ; i < arrayLines.length ; i++)
		{			
			String strLine = arrayLines[i];
			String strToken = "";
			
			boolean isString = false;
			boolean isEscape = false;
			
			for (int j = 0 ; j < strLine.length() ; j++)
			{
				char c = strLine.charAt(j);

				if (isString)
				{
					if( isEscape )
					{
						if( c == 't' )
						{
							strToken += '\t';
						}
						else if( c == 'n' )
						{
							strToken += '\n';
						}
						else
						{
							strToken += c;							
						}
						isEscape = false;
					}
					else
					{
						if( c == '"' )
						{
							//. end string.
							isString = false;
							strToken += "\"";
							AddToken( strToken, i, nDepth );
							strToken = "";
						}
						else if( c == '\\' )
						{
							//. begin escape.
							isEscape = true;
						}
						else if( c == '\t' || c == '\n' )
						{
							
						}
						else
						{
							strToken += c;
						}
					}
				}
				else
				{
					if (c == ';')
					{
						//. comment.
						break;
					}
					else if( c == '"' )
					{
						//. begin string.
						AddToken(strToken,i,nDepth);
						isString = true;
						strToken = "\"";
					}
					else if (Contains(c, Separators))
					{
						//. separator.
						AddToken(strToken, i, nDepth);
						strToken = "";
					}
					else if (Contains(c, SeparatorTokens))
					{
						//. separator token.
						AddToken(strToken, i, nDepth);
						
						if (c == ')')
						{
							nDepth--;
						}

						AddToken("" + c, i, nDepth);
						strToken = "";

						if (c == '(')
						{
							nDepth++;
						}
					}
					else
					{
						strToken += c;
						
						String strKeyword = Contains(strToken,SeparatorKeywords);
						if( strKeyword != null )
						{
							AddToken( strToken.substring(0,strToken.length()-strKeyword.length() ), i, nDepth );
							AddToken( strKeyword, i, nDepth );
							strToken = "";
						}
					}
				}
			}

			AddToken(strToken, i, nDepth); 
		}
	}

	private final boolean Contains(char c, String strTarget)
	{
		for (int i = 0 ; i < strTarget.length() ; i++)
		{
			if (c == strTarget.charAt(i))
			{
				return true;
			}
		}

		return false;
	}
	
	private final String Contains( String strToken, String[] listKeywords )
	{
		for( String strKeyword : listKeywords )
		{
			if( strToken.length() < strKeyword.length() )
			{
				continue;
			}
			
			if( Token.IsMatchLast( strToken, strKeyword ) )
			{
				return strKeyword;
			}
		}
		
		return null;
	}

	private final void AddToken(String strToken, int nLine, int nDepth)
	{
		if (strToken.length() > 0)
		{			
			Tokens.add(new Token(strToken, nLine, nDepth));
		}
	}
}

