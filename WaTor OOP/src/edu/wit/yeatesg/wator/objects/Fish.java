package edu.wit.yeatesg.wator.objects;

import edu.wit.yeatesg.wator.containers.WaTor;

public class Fish extends Entity
{
	private static int chrononsTillReproduce;
	private static int fishEnergyWorth;

	public Fish(Location loc)
	{
		this(map.array, loc);
	}
	
	public Fish(Entity[][] array, Location loc)
	{
		array[loc.getY()][loc.getX()] = this;
		survived = 0;
		location = loc;
	}

	@Override
	public boolean move()
	{
		survived++;

		updateAdjacencyLists();
		
		boolean hasAdjSpace = this.adjacentSpaces.size() > 0 ? true : false;
		
		boolean moved = true;
		if (hasAdjSpace)
		{
			location = adjacentSpaces.get(WaTor.R.nextInt(adjacentSpaces.size()));
		}
		else
		{
			moved = false;
		}
		
		map.nextArray[location.getY()][location.getX()] = this;
		
		return moved;
	}
	
	@Override
	public void preReproduce()
	{
		if (map.chrononNum % Fish.chrononsTillReproduce == 0)
		{
			reproduce();
		}
	}

	@Override
	public void reproduce()
	{
		Location startLoc = location.clone();
		boolean moved = move();
		if (moved)
		{
			new Fish(map.nextArray, startLoc);
		}
	}

	public static int getFishEnergyWorth()
	{
		return fishEnergyWorth;
	}

	public static void setFishEnergyWorth(int fishEnergyWorth)
	{
		Fish.fishEnergyWorth = fishEnergyWorth;
	}
	

	public static void setChrononsTillReproduce(int chrononsTillReproduce)
	{
		Fish.chrononsTillReproduce = chrononsTillReproduce;
	}
}
