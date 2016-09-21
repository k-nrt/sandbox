package com.nrt.cardboardstar;

public class CardboardStarTargetList
{
	public final CardboardStarTarget World = new CardboardStarTarget();
	public final CardboardStarTarget Player = new CardboardStarTarget();
	public final CardboardStarTarget ViewPoint = new CardboardStarTarget();
	
	public final CardboardStarTarget[] Enemies = new CardboardStarTarget[20];
	public final CardboardStarTarget[] StarShips = new CardboardStarTarget[10];
	
	public CardboardStarTargetList()
	{
		for( int i = 0 ; i < Enemies.length ; i++ )
		{
			Enemies[i] = new CardboardStarTarget();
		}

		for( int i = 0 ; i < StarShips.length ; i++ )
		{
			StarShips[i] = new CardboardStarTarget();
		}
	}
	
}
