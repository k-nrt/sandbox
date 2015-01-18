package com.nrt.javasm;

import java.util.List;
import java.util.ArrayList;
import android.os.*;

public class PseudoCode
{
	public interface DataParser
	{
		public boolean IsMatch( List<Expression> listExpressions );
		
	};
	
	public interface DataCounter
	{
		public int GetDataLength( List<Expression> listExpressions );
	};
	
	private static class ConstantParser implements DataParser
	{
		@Override
		public boolean IsMatch(List<Expression> listExpressions)
		{
			if( listExpressions == null )
			{
				return false;
			}
			
			if( listExpressions.size() != 1)
			{
				return false;
			}
			
			return IsConstantExpression( listExpressions.get(0) );
		}
		
		public static boolean IsConstantExpression( Expression expression )
		{
			for( Token token : expression.Tokens )
			{
				if( Expression.IsExpressionKeywordType( token.Keyword ) == false )
				{
					return false;
				}
			}
			
			return true;
		}
	}
	
	private static class ZeroDataCounter implements DataCounter
	{
		@Override
		public int GetDataLength(List<Expression> listExpressions)
		{
			return 0;
		}
	}
	
	private static class ValueListParser implements DataParser
	{

		@Override
		public boolean IsMatch(List<Expression> listExpressions)
		{
			if( listExpressions == null )
			{
				return false;
			}
			
			if( listExpressions.size() <= 0 )
			{
				return false;
			}
			
			
			for( Expression expression : listExpressions )
			{
				if( ConstantParser.IsConstantExpression( expression ) == false )
				{
					return false;
				}
			}

			return true;
		}
	}
	
	private static class ByteListDataCounter implements DataCounter
	{
		@Override
		public int GetDataLength(List<Expression> listExpressions)
		{
			return listExpressions.size();
		}		
	}
	
	private static class WordListDataCounter implements DataCounter
	{
		@Override
		public int GetDataLength(List<Expression> listExpressions)
		{
			return listExpressions.size()*2;
		}		
	}
	
	private static class StringExpressionParser implements DataParser
	{

		@Override
		public boolean IsMatch(List<Expression> listExpressions)
		{
			if( listExpressions == null )
			{
				return false;
			}
			
			if( listExpressions.size() != 1 )
			{
				return false;
			}

			return IsStringExpression( listExpressions.get(0) );	
		}

		public static boolean IsStringExpression( Expression expression )
		{
			for( Token token : expression.Tokens )
			{
				if( IsStringKeyword( token.Keyword ) == false )
				{
					return false;
				}
			}
			
			return true;
		}
		
		public static boolean IsStringKeyword( Token.EKeyword eKeyword )
		{
			if( eKeyword == null )
			{
				return false;
			}
			
			switch( eKeyword.Type )
			{
				case String:
				case Unknown:
					return true;
					
				case Operator:
					if( eKeyword == Token.EKeyword.Plus )
					{
						return true;
					}
					else
					{
						return false;
					}
					
				case Number:
				case Hex:
				case Bin:
				case Register:
				case RegisterPair:
				case Condition:
				case Opcode:
				case PseudoCode:
				case Comma:
				case LabelSuffix:
				case ParenthesesOpen:
				case ParenthesesClose:
					return false;
			}
			
			return false;
		}
		
	}
	
	private static class StringDataCounter implements DataCounter
	{

		@Override
		public int GetDataLength(List<Expression> listExpressions)
		{
			//. eval しないとダメだけどとりあえず.
			return listExpressions.get(0).Tokens.get(0).Name.length() - 2;
		}
	}
	
	
	
	public enum EDataType
	{
		Constant_x1( new ConstantParser() ),
		Value_xN( new ValueListParser() ),
		String_x1( new StringExpressionParser() );
		
		public DataParser DataParser = null;
		public EDataType( DataParser dataParser )
		{
			DataParser = dataParser;
		}
	};
	
	public enum EType
	{
		Origin(Token.EKeyword.Origin, EDataType.Constant_x1, new ZeroDataCounter() ),
		Equal(Token.EKeyword.Equal, EDataType.Constant_x1, new ZeroDataCounter() ),
		ByteData( Token.EKeyword.ByteData, EDataType.Value_xN, new ByteListDataCounter() ),
		WordData( Token.EKeyword.WordData, EDataType.Value_xN, new WordListDataCounter() ),
		StringData( Token.EKeyword.StringData, EDataType.String_x1, new StringDataCounter() );
		
		public Token.EKeyword Keyword = null;
		public EDataType DataType = null;
		public DataCounter DataCounter = null;
		
		
		private EType( Token.EKeyword keyword, EDataType eDataType, DataCounter dataCounter )
		{
			Keyword = keyword;
			DataType = eDataType;
			DataCounter = dataCounter;
		}		
		
		public boolean IsMatch( Line line )
		{
			if( line.Opcode == null )
			{
				return false;
			}
			
			if( line.Opcode.Keyword != Keyword )
			{
				return false;
			}
			
			return true;//DataType.DataParser.IsMatch( line.Expressions );			 
		}
	}
	
	public EType Type = null;
	public List<Expression> Expressions = null;
	
	public PseudoCode(){}
	public PseudoCode( EType eType, List<Expression> listExpressions )
	{
		Type = eType;
		Expressions = listExpressions;
	}
	
	public static PseudoCode Find( Line line )
	{
		if( line.Opcode == null )
		{
			return null;
		}
		
		for( EType eType : EType.values() )
		{
			if( eType.IsMatch( line ) )
			{
				return new PseudoCode( eType, line.Expressions );
			}
		}
		
		return null;
	}
}
