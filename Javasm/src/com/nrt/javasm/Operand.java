package com.nrt.javasm;

import java.util.List;
import java.util.ArrayList;

public class Operand
{
	public enum EType
	{
		Unknown,
		Immediate, // n      = １バイト(0～255)の数値
		//nn, //nn     = ２バイト(0～65535)の数値
		Register, //r      = B, C, D, E, H, L, A のいずれか
		RegisterPair, //rr     = BC, DE, HL, SP のいずれか
		RegisterX, //xx     = BC, DE, IX, SP のいずれか
		RegisterY, //yy     = BC, DE, IY, SP のいずれか
		//adr, //adr    = アドレス(0～65535)
		//e, //e      = 相対ジャンプオフセット(-128～+127)
		//b, //b      = ビット番号。オペランド.b はオペランドの任意ビットを示す
		Condition, //cc     = ジャンプ命令の条件。詳細は各命令の説明を参照
		AddressExpression, // (adr)  = メモリ。adrはアドレス(0～65535)
		AddressHL, //(HL)   = メモリ。HLレジスタの値がアドレス
		AddressBC, //(BC)   = メモリ。BCレジスタの値がアドレス
		AddressDE, //(DE)   = メモリ。DEレジスタの値がアドレス

		AddressIXd, //(IX+d) = メモリ。IXレジスタにdを足した値がアドレス dは-128～+127のオフセット数値

		AddressIYd, //(IY+d) = メモリ。IYレジスタにdを足した値がアドレス dは-128～+127のオフセット数値
		//_p_,	//(p)    = I/Oポート。pはI/Oアドレス(0～255)
		PortC, //(c)
	}

	public List<Token> Tokens = new ArrayList<Token>();
	
	public EType DetectType()
	{
		return EType.Unknown;
	}
	
	
	
	
	
	/*
	public List<Token> Tokens = new ArrayList<Token>();

	public boolean IsMatch(String... arrayNames)
	{
		if (Tokens.size() != arrayNames.length)
		{
			return false;
		}
		else
		{
			return IsMatchFirst(arrayNames);
		}
	}

	public boolean IsMatchFirst(String... arrayNames)
	{
		if (Tokens.size() < arrayNames.length)
		{
			return false;
		}

		for (int i = 0 ; i < arrayNames.length ; i++)
		{
			if (Tokens.get(i).Name.compareTo(arrayNames[i]) != 0)
			{
				return false;
			}
		}

		return true;
	}

	public enum Register
	{
		r,
		rr,
		xx,
		yy,
	}

	public static String[] Registers = { "A", "B", "C", "D", "E", "H", "L" };
	public static String[] PairRegisters = { "BC", "DE", "HL", "SP" };
	public static String[] IXRegisters = { "BC", "DE", "IX", "SP" };
	public static String[] IYRegisters = { "BC", "DE", "IY", "SP" };

	public boolean IsRegister(Register eRegister)
	{
		String[] arrayRegisters = null;
		switch (eRegister)
		{
			case r:
				arrayRegisters = Registers;
				break;

			case rr:
				arrayRegisters = PairRegisters;
				break;

			case xx:
				arrayRegisters = IXRegisters;
				break;

			case yy:
				arrayRegisters = IYRegisters;
				break;
		}

		for (String strRegister : arrayRegisters)
		{
			if (IsMatch(strRegister))
			{
				return true;
			}
		}
		return false;
	}

	public boolean IsAddressReference()
	{
		if (Tokens.size() < 3)
		{
			return false;
		}

		Token token = Tokens.get(0);
		if (token.Name.compareTo("(") == 0 && token.Depth != 0)
		{
			return false;
		}

		int iLast = Tokens.size() - 1;
		for (int i = 1 ; i < iLast ; i++)
		{
			token = Tokens.get(i);
			if (token.Name.compareTo(")") == 0 &&
				token.Depth == 0)
			{
				return false;
			}
		}

		token = Tokens.get(iLast);
		if (token.Name.compareTo(")") == 0 && token.Depth == 0)
		{
			return true;
		}
		return false;
	}
	*/
}

