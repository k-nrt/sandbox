package com.nrt.javasm;

import android.app.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import android.webkit.*;
import java.io.*;

import com.nrt.basic.Loader;
import java.util.*;
import android.location.*;
import android.nfc.*;
import java.lang.ref.*;
import javax.xml.parsers.*;
import java.lang.Runnable;
import java.lang.Thread;

public class MainActivity extends Activity implements Runnable
{
	Loader Loader = null;
	Thread m_thread = null;
	TextViewLog m_textViewLog = null;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

		final TextView textView = (TextView) this.findViewById(R.id.textView);
		
		m_textViewLog = new TextViewLog( textView );		
		m_thread = new Thread( this );
		m_thread.start();		
    }

	@Override
	public void run()
	{
		Loader = new Loader(this.getAssets());
		String[] arrayInstructionSourceLines = Loader.LoadTextFile("z80_instructions.txt");
		String[] arraySourceLines = Loader.LoadTextFile("test.asm");

		AppendText( "\n" );
				
		Assembler asm = new Assembler();
		asm.InstructionList = new InstructionList( arrayInstructionSourceLines, m_textViewLog );
		asm.Parse(arraySourceLines);
				
		asm.DumpTokens( m_textViewLog );

		String strLines = "";
		strLines = asm.LineParser.DumpLines(strLines);
		AppendText( strLines );
	}
	
	void AppendText( String strText )
	{
		m_textViewLog.AppendText( strText );
	}
}


class Assembler
{
	public Assembler() 
	{

	}

	public InstructionList InstructionList = null;
	
	Tokenizer Tokenizer = new Tokenizer();
	
	public LineParser LineParser = new LineParser();

	public void Parse(String[] arrayLines)
	{
		Tokenizer.Tokenize( arrayLines );

		//ParseTokens( Tokenizer.Tokens );
		
		LineParser.Parse( Tokenizer.Tokens );
		
		int Address = 0;
		for( int i = 0 ;  i < LineParser.Lines.size() ; i++ )
		{
			Line line = LineParser.Lines.get(i);			
			line.Address = Address;
			line.Instruction = InstructionList.FindInstruction( line );
			if( line.Instruction == null )
			{
				line.PseudoCode = PseudoCode.Find( line );
				if( line.PseudoCode != null )
				{
					Address += line.PseudoCode.Type.DataCounter.GetDataLength( line.PseudoCode.Expressions );
				}
			}
			else
			{
				Address += line.Instruction.Binaries.length;
			}
		}
	}

	public void DumpTokens( TextLog log )
	{
		log.AppendText( "Tokenizer result -------------\n" );
		for (int i = 0 ; i < LineParser.Lines.size() ; i++ )
		{
			Line line = LineParser.Lines.get(i);
			for (Token token : line.Tokens)
			{
				log.AppendText( String.format("%06d\t\t:", token.Line) );
				for (int j = 0 ; j < token.Depth ; j++)
				{
					log.AppendText(  "\t" );
				}

				log.AppendText( token.Name );
				log.AppendText(  "\t" );
				log.AppendText( token.Type.toString() );
				
				if( token.Keyword != null )
				{
					log.AppendText( "\t" );
					log.AppendText( token.Keyword.toString() );
				}
					
				log.AppendText( "\n" );
			}
			log.AppendText( "\n" );
		}
		log.AppendText( "-------------\n" );
	}
}
	
