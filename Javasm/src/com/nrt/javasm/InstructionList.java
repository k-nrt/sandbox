package com.nrt.javasm;

public class InstructionList
{
	public Instruction[] Instructions = null;
	
	public InstructionList() {}
	
	public InstructionList( String[] arrayLines, TextLog log )
	{
		Instructions = new Instruction[arrayLines.length];
		
		for( int i = 0 ; i < Instructions.length ; i++ )
		{
			Instructions[i] = new Instruction( arrayLines[i] );
			log.AppendText( Instructions[i].Dump() + "\n" );
		}
	}
	
	public String Dump()
	{
		String strResult = "";
		for( Instruction instruction : Instructions )
		{
			strResult += instruction.Dump();
			strResult += "\n";
		}
		return strResult;
	}
	
	public Instruction FindInstruction( Line line )
	{
		for( Instruction instruction : Instructions )
		{
			if( instruction.Undefined )
			{
				continue;
			}
			
			if( instruction.Opcode == null )
			{
				continue;
			}
			
			if( line.Opcode == null )
			{
				continue;
			}
			
			if( instruction.Opcode.compareTo( line.Opcode.Name.toUpperCase() ) == 0 )
			{
				/*
				if( instruction.Operands == null && line.Expressions = )
				{
					return  instruction;
				}
				else */
				if( instruction.Operands == null )
				{
					continue;
				}
				
				if( instruction.Operands.length == line.Expressions.size() )
				{
					boolean isFound = true;
					for( int i = 0 ; i < instruction.Operands.length ; i++ )
					{
						Instruction.EOperand eOperand = instruction.Operands[i];
						
						if( eOperand == null )
						{
							isFound = false;
							break;
						}
					
						if( eOperand.Parser != null )
						{
							if( eOperand.Parser.IsMatch( line.Expressions.get(i) ) == false )
							{
								isFound = false;
								break;
							}
						}
						else
						{
							isFound = false;
							break;
						}	
					}
					
					if( isFound )
					{
						return instruction;
					}
				}
			}
		}	
		
		return null;
	}
}
