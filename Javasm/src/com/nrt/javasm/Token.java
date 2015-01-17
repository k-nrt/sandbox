package com.nrt.javasm;

public final class Token
{
	public String Name = "";
	public int Line = 0;
	public int Depth = 0;
	
	public enum EType
	{
		Number,
		Hex,
		Bin,
		String,
		Register,
		RegisterPair,
		Operator,
		Condition,
		Opcode,
		PseudoCode,
		Comma,
		LabelSuffix,
		ParenthesesOpen,
		ParenthesesClose,
		UserKeyword,
		Unknown,
	};
	
	public EType Type = EType.Unknown;
	
	public enum EKeyword
	{
		ADD(EType.Opcode), ADC(EType.Opcode), AND(EType.Opcode),
		BIT(EType.Opcode),
		CALL(EType.Opcode), CP(EType.Opcode), 
		CPIR(EType.Opcode), CPDR(EType.Opcode), CPI(EType.Opcode), CPD(EType.Opcode),
		CPL(EType.Opcode), CCF(EType.Opcode),
		DEC(EType.Opcode), DAA(EType.Opcode), DJNZ(EType.Opcode), DI(EType.Opcode),
		EX(EType.Opcode), EXX(EType.Opcode), EI(EType.Opcode), 
		HALT(EType.Opcode),
		IN(EType.Opcode), INC(EType.Opcode), 
		INIR(EType.Opcode), INDR(EType.Opcode), INI(EType.Opcode), IND(EType.Opcode),
		IM(EType.Opcode), 
		JP(EType.Opcode), JR(EType.Opcode),
		LD(EType.Opcode),
		LDIR(EType.Opcode), LDDR(EType.Opcode), LDI(EType.Opcode), LDD(EType.Opcode),
		NOP(EType.Opcode), NEG(EType.Opcode),
		OUT(EType.Opcode),
		OTIR(EType.Opcode), OTDR(EType.Opcode), OUTI(EType.Opcode), OUTD(EType.Opcode), 
		OR(EType.Opcode),
		PUSH(EType.Opcode), POP(EType.Opcode), 
		RET(EType.Opcode), RETI(EType.Opcode), RETN(EType.Opcode),
		RST(EType.Opcode), RES(EType.Opcode),
		RLCA(EType.Opcode), RRCA(EType.Opcode), 
		RLA(EType.Opcode), RRA(EType.Opcode), 
		RLC(EType.Opcode), RRC(EType.Opcode),
		RL(EType.Opcode), RR(EType.Opcode),
		RLD(EType.Opcode), RRD(EType.Opcode),
		SLA(EType.Opcode), SRA(EType.Opcode), SRL(EType.Opcode),
		SUB(EType.Opcode), SBC(EType.Opcode), SET(EType.Opcode), SCF(EType.Opcode),
		XOR(EType.Opcode),
		
		A(EType.Register),F(EType.Register),
		B(EType.Register),C(EType.Register),
		D(EType.Register),E(EType.Register),
		H(EType.Register),L(EType.Register),
		R(EType.Register),I(EType.Register),
		
		AF(EType.RegisterPair),BC(EType.RegisterPair),
		DE(EType.RegisterPair),HL(EType.RegisterPair),
		SP(EType.RegisterPair),PC(EType.RegisterPair),
		IX(EType.RegisterPair),IY(EType.RegisterPair),
		AFx(EType.RegisterPair,"AF'"),
		//CY(EType.Condition,"C"),
		NC(EType.Condition),
		Z(EType.Condition),NZ(EType.Condition),
		P0(EType.Condition),PE(EType.Condition),
		P(EType.Condition),M(EType.Condition),
		
		Plus( EType.Operator, "+" ),
		Minus( EType.Operator, "-" ),
		Mul( EType.Operator, "*" ),
		Div( EType.Operator, "/" ),
		Mod( EType.Operator, "%" ),
		And( EType.Operator, "&", "AND" ),
		Or( EType.Operator, "|", "OR" ),
		Xor( EType.Operator, "^", "XOR" ),
		Not( EType.Operator, "~", "NOT" ),
		ShiftLeft( EType.Operator, "<<" ),
		ShiftRight( EType.Operator, ">>" ),
		
		Equal( EType.PseudoCode, "=", "EQU" ),
		ByteData( EType.PseudoCode, "DEFB", "DB" ),
		WordData( EType.PseudoCode, "DEFW", "DW" ),
		StringData( EType.PseudoCode, "DEFS", "DS" ),
		Origin( EType.PseudoCode, "ORG" ),
		
		LabelSuffix( EType.LabelSuffix, ":" ),
		Comma( EType.Comma, "," ),
		
		ParenthesesOpen( EType.ParenthesesOpen, "(" ),
		ParenthesesClose( EType.ParenthesesClose, ")" );
		
		//Label( EType.Unknown );
		
		
		public EType Type = EType.Unknown;
		public String[] Names = null;
		
		private EKeyword( EType type )
		{
			Type = type;
			Names = new String[] { this.toString() };
		}
		
		private EKeyword( EType type, String ... names )
		{
			Type = type;
			Names = names;
		}
		
		public static EKeyword Match( String strTarget )
		{
			EKeyword[] keywords = values();
			for( EKeyword keyword : keywords )
			{
				for( String strName : keyword.Names )
				{
					if( strName.compareTo( strTarget ) == 0 )
					{
						return keyword;
					}
				}
			}

			return null;
		}
		
		public boolean IsMatch( String strTarget )
		{
			for( String strName : Names )
			{
				if( strName.compareTo( strTarget ) == 0 )
				{
					return true;
				}
			}
			
			return false;
		}
	};
	/*
	public enum ERegister
	{
		
		
		public static ERegister Match( String strName )
		{
			ERegister[] registers = values();
			for( ERegister register : registers )
			{
				if( register.toString().compareTo( strName ) == 0 )
				{
					return register;
				}
			}

			return null;
		}
	};
	*/
	/*
	public enum ERegisterPair
	{
		
		
		public static ERegisterPair Match( String strName )
		{
			ERegisterPair[] pairs = values();
			for( ERegisterPair pair : pairs )
			{
				if( pair.toString().compareTo( strName ) == 0 )
				{
					return pair;
				}
			}

			return null;
		}
	};
	*/
	/*
	public enum ECondition
	{
		C,NC,Z,NZ,P0,PE,P,M;
		
		public static ECondition Match( String strName )
		{
			ECondition[] conditions = values();
			for( ECondition condition : conditions )
			{
				if( condition.toString().compareTo( strName ) == 0 )
				{
					return condition;
				}
			}
			
			return null;
		}
	};
	*/
	
	/*
	public enum EOperator
	{
		Plus( "+"  ),
		Minus( "-" ),
		Mul( "*" ),
		Div( "/" ),
		Mod( "%" ),
		And( "&", "AND" ),
		Or( "|", "OR" ),
		Xor( "^", "XOR" ),
		Not( "~", "NOT" ),
		ShiftLeft( "<<" ),
		ShiftRight( ">>" );

		public String[] Names = null;
		
		private EOperator( String ... names )
		{
			Names = names;
		}
		
		public static EOperator Match( String strTarget )
		{
			EOperator[] operators = values();
			for( EOperator operator : operators )
			{
				for( String strName : operator.Names )
				{
					if( strName.compareTo( strTarget ) == 0 )
					{
						return operator;
					}
				}
			}
			
			return null;
		}
	};
	*/
	
	/*
	public enum EPseudoCode
	{
		Equal( "=", "EQU" ),
		ByteData( "DEFB", "DB" ),
		WordData( "DEFW", "DW" ),
		StringData( "DEFS", "DS" ),
		Origin( "ORG" );
		
		public String[] Names = null;

		private EPseudoCode( String ... names )
		{
			Names = names;
		}
		
		public static EPseudoCode Match( String strTarget )
		{
			EPseudoCode[] pseudoCodes = values();
			for( EPseudoCode pseudoCode : pseudoCodes )
			{
				for( String strName : pseudoCode.Names )
				{
					if( strName.compareTo( strTarget ) == 0 )
					{
						return pseudoCode;
					}
				}
			}

			return null;
		}
	};
	*/
	
	/*
	public ERegister Register = null;
	public ERegisterPair RegisterPair = null;
	public EOperator Operator = null;
	public EOpcode Opcode = null;
	public EPseudoCode PseudoCode = null;
	*/
	
	public EKeyword Keyword = null;
	
	public Token(String strName, int nLine, int nDepth)
	{
		Name = strName;
		Line = nLine;
		Depth = nDepth;
		
		if( IsDecimal() )
		{
			Type = EType.Number;
		}
		else if( IsHexadecimal() )
		{
			Type = EType.Hex;
		}
		else if( IsBinary() )
		{
			Type = EType.Bin;
		}
		else if( IsString() )
		{
			Type = EType.String;
		}
		else if( (Keyword = EKeyword.Match( Name )) != null )
		{
			Type = Keyword.Type;
		}
		
		/*
		else if( IsRegisterPairs() )
		{
			Type = EType.RegisterPair;
		}
		else if( IsOperator() )
		{
			Type = EType.Operator;
		}
		else if( IsCondition() )
		{
			Type = EType.Condition;
		}
		else if( (Opcode = EOpcode.Match( Name )) != null )
		{
			Type = EType.Opcode;
		}
		else if( IsPseudoCode() )
		{
			Type = EType.PseudoCode;
		}
		*/
		else if( IsComma() )
		{
			Type = EType.Comma;
		}
		else if( IsLabelSuffix() )
		{
			Type = EType.LabelSuffix;
		}
		else if( IsParenthesesOpen() )
		{
			Type = EType.ParenthesesOpen;
		}
		else if( IsParenthesesClose() )
		{
			Type = EType.ParenthesesClose;
		}
		else if( IsUserKeyword() )
		{
			Type = EType.UserKeyword;
		}
	}
	
	private static final char[] m_arrayDecimals = "0123456789".toCharArray();
	public boolean IsDecimal()
	{
		return IsMatchGroup( Name, m_arrayDecimals );
	}
	
	private static final char[] m_arrayHexadecimals = "0123456789abcdefABCDEF".toCharArray();
	public boolean IsHexadecimal()
	{
		if( IsMatchFirst( Name, "0x" ) )
		{
			return IsMatchGroup( Name.substring(2), m_arrayHexadecimals );
		}
		else if( IsMatchFirst( Name, "$" ) )
		{
			return IsMatchGroup( Name.substring(1), m_arrayHexadecimals );
		}
		else if( IsMatchLast( Name.toUpperCase(), "H" ) )
		{
			return IsMatchGroup( Name.substring( 0, Name.length() - 1 ), m_arrayHexadecimals );
		}
		else
		{
			return false;
		}
	}
	
	private static final char[] m_arrayBinaries = "01".toCharArray();
	public boolean IsBinary()
	{
		if( IsMatchFirst( Name, "0b" ) )
		{
			return IsMatchGroup( Name.substring(2), m_arrayBinaries);
		}
		else if( IsMatchLast( Name.toUpperCase(), "B" )  )
		{
			return IsMatchGroup( Name.substring( Name.length() - 1 ), m_arrayBinaries );
		}
		else
		{
			return false;
		}
	}
	
	private static final char[] m_arrayUserKeywordFirst =
		"@_abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
	private static final char[] m_arrayUserKeyword =
		"@_abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();
	public boolean IsUserKeyword()
	{
		if( Name.length() <= 0 )
		{
			return false;
		}
		
		if( IsMatchGroup( Name.charAt(0), m_arrayUserKeywordFirst ) == false )
		{
			return false;
		}
		
		if( Name.length() <= 1 )

		{
			return true;
		}
		else
		{
			return IsMatchGroup( Name.substring(1), m_arrayUserKeyword );
		}
	}
	
	public boolean IsMatchGroup( char target, char[] arrayGroup )

	{
		for( char c : m_arrayUserKeywordFirst )
		{
			if( c == target )
			{
				return true;
			}
		}
		
		return false;
	}
	
	
	public boolean IsString()
	{
		if( IsMatchFirst( Name, "\"" ) && IsMatchLast( Name, "\"" ) )
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	public boolean IsComma()
	{
		return IsMatch( Name, "," ); 
	}
	
	public boolean IsLabelSuffix()
	{
		return IsMatch( Name, ":" ); 
	}
	
	public boolean IsParenthesesOpen()
	{
		return IsMatch( Name, "(" );
	}
	
	
	public boolean IsParenthesesClose()
	{
		return IsMatch( Name, ")" );
	}

	private static final boolean IsMatch( String strTarget, String strMatch )
	{
		if( strTarget.compareTo( strMatch ) == 0 )
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	private boolean IsMatchAny( String strTarget, String[] arrayItems )
	{

		for( String strItem : arrayItems )
		{
			if( strTarget.compareTo( strItem ) == 0 )
			{
				return true;
			}
		}
		
		return false;
	}
	
	private static final boolean IsMatchFirst( String strTarget, String strFirst )
	{
		if( strTarget.length() < strFirst.length() )
		{
			return false;
		}
		
		if( strTarget.substring( 0, strFirst.length() ).compareTo( strFirst ) == 0 )
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	public static final boolean IsMatchLast( String strTarget, String strLast )
	{
		if( strTarget.length() < strLast.length() )
		{
			return false;
		}

		if( strTarget.substring( strTarget.length() - strLast.length() ).compareTo( strLast ) == 0 )
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	private static final boolean IsMatchGroup( String strTarget, char[] arrayGroup )
	{
		char[] arrayTarget = strTarget.toCharArray();
		// = strGroup.toCharArray();
		for( char tc : arrayTarget )
		{
			boolean isMatch = false;
			for( char gc : arrayGroup )
			{
				if( tc == gc )
				{
					isMatch = true;
					break;
				}
			}
			
			if( isMatch == false )
			{
				return false;
			}
		}
		
		return true;
	}
}
