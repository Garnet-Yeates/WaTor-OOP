package edu.wit.yeatesg.wator.objects;

import edu.wit.yeatesg.wator.containers.WaTor;

public class Shark extends Entity
{	
	private static int baseEnergy;
	private static int chrononsTillReproduce;
	private static int invulnerabilityPeriod;
	
	private int energy;
		
	public Shark(Location loc)
	{
		this(map.array, loc);
	}
	
	public Shark(Entity[][] array, Location loc)
	{
		array[loc.getY()][loc.getX()] = this;
		energy = baseEnergy;
		survived = 0;
		location = loc;
	}
	
	@Override
	public boolean move()
	{
		survived++;
		depleteEnergy(1);
		
		if (energy < 0)
		{
			energy = 0;
		}

		updateAdjacencyLists();
		
		boolean hasAdjSpace = this.adjacentSpaces.size() > 0 ? true : false;
		boolean hasAdjFish = this.adjacentFish.size() > 0 ? true : false;
			
		boolean moved = true;
		if (hasAdjFish)
		{
			System.out.println(adjacentFish.size());
			location = adjacentFish.get(WaTor.R.nextInt(adjacentFish.size()));
			energy+= Fish.getFishEnergyWorth();
		}
		else if (hasAdjSpace)
		{
			location = adjacentSpaces.get(WaTor.R.nextInt(adjacentSpaces.size()));
		}
		else
		{
			moved = false;
		}
		
		map.nextArray[location.getY()][location.getX()] = this;
		
		updateMortality();
		
		return moved;
	}
	
	public void updateMortality()
	{
		if (energy < 1 && survived > invulnerabilityPeriod)
		{
			map.nextArray[location.getY()][location.getX()] = null;
		}
	}

	@Override
	public void preReproduce()
	{
		if (map.chrononNum % Shark.chrononsTillReproduce == 0 && energy > 0)
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
			new Shark(map.nextArray, startLoc);
		}
	}
	
	public void depleteEnergy(int amount)
	{
		energy -= amount;
	}
	
	public int getEnergy()
	{
		return energy;
	}

	public static int getBaseEnergy()
	{
		return baseEnergy;
	}

	public static void setBaseEnergy(int baseEnergy)
	{
		Shark.baseEnergy = baseEnergy;
	}

	public static int getChrononsTillReproduce()
	{
		return chrononsTillReproduce;
	}

	public static void setChrononsTillReproduce(int churonsTillReproduce)
	{
		Shark.chrononsTillReproduce = churonsTillReproduce;
	}
	
	public static void setInvulnerabilityPeriod(int churons)
	{
		Shark.invulnerabilityPeriod = churons;
	}
}
