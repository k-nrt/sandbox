package com.nrt.javasm;
import java.util.*;
import dalvik.bytecode.*;

public class Instruction
{
	public class Binary
	{
		public enum EType
		{
			Code("00"),
			Imm8("nn"),
			Imm16L("ll"),
			Imm16H("hh"),
			Relative("ee"),
			Port("p"),
			Offset("d");
			
			public String Name = null;
			
			private EType( String strName )
			{
				Name = strName;
			}
		};
		
		public EType Type = EType.Code;
		public String  Value = "";
		
		public Binary( String value )
		{
			Type = EType.Code;
			Value = value;
		}
		
		public Binary( EType type )
		{
			Type = type;
			Value = type.Name;
		}
	}
	
	private enum EImmediate
	{
		n( Binary.EType.Imm8 ),
		nn( Binary.EType.Imm16L, Binary.EType.Imm16H ),
		e( Binary.EType.Relative ),
		p( Binary.EType.Port ),
		d( Binary.EType.Offset );

		public Binary.EType[] Types = null;

		private EImmediate(Binary.EType ... arrayTypes )
		{
			Types = arrayTypes;
		}

		public final static EImmediate Match( String strTarget )
		{
			EImmediate[] immediates = values();
			for( EImmediate immediate : immediates )
			{
				if( strTarget.compareTo( immediate.toString()) == 0 )
				{
					return immediate;
				}
			}

			return null;
		}
	}
	
	public enum EOperand
	{		
		HLa("(HL)",EType.RegisterAddress, new KeywordParser( "(", "HL", ")" ) ), //(HL)   = メモリ。HLレジスタの値がアドレス
		BCa("(BC)",EType.RegisterAddress, new KeywordParser( "(", "BC", ")" ) ), //(BC)   = メモリ。BCレジスタの値がアドレス
		DEa("(DE)",EType.RegisterAddress, new KeywordParser( "(", "DE", ")" ) ), //(DE)   = メモリ。DEレジスタの値がアドレス
		IXa("(IX)",EType.RegisterAddress, new KeywordParser( "(", "IX", ")" ) ),
		IYa("(IY)",EType.RegisterAddress, new KeywordParser( "(", "IY", ")" ) ),
		SPa("(SP)",EType.RegisterAddress, new KeywordParser( "(", "SP", ")" ) ),
		Cp("(C)",EType.RegisterPort, new KeywordParser( "(", "C", ")" ) ),

		IXda("(IX+d)",EType.RegisterOffsetAddress, new IndexRegisterAddressParser( Token.EKeyword.IX ) ), //(IX+d) = メモリ。IXレジスタにdを足した値がアドレス dは-128～+127のオフセット数値
		IYda("(IY+d)",EType.RegisterOffsetAddress, new IndexRegisterAddressParser( Token.EKeyword.IY ) ), //(IY+d) = メモリ。IYレジスタにdを足した値がアドレス dは-128～+127のオフセット数値
		
		A(EType.Register, new KeywordParser( "A" ) ),
		B(EType.Register, new KeywordParser( "B" ) ),
		C(EType.Register, new KeywordParser( "C" ) ),
		D(EType.Register, new KeywordParser( "D" ) ),
		E(EType.Register, new KeywordParser( "E" ) ),
		H(EType.Register, new KeywordParser( "H" ) ),
		L(EType.Register, new KeywordParser( "L" ) ),
		I(EType.Register, new KeywordParser( "I" ) ),
		R(EType.Register, new KeywordParser( "R" ) ),
		
		AF(EType.RegisterPair, new KeywordParser( "AF" ) ),
		BC(EType.RegisterPair, new KeywordParser( "BC" ) ),
		DE(EType.RegisterPair, new KeywordParser( "DE" ) ),
		HL(EType.RegisterPair, new KeywordParser( "HL" ) ),
		IX(EType.RegisterPair, new KeywordParser( "IX" ) ),
		IY(EType.RegisterPair, new KeywordParser( "IY" ) ),
		SP(EType.RegisterPair, new KeywordParser( "SP" ) ),
		AFx("AF'",EType.RegisterPair, new KeywordParser( "AF'" ) ),

		NZ(EType.Condition, new KeywordParser( "NZ" ) ),
		Z(EType.Condition, new KeywordParser( "Z" ) ),
		NC(EType.Condition, new KeywordParser( "NC" ) ),
		//C(EType.Condition),
		PO(EType.Condition, new KeywordParser( "PO" ) ),
		PE(EType.Condition, new KeywordParser( "PE" ) ),
		P(EType.Condition, new KeywordParser( "P" ) ),
		M(EType.Condition, new KeywordParser( "M" ) ),
		
		p("(p)",EType.Exp8Port, new ExpressionAddressParser()),
		nna("(nn)",EType.Exp16Address, new ExpressionAddressParser() ),
		
		_0("0",EType.BitPos, new ExpressionParser(ExpressionParser.EType.RestrictNumber,0)),
		_1("1",EType.BitPos, new ExpressionParser(ExpressionParser.EType.RestrictNumber,1)),
		_2("2",EType.BitPos, new ExpressionParser(ExpressionParser.EType.RestrictNumber,2)),
		_3("3",EType.BitPos, new ExpressionParser(ExpressionParser.EType.RestrictNumber,3)),
		_4("4",EType.BitPos, new ExpressionParser(ExpressionParser.EType.RestrictNumber,4)),
		_5("5",EType.BitPos, new ExpressionParser(ExpressionParser.EType.RestrictNumber,5)),
		_6("6",EType.BitPos, new ExpressionParser(ExpressionParser.EType.RestrictNumber,6)),
		_7("7",EType.BitPos, new ExpressionParser(ExpressionParser.EType.RestrictNumber,7)),		
		
		_00H("00H",EType.RstPos, new ExpressionParser(ExpressionParser.EType.RestrictNumber,0x00)),
		_08H("08H",EType.RstPos, new ExpressionParser(ExpressionParser.EType.RestrictNumber,0x08)),
		_10H("10H",EType.RstPos, new ExpressionParser(ExpressionParser.EType.RestrictNumber,0x10)),
		_18H("18H",EType.RstPos, new ExpressionParser(ExpressionParser.EType.RestrictNumber,0x18)),
		_20H("20H",EType.RstPos, new ExpressionParser(ExpressionParser.EType.RestrictNumber,0x20)),
		_28H("28H",EType.RstPos, new ExpressionParser(ExpressionParser.EType.RestrictNumber,0x28)),
		_30H("30H",EType.RstPos, new ExpressionParser(ExpressionParser.EType.RestrictNumber,0x30)),
		_38H("38H",EType.RstPos, new ExpressionParser(ExpressionParser.EType.RestrictNumber,0x38)),
		
		n(EType.Exp8, new ExpressionParser(ExpressionParser.EType.Immediate8,0) ), // n      = １バイト(0～255)の数値
		e(EType.Exp8RelativeAddress, new ExpressionParser(ExpressionParser.EType.RelativeAddress,0)),		
		nn(EType.Exp16,new ExpressionParser(ExpressionParser.EType.Immediate16,0)); //nn, //nn     = ２バイト(0～65535)の数値
		
		public enum EType
		{
			Register,
			RegisterPair,
			RegisterAddress,
			RegisterOffsetAddress,
			RegisterPort,
			Condition,
			Exp8,
			Exp8Port,
			Exp8RelativeAddress,
			Exp16,
			Exp16Address,
			BitPos,
			RstPos,
		};
		
		public String Name = null;
		
		public EType Type = null;
		
		public OperandParser Parser = null;
		
		private EOperand( EType eType )
		{
			Name = toString();
			Type = eType;
		}
		
		private EOperand( EType eType, OperandParser parser )
		{
			Name = toString();
			Type = eType;
			Parser = parser;
		}
		
		private EOperand( String strName, EType eType )
		{
			Name = strName;
			Type = eType;
		}
		
		private EOperand( String strName, EType eType, OperandParser parser  )
		{
			Name = strName;
			Type = eType;
			Parser = parser;
		}
		
		public final static EOperand Convert( String strTarget )
		{
			for( EOperand eOperand : values() )
			{
				if( strTarget.compareTo( eOperand.Name ) == 0 )
				{
					return eOperand;
				}
			}
			return null;
		}
	};
	
	public Binary[] Binaries = new Binary[0];
	public String Opcode = "";
	
	public EOperand[] Operands = new EOperand[0];
	public boolean Undefined = true;
	
	public Instruction()
	{
		
	}
	
	public Instruction( String strLine )
	{
		String[] arrayTokens = Tokenize( strLine );
		
		List<Binary> listBinaries = new ArrayList<Binary>();
		List<EOperand> listOperands = new ArrayList<EOperand>();
		
		boolean isBinary = true;
		for( String strToken : arrayTokens )
		{
			if( isBinary )
			{
				EImmediate immediate = EImmediate.Match( strToken );
				if( immediate != null )
				{
					for( Binary.EType eType : immediate.Types )
					{
						listBinaries.add( new Binary( eType ) );
					}
				}
				else if( IsMatch( Opcodes, strToken ) )
				{
					Opcode = strToken;
					isBinary = false;
					Undefined = false;
					//listBinaries.add( new strToken );
				}
				else
				{
					listBinaries.add( new Binary( strToken ) );
				}				
			}
			else
			{
				EOperand eOperand = EOperand.Convert( strToken );
				
				if( eOperand == null )
				{
					isBinary = true;
					break;
				}
				else
				{
					listOperands.add( EOperand.Convert( strToken ) );			
				}
			}
		}
		if( isBinary )
		{
			Undefined = true;
			Opcode = strLine;
		}
		else
		{
			Binaries = listBinaries.toArray( new Binary[listBinaries.size()] );
			Operands = listOperands.toArray( new EOperand[listOperands.size()] );
		}
	}
	
	public String Dump()
	{
		String strResult = Opcode;
		
		if( Undefined )
		{
			strResult += " undefined";
		}
		else
		{
		
			strResult += " ";
		
			for( EOperand eOperand : Operands )
			{
				if( eOperand != null )
				{
					strResult += eOperand.toString();
					strResult += " ";
					
					if( eOperand.Parser != null )
					{
						//Token.EKeyword[] keywords = ((KeywordParser)eOperand.Parser).Keywords;
						//for( 

						strResult += "[" + eOperand.Parser.Dump() + "] ";
					}
				}
				else
				{
					strResult += "unknown ";
				}
			
				
				
			}
		
			strResult += ":";
			for( Binary binary : Binaries )
			{
				strResult += binary.Value;
				strResult += " ";			
			}
		}
		
		return strResult;		
	}
	
	private final boolean IsMatch( String[] arrayList, String strTarget )
	{
		for( String strList : arrayList )
		{
			if( strTarget.compareTo( strList ) == 0 )
			{
				return true;
			}
		}
		
		return false;
	}


	/*
	private final static Map<String,Binary[]> im = CreateImmediates();
	
	private final static Map<String,Binary[]> CreateImmediates()
	{
		Map<String,Binary[]> map = new HashMap<String, Binary[]>();
		
		map.add( "n", new Binary[]{ new}( Binary.EType.Imm8 ) );
		return map;
	}
	*/
	
	
	
	private final static String[] Opcodes = FindOpcodes();
	
	
	public static String Separators = " \n\t,";	
	public final static String[] Tokenize(String strLine )
	{
		List<String> listResult = new ArrayList<String>();
		String strToken = "";
		for( int j = 0 ; j < strLine.length() ; j++)
		{
			char c = strLine.charAt(j);
			if(c == ';')
			{
				//. comment.
				break;
			}
			else if (Contains(c, Separators))
			{
				//. separator.
				AddToken( listResult, strToken );
				//listResult.add( strToken );
				strToken = "";
			}
			/*
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
			*/
			else
			{
				strToken += c;
			}
		}

		AddToken( listResult, strToken );
		String[] arrayResult = new String[listResult.size()];
		return listResult.toArray( arrayResult );		
	}
		
	private final static boolean Contains(char c, String strTarget)
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
	
	private final static void AddToken( List<String> listTarget, String strTarget )
	{
		if( strTarget.compareTo( "" ) == 0 )
		{
			return;
		}
		else
		{
			listTarget.add( strTarget );
		}
	}
	
	private final static String[] FindOpcodes()
	{
		List<String> listResult = new ArrayList<String>();
		for( Token.EKeyword eKeyword : Token.EKeyword.values() )
		{
			if( eKeyword.Type == Token.EType.Opcode )
			{
				listResult.add( eKeyword.name() );
			}
		}
		
		return listResult.toArray( new String[listResult.size()] );
	}
	/*
	public enum EOperand
	{
		exp, // n      = １バイト(0～255)の数値
		//nn, //nn     = ２バイト(0～65535)の数値
		r, //r      = B, C, D, E, H, L, A のいずれか
		rr, //rr     = BC, DE, HL, SP のいずれか
		xx, //xx     = BC, DE, IX, SP のいずれか
		yy, //yy     = BC, DE, IY, SP のいずれか
		//adr, //adr    = アドレス(0～65535)
		//e, //e      = 相対ジャンプオフセット(-128～+127)
		//b, //b      = ビット番号。オペランド.b はオペランドの任意ビットを示す
		cond, //cc     = ジャンプ命令の条件。詳細は各命令の説明を参照
		_exp_, // (adr)  = メモリ。adrはアドレス(0～65535)
		_hl_, //(HL)   = メモリ。HLレジスタの値がアドレス
		_bc_, //(BC)   = メモリ。BCレジスタの値がアドレス

		_de_, //(DE)   = メモリ。DEレジスタの値がアドレス

		_ix_d_, //(IX+d) = メモリ。IXレジスタにdを足した値がアドレス dは-128～+127のオフセット数値

		_iy_d_, //(IY+d) = メモリ。IYレジスタにdを足した値がアドレス dは-128～+127のオフセット数値
		_ix_,
		_iy_,
		//_p_,	//(p)    = I/Oポート。pはI/Oアドレス(0～255)
		_c_,
		A,
		I,
		R,
		AF,
		AFx,
		BC,
		DE,
		HL,
		IX,
		IY,
		SP,
		SPa,
		NZ,
		Z,
		NC,
		C,
		PO,
		PE,
		P,
		M,
		_00H,
		_08H,
		_10H,
		_18H,
		_20H,
		_28H,
		_30H,
		_38H,
	};
	
	private static final Token.EKeyword ld = Token.EKeyword.LD;
	private static final Token.EKeyword push = Token.EKeyword.PUSH;
	private static final Token.EKeyword pop = Token.EKeyword.POP;
	private static final Token.EKeyword ex = Token.EKeyword.EX;
	private static final Token.EKeyword add = Token.EKeyword.ADD;
	private static final Token.EKeyword adc = Token.EKeyword.ADC;
	private static final Token.EKeyword sub = Token.EKeyword.SUB;
	private static final Token.EKeyword sbc = Token.EKeyword.SBC;
	private static final Token.EKeyword and = Token.EKeyword.AND;
	private static final Token.EKeyword xor = Token.EKeyword.XOR;
	private static final Token.EKeyword or = Token.EKeyword.OR;
	private static final Token.EKeyword cp = Token.EKeyword.CP;
	private static final Token.EKeyword inc = Token.EKeyword.INC;
	private static final Token.EKeyword dec = Token.EKeyword.DEC;
	private static final Token.EKeyword jp = Token.EKeyword.JP;
	private static final Token.EKeyword jr = Token.EKeyword.JR;
	private static final Token.EKeyword call = Token.EKeyword.CALL;
	private static final Token.EKeyword ret = Token.EKeyword.RET;
	private static final Token.EKeyword rst = Token.EKeyword.RST;
	private static final Token.EKeyword rlc = Token.EKeyword.RLC;
	private static final Token.EKeyword rrc = Token.EKeyword.RRC;
	private static final Token.EKeyword rl = Token.EKeyword.RL;
	private static final Token.EKeyword RR = Token.EKeyword.RR;
	private static final Token.EKeyword sla = Token.EKeyword.SLA;
	private static final Token.EKeyword sra = Token.EKeyword.SRA;
	private static final Token.EKeyword srl = Token.EKeyword.SRL;
	private static final Token.EKeyword bit = Token.EKeyword.BIT;
	private static final Token.EKeyword set = Token.EKeyword.SET;
	private static final Token.EKeyword res = Token.EKeyword.RES;
	
	
	private static final EOperand r = EOperand.r;
	private static final EOperand n = EOperand.exp;
	private static final EOperand rr = EOperand.rr;
	private static final EOperand HLa = EOperand._hl_;
	private static final EOperand IXda = EOperand._ix_d_;
	private static final EOperand IYda = EOperand._iy_d_;
	private static final EOperand IXa = EOperand._ix_;
	private static final EOperand IYa = EOperand._iy_;
	private static final EOperand BCa  = EOperand._bc_;
	private static final EOperand DEa  = EOperand._de_;
	private static final EOperand A = EOperand.A;
	private static final EOperand I = EOperand.I;
	private static final EOperand R = EOperand.R;
	private static final EOperand adr = EOperand._exp_;
	private static final EOperand nn = EOperand.exp;
	private static final EOperand AF = EOperand.AF;
	private static final EOperand AFx = EOperand.AFx;
	private static final EOperand BC = EOperand.BC;
	private static final EOperand DE = EOperand.DE;
	private static final EOperand HL = EOperand.HL;
	private static final EOperand IX = EOperand.IX;
	private static final EOperand IY = EOperand.IY;
	private static final EOperand SP = EOperand.SP;
	private static final EOperand SPa = EOperand.SPa;
	private static final EOperand adrj = EOperand.exp;
	
	private static final EOperand NZ = EOperand.NZ;
	private static final EOperand Z = EOperand.Z;
	private static final EOperand NC = EOperand.NC;
	private static final EOperand C = EOperand.C;
	private static final EOperand PO = EOperand.PO;
	private static final EOperand PE = EOperand.PE;
	private static final EOperand P = EOperand.P;
	private static final EOperand M = EOperand.M;
	
	private static final EOperand e = EOperand.exp;
	private static final EOperand b = EOperand.exp;
	
	
	public enum EType
	{
		//. 8ビット同士の組合わせ       命令バイト数
		LD_r_n(ld,r,n,2),  //LD    r,n               2
		LD_r_r(ld,r,r,1),  //LD    r,r               1
		LD_hla_n(ld,HLa,n,2),//LD    (HL),n            2
		LD_hla_r(ld,HLa,r,1),// LD    (HL),r            1
		LD_r_hla(ld,r,HLa,1),//LD    r,(HL)            1
		LD_ixda_n(ld,IXda,n,4),//LD    (IX+d),n          4
		LD_ixda_r(ld,IXda,r,3),// LD    (IX+d),r          3
		LD_r_ixda(ld,r,IXda,3),//LD    r,(IX+d)          3
		LD_iyda_n(ld,IYda,n,4),//LD    (IY+d),n          4
		LD_iyda_r(ld,IYda,r,3),// LD    (IY+d),r          3
		LD_r_iyda(ld,r,IYda,3),//LD    r,(IY+d)          3
		LD_bca_a(ld,BCa,A,1),//LD    (BC),A            1
		LD_a_bca(ld,A,BCa,1),// LD    A,(BC)            1
		LD_dea_a(ld,DEa,A,1),// LD    (DE),A            1
		LD_a_dea(ld,A,DEa,1),// LD    A,(DE)            1
		LD_adr_a(ld,adr,A,3),// LD    (adr),A           3
		LD_a_adr(ld,A,adr,3),// LD    A,(adr)           3
		LD_i_a(ld,I,A,2),//LD    I,A               2
		LD_a_i(ld,A,I,2),//    A,I               2
		LD_r_i(ld,R,A,2),//LD    R,A               2
		LD_a_r(ld,A,R,2),// LD    A,R               2
		
		//. 16ビット同士の組合わせ      命令バイト数
		LD_rr_nn(ld,rr,nn,3), //. LD    rr,nn             3
		LD_IX_nn(ld,IX,nn,4), //. LD    IX,nn             4
		LD_IY_nn(ld,IY,nn,4), //. LD    IY,nn             4
		LD_adr_HL(ld,adr,HL,3), //. LD    (adr),HL          3
		LD_HL_adr(ld,HL,adr,3), //. LD    HL,(adr)          3
		LD_adr_rr(ld,adr,rr,4), //. LD    (adr),rr          4
		LD_rr_adr(ld,rr,adr,4), //. LD    rr,(adr)          4
		LD_adr_IX(ld,adr,IX,4), //. LD    (adr),IX          4
		LD_IX_adr(ld,IX,adr,4), //. LD    IX,(adr)          4
		LD_adr_IY(ld,adr,IY,4), //. LD    (adr),IY          4
		LD_IY_adr(ld,IY,adr,4), //. LD    IY,(adr)          4
		LD_SP_HL(ld,SP,HL,1), //. LD    SP,HL             1
		LD_SP_IX(ld,SP,IX,2), //. LD    SP,IX             2
		LD_SP_IY(ld,SP,IY,2), //. LD    SP,IY             2
		
		//. PUSH/POP.
		PUSH_BC(push,BC,1), //. PUSH  BC                1
		PUSH_DE(push,DE,1), //. PUSH  DE                1
		PUSH_HL(push,HL,1), //. PUSH  HL                1
		PUSH_AF(push,AF,1), //. PUSH  AF                1
		PUSH_IX(push,IX,2), //. PUSH  IX                2
		PUSH_IY(push,IY,2), //. PUSH  IY                2

		POP_BC(pop,BC,1), //. POP   BC                1
		POP_DE(pop,DE,1), //. POP   DE                1
		POP_HL(pop,HL,1), //. POP   HL                1
		POP_AF(pop,AF,1), //. POP   AF                1
		POP_IX(pop,IX,2), //. POP   IX                2
		POP_IY(pop,IY,2), //. POP   IY                2
		
		//. EX/EXX.
		EX_DE_HL(ex,DE,HL,1), //. EX    DE,HL             1
		EX_AF_AFx(ex,AF,AFx,1), //. EX    AF,AF'            1
		EX_SPa_HL(ex,SPa,HL,1), //. EX    (SP),HL           1
		EX_SPa_IX(ex,SPa,IX,2), //. EX    (SP),IX           2
		EX_SPa_IY(ex,SPa,IY,2), //. EX    (SP),IY           2
		EXX(Token.EKeyword.EXX,1), //. EXX                     1
		
		//. LDIR/CPIR.
		LDIR(Token.EKeyword.LDIR,2), //. LDIR                    2
		LDDR(Token.EKeyword.LDDR,2), //. LDDR                    2
		LDI(Token.EKeyword.LDI,2), //. LDI                     2
		LDD(Token.EKeyword.LDD,2), //. LDD                     2

		CPIR(Token.EKeyword.CPIR,2), //. CPIR                    2
		CPDR(Token.EKeyword.CPDR,2), //. CPDR                    2
		CPI(Token.EKeyword.CPI,2), //. CPI                     2
		CPD(Token.EKeyword.CPD,2), //. CPD                     2
		
		//. ADD/ADC.
		ADD_A_n(add,A,n,2), //. ADD   A,n               2
		ADD_A_r(add,A,r,1), //. ADD   A,r               1
		ADD_A_HLa(add,A,HLa,1), //. ADD   A,(HL)            1
		ADD_A_IXda(add,A,IXda,3), //. ADD   A,(IX+d)          3
		ADD_A_IYda(add,A,IYda,3), //. ADD   A,(IY+d)          3

		ADC_A_n(adc,A,n,2), //. ADC   A,n               2
		ADC_A_r(adc,A,r,1), //. ADC   A,r               1
		ADC_A_HLa(adc,A,HLa,1), //. ADC   A,(HL)            1
		ADC_A_IXda(adc,A,IXda,3), //. ADC   A,(IX+d)          3
		ADC_A_IYda(adc,A,IYda,3), //. ADC   A,(IY+d)          3
		
		//. SUB/SBC.
		SUB_n(sub,n,2), //. SUB   n                 2
		SUB_r(sub,r,1), //. SUB   r                 1
		SUB_HLa(sub,HLa,1), //. SUB   (HL)              1
		SUB_IXda(sub,IXda,3), //. SUB   (IX+d)            3
		SUB_IYda(sub,IYda,3), //. SUB   (IY+d)            3

		SBC_A_n(sbc,A,n,2), //. SBC   A,n               2
		SBC_A_r(sbc,A,r,1), //. SBC   A,r               1
		SBC_A_HLa(sbc,A,HLa,1), //. SBC   A,(HL)            1
		SBC_A_IXda(sbc,A,IXda,3), //. SBC   A,(IX+d)          3
		SBC_A_IYda(sbc,A,IYda,3), //. SBC   A,(IY+d)          3
		
		//. AND/XOR/OR.
		AND_n(and,n,2), //. AND   n                 2
		AND_r(and,r,1), //. AND   r                 1
		AND_HLa(and,HLa,1), //. AND   (HL)              1
		AND_IXda(and,IXda,3), //. AND   (IX+d)            3
		AND_IYda(and,IYda,3), //. AND   (IY+d)            3

		XOR_n(xor,n,2), //. XOR   n                 2
		XOR_r(xor,r,1), //. XOR   r                 1
		XOR_HLa(xor,HLa,1), //. XOR   (HL)              1
		XOR_IXda(xor,IXda,3), //. XOR   (IX+d)            3
		XOR_IYda(xor,IYda,3), //. XOR   (IY+d)            3

		OR_n(or,n,2), //. OR    n                 2
		OR_r(or,r,1), //. OR    r                 1
		OR_HLa(or,HLa,1), //. OR    (HL)              1
		OR_IXda(or,IXda,3), //. OR    (IX+d)            3
		OR_IYda(or,IYda,3), //. OR    (IY+d)            3
		
		CP_n(cp,n,2), //. CP    n                 2
		CP_r(cp,r,1), //. CP    r                 1
		CP_HLa(cp,HLa,1), //. CP    (HL)              1
		CP_IXda(cp,IXda,3), //. CP    (IX+d)            3
		CP_IYda(cp,IYda,3), //. CP    (IY+d)            3

		CPL(Token.EKeyword.CPL,1), //. CPL                     1
		NEG(Token.EKeyword.NEG,2), //. NEG                     2
		DAA(Token.EKeyword.DAA,1), //. DAA                     1
		
		INC_r(cp,r,1), //. INC   r                 1
		INC_HLa(cp,HLa,1), //. INC   (HL)              1
		INC_IXda(cp,IXda,3), //. INC   (IX+d)            3
		INC_IYda(cp,IYda,3), //. INC   (IY+d)            3

		DEC_r(cp,r,1), //. DEC   r                 1
		DEC_HLa(cp,HLa,1), //. DEC   (HL)              1
		DEC_IXda(cp,IXda,3), //. DEC   (IX+d)            3
		DEC_IYda(cp,IYda,3), //. DEC   (IY+d)            3
		
		ADD_HL_BC(add,HL,BC,1), //. ADD   HL,BC             1
		ADD_HL_DE(add,HL,DE,1), //. ADD   HL,DE             1
		ADD_HL_HL(add,HL,HL,1), //. ADD   HL,HL             1
		ADD_HL_SP(add,HL,SP,1), //. ADD   HL,SP             1

		ADD_IX_BC(add,IX,BC,2), //. ADD   IX,BC             2
		ADD_IX_DE(add,IX,DE,2), //. ADD   IX,DE             2
		ADD_IX_IX(add,IX,IX,2), //. ADD   IX,IX             2
		ADD_IX_SP(add,IX,SP,2), //. ADD   IX,SP             2

		ADD_IY_BC(add,IY,BC,2), //. ADD   IY,BC             2
		ADD_IY_DE(add,IY,DE,2), //. ADD   IY,DE             2
		ADD_IY_IY(add,IY,IY,2), //. ADD   IY,IY             2
		ADD_IY_SP(add,IY,SP,2), //. ADD   IY,SP             2

		ADC_HL_BC(adc,HL,BC,2), //. ADC   HL,BC             2
		ADC_HL_DE(adc,HL,DE,2), //. ADC   HL,DE             2
		ADC_HL_HL(adc,HL,HL,2), //. ADC   HL,HL             2
		ADC_HL_SP(adc,HL,SP,2), //. ADC   HL,SP             2

		SBC_HL_BC(sbc,HL,BC,2), //. SBC   HL,BC             2
		SBC_HL_DE(sbc,HL,DE,2), //. SBC   HL,DE             2
		SBC_HL_HL(sbc,HL,HL,2), //. SBC   HL,HL             2
		SBC_HL_SP(sbc,HL,SP,2), //. SBC   HL,SP             2

		INC_BC(inc,BC,1), //. INC   BC                1
		INC_DE(inc,DE,1), //. INC   DE                1
		INC_HL(inc,HL,1), //. INC   HL                1
		INC_SP(inc,SP,1), //. INC   SP                1
		INC_IX(inc,IX,2), //. INC   IX                2
		INC_IY(inc,IY,2), //. INC   IY                2

		DEC_BC(dec,BC,1), //. DEC   BC                1
		DEC_DE(dec,DE,1), //. DEC   DE                1
		DEC_HL(dec,HL,1), //. DEC   HL                1
		DEC_SP(dec,SP,1), //. DEC   SP                1
		DEC_IX(dec,IX,2), //. DEC   IX                2
		DEC_IY(dec,IY,2), //. DEC   IY                2
		
		JP_adr(jp,adrj,3), //. JP    adr               3

		JP_NZ_adr(jp,NZ,adrj,3), //. JP    NZ,adr            3
		JP_Z_adr(jp,Z,adrj,3), //. JP    Z,adr             3
		JP_NC_adr(jp,NC,adrj,3), //. JP    NC,adr            3
		JP_C_adr(jp,C,adrj,3), //. JP    C,adr             3
		JP_PO_adr(jp,PO,adrj,3), //. JP    PO,adr            3
		JP_PE_adr(jp,PE,adrj,3), //. JP    PE,adr            3
		JP_P_adr(jp,P,adrj,3), //. JP    P,adr             3
		JP_M_adr(jp,M,adrj,3), //. JP    M,adr             3

		JP_HLa(jp,HLa,1), //. JP    (HL)              1
		JP_IXa(jp,IXa,2), //. JP    (IX)              2
		JP_IYa(jp,IYa,2), //. JP    (IY)              2

		JR_e(jr,e,2), //. JR    e                 2

		JR_NZ_e(jr,NZ,e,2), //. JR    NZ,adr            2
		JR_Z_e(jr,Z,e,2), //. JR    Z,adr             2
		JR_NC_e(jr,NC,e,2), //. JR    NC,adr            2
		JR_C_e(jr,C,e,2), //. JR    C,adr             2

		DJNZ_e(Token.EKeyword.DJNZ,e,2), //. DJNZ  e                 2
		
		
		CALL_adr(call,adr,3), //. CALL  adr               3
		CALL_NZ_adr(call,NZ,adr,3), //. CALL  NZ,adr            3
		CALL_Z_adr(call,Z,adr,3), //. CALL  Z,adr             3
		CALL_NC_adr(call,NC,adr,3), //. CALL  NC,adr            3
		CALL_C_adr(call,C,adr,3), //. CALL  C,adr             3
		CALL_PO_adr(call,PO,adr,3), //. CALL  PO,adr            3
		CALL_PE_adr(call,PE,adr,3), //. CALL  PE,adr            3
		CALL_P_adr(call,P,adr,3), //. CALL  P,adr             3
		CALL_M_adr(call,M,adr,3), //. CALL  M,adr             3

		RET(ret,1), //. RET                     1
		RET_NZ(ret,NZ,1), //. RET   NZ                1
		RET_Z(ret,Z,1), //. RET   Z                 1
		RET_NC(ret,NC,1), //. RET   NC                1
		RET_C(ret,C,1), //. RET   C                 1
		RET_PO(ret,PO,1), //. RET   PO                1
		RET_PE(ret,PE,1), //. RET   PE                1
		RET_P(ret,P,1), //. RET   P                 1
		RET_M(ret,M,1), //. RET   M                 1
		
		RETI(Token.EKeyword.RETI,2), //. RETI                    2
		RETN(Token.EKeyword.RETN,2), //. RETN                    2
		
		RST_00H(rst,EOperand._00H,1), //. RST   00H               1
		RST_08H(rst,EOperand._08H,1), //. RST   08H               1
		RST_10H(rst,EOperand._10H,1), //. RST   10H               1
		RST_18H(rst,EOperand._18H,1), //. RST   18H               1
		RST_20H(rst,EOperand._20H,1), //. RST   20H               1
		RST_28H(rst,EOperand._28H,1), //. RST   28H               1
		RST_30H(rst,EOperand._30H,1), //. RST   30H               1
		RST_38H(rst,EOperand._38H,1), //. RST   38H               1
		
		RLCA(Token.EKeyword.RLCA,1), //. RLCA                    1
		RRCA(Token.EKeyword.RRCA,1), //. RRCA                    1
		RLA(Token.EKeyword.RLA,1), //. RLA                     1
		RRA(Token.EKeyword.RRA,1), //. RRA                     1
		
		RLC_r(rlc,r,2), //. RLC     r               2
		RLC_HLa(rlc,HLa,2), //. RLC     (HL)            2
		RLC_IXda(rlc,IXda,2), //. RLC     (IX+d)          2
		RLC_IYda(rlc,IYda,2), //. RLC     (IY+d)          2

		RRC_r(rrc,r,2), //. RRC     r               2
		RRC_HLa(rrc,HLa,2), //. RRC     (HL)            2
		RRC_IXda(rrc,IXda,2), //. RRC     (IX+d)          2
		RRC_IYda(rrc,IYda,2), //. RRC     (IY+d)          2

		RL_r(rl,r,2), //. RL      r               2
		RL_HLa(rl,HLa,2), //. RL      (HL)            2
		RL_IXda(rl,IXda,2), //. RL      (IX+d)          2
		RL_IYda(rl,IYda,2), //. RL      (IY+d)          2

		RR_r(RR,r,2), //. RR      r               2
		RR_HLa(RR,HLa,2), //. RR      (HL)            2
		RR_IXda(RR,IXda,2), //. RR      (IX+d)          2
		RR_IYda(RR,IYda,2), //. RR      (IY+d)          2
		
		RLD(Token.EKeyword.RLD,2), //. RLD                     2
		RRD(Token.EKeyword.RLD,2), //. RRD                     2
	
		SLA_r(sla,r,2), //. SLA     r               2
		SLA_HLa(sla,HLa,2), //. SLA     (HL)            2
		SLA_IXda(sla,IXda,2), //. SLA     (IX+d)          2
		SLA_IYda(sla,IYda,2), //. SLA     (IY+d)          2

		SRA_r(sra,r,2), //. SRA     r               2
		SRA_HLa(sra,HLa,2), //. SRA     (HL)            2
		SRA_IXda(sra,IXda,2), //. SRA     (IX+d)          2
		SRA_IYda(sra,IYda,2), //. SRA     (IY+d)          2

		SRL_r(srl,r,2), //. SRL     r               2
		SRL_HLa(srl,HLa,2), //. SRL     (HL)            2
		SRL_IXda(srl,IXda,2), //. SRL     (IX+d)          2
		SRL_IYda(srl,IYda,2), //. SRL     (IY+d)          2
		
		BIT_b_r(bit,b,r,2), //. BIT   b,r               2
		BIT_b_HLa(bit,b,HLa,2), //. BIT   b,(HL)            2
		BIT_b_IXda(bit,b,IXda,4), //. BIT   b,(IX+d)          4
		BIT_b_IYda(bit,b,IYda,4), //. BIT   b,(IY+d)          4

		SET_b_r(set,b,r,2), //. SET   b,r               2
		SET_b_HLa(set,b,HLa,2), //. SET   b,(HL)            2
		SET_b_IXda(set,b,IXda,2), //. SET   b,(IX+d)          4
		SET_b_IYda(set,b,IYda,2), //. SET   b,(IY+d)          4

		RES_b_r(res,b,r,2), //. RES   b,r               2
		RES_b_HLa(res,b,HLa,2), //. RES   b,(HL)            2
		RES_b_IXda(res,b,IXda,2), //. RES   b,(IX+d)          4
		RES_b_IYda(res,b,IYda,2), //. RES   b,(IY+d)          4
		
		End(null,0);	
				
		public Token.EKeyword Keyword = null;
		public EOperand Operand0 = null;
		public EOperand Operand1 = null;
		public int Size = 0;
		
		private EType( Token.EKeyword eKeyword, int size )
		{
			Keyword = eKeyword;
			Size = size;
		}
		private EType( Token.EKeyword eKeyword, EOperand eOperand0, int size )
		{
			Keyword = eKeyword;
			Operand0 = eOperand0;
			Size = size;
		}
		private EType( Token.EKeyword eKeyword, EOperand eOperand0, EOperand eOperand1, int  size )
		{
			Keyword = eKeyword;
			Operand0 = eOperand0;
			Operand1 = eOperand1;			
			Size = size;
		}
		*/
	
	
	
	
	
}
