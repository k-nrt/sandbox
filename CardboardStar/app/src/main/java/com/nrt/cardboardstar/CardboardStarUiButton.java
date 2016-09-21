package com.nrt.cardboardstar;

import com.nrt.ui.UiRectButton;
import com.nrt.render.*;
import com.nrt.input.*;
import com.nrt.math.*;

public class CardboardStarUiButton extends UiRectButton
{
	final float kHoldTime = 0.25f;
	final float kTapTime = 0.5f;
	
	float m_fDownTime = 0.0f;
	
	float Rotation = 0.0f;
	final Float3 Velocity = new Float3();
	
	final Float3 PushPosition = new Float3();
	final Float3 LastPosition = new Float3();
	
	public CardboardStarUiButton(float x, float y, float w, float h)
	{
		super(x,y,w,h,x,y,w,h);
	}

	@Override
	public void OnUpdate(float fElapsedTime)
	{
		// TODO: Implement this method
		super.OnUpdate(fElapsedTime);
		
		if(IsPush())
		{
			m_fDownTime = 0.0f;
			for( FramePointer.Pointer pointer : Pointers )
			{
				if( pointer == null )
				{
					continue;
				}
				else
				{
					PushPosition.Load( pointer.Position );
					break;
				}
			}
		}
		else if(IsDown())
		{
			m_fDownTime += fElapsedTime;
			
			
		}
		else
		{
		
		}
		
		if(IsDown())
		{
			int nbPointers = 0;
			Rotation = 0.0f;
			Velocity.Load(0.0f);
			
			
			for(FramePointer.Pointer pointer : Pointers )
			{
				if( pointer == null )
				{
					continue;
				}
				
				Rotation += pointer.History.AverageRotation;
				Velocity.Add( Velocity, pointer.History.AverageVelocity );
				nbPointers++;
				
				LastPosition.Load( pointer.Position );
			}
			
			if( 0 < nbPointers )
			{
				Rotation /= (float) nbPointers;
				Velocity.Div( Velocity, (float) nbPointers );
			}	
		}		
	}

	@Override
	public void OnRender(BasicRender br)
	{
		// TODO: Implement this method
		super.OnRender(br);
		
		
		for(FramePointer.Pointer pointer : Pointers )
		{
			if( pointer == null )
			{
				continue;
			}
			
			if( 1 < pointer.History.Histories )
			{
				br.SetColor(1.0f,1.0f,1.0f,1.0f);
				br.Begin( Primitive.LineStrip, BasicRender.EShader.Color );
				for( int i = 0 ; i < pointer.History.Histories ; i++ )
				{
					br.SetVertex( pointer.History.GetPosition(i));
				}
				br.End();
			}
			
			float cx = Enter.X + Enter.Width/2.0f;
			float cy = Enter.Y + Enter.Height/2.0f;
			float s = FMath.Sin(pointer.History.AverageRotation);
			float c = FMath.Cos(pointer.History.AverageRotation);
			
			br.Begin(Primitive.Lines,BasicRender.EShader.Color);
			br.SetVertex(cx,cy,0.0f);
			br.SetVertex(cx + c*500.0f, cy+s*500.0f, 0.0f);
			br.End();
		}
		
	}	
	
	public boolean IsHold() 
	{
		if(IsDown() && kHoldTime < m_fDownTime )
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	public boolean IsRelease()
	{
		if( 0 < m_nbPrevPointers && m_nbPointers <= 0 )
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	public boolean IsTap()
	{
		float fDistance = Float3.Distance( PushPosition, LastPosition );
		if(IsRelease() && m_fDownTime < kTapTime && fDistance < 200.0f )
		{
			return true;
		}
		else
		{
			return false;
		}
	}
}
