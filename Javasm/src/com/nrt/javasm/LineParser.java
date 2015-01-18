package com.nrt.javasm;
import java.util.List;
import java.util.ArrayList;

public class LineParser
{
	public List<Line> Lines = new ArrayList<Line>();

	public LineParser()
	{}
	
	public void Parse( List<Token> listTokens )
	{
		int nLine = -1;
		Line line = null;
		for (Token token : listTokens )
		{
			if (token.Line != nLine)
			{
				nLine = token.Line;

				AddLine(line);
				line = new Line(nLine);
			}

			line.Tokens.add(token);			
		}
		AddLine(line);
	}
	
	private void AddLine(Line line)
	{
		if (line != null)
		{
			line.Parse();
			Lines.add(line);
			
		}
	}
	
	public String DumpLines( String strBase )
	{
		for( Line line : Lines )
		{
			strBase += String.format( "LN[%06d] AD[%04x] ", line.LineNumber, line.Address );
			if( line.AddressLabel != null )
			{
				strBase += String.format( "L[%s] ", line.AddressLabel.Name );
			}
			
			if( line.ConstantLabel != null )
			{
				strBase += String.format( "C[%s] ", line.ConstantLabel.Name );
			}
			
			if( line.Opcode != null )
			{
				strBase += String.format( "OP[%s] ", line.Opcode.Name );
			}
			
			for( Expression exp : line.Expressions )
			{
				if( exp.Addressing == true )
				{
					strBase += "AD";	
				}
				else
				{
					strBase += "IM";
				}
				
				strBase += "{";
				for( Token token : exp.Tokens )
				{
					strBase += String.format( "E[%s] ", token.Name );
				}
				strBase += "} ";
			}
			
			if( line.Instruction != null )
			{
				strBase += " : ";
				
				
				strBase += line.Instruction.Opcode;
				
				if( line.Instruction.Operands != null )
				{
					for( Instruction.EOperand eOperand : line.Instruction.Operands )
					{
						strBase += " ";
						strBase += eOperand.name();
					
					}
				}
				
				if( line.Instruction.Binaries != null )
				{
					for( Instruction.Binary binary : line.Instruction.Binaries )
					{
						strBase += " " + binary.Value;
					}
				}				
			}
			else if( line.PseudoCode != null )
			{
				strBase += " : " + line.PseudoCode.Type.toString();			
			}
			
			
			strBase += "\n";
		}
		
		
		return strBase;
	}
}


	

