package edu.wit.yeatesg.wator.objects;

import edu.wit.yeatesg.wator.containers.WaTor;

public class Shark extends Entity
{	
	private int energy;
		
	/**
	 * Constructs and spawns a new Shark in {@link Map#array}
	 * @param loc The y,x location in the Array to put this shark into
	 */
	public Shark(Location loc)
	{
		this(map.array, loc);
	}
	
	/**
	 * Constructs and spawns a new shark in the given array. For reproduction,
	 * this constructor should be called with {@link Map#nextArray}. For regular
	 * spawning, this should be called with {@link Map#array}. See {@link #Shark(Location)}
	 * @param array the array to spawn the Shark into
	 * @param loc the y,x location in the array to put this Shark at
	 */
	public Shark(Entity[][] array, Location loc)
	{
		array[loc.getY()][loc.getX()] = this;
		energy = baseEnergy;
		survived = 0;
		location = loc;
	}
	
	/**
	 * Tries to move this shark to an adjacent fish. If there exists an adjacent fish, this Shark
	 * will gain {@link Fish#getFishEnergyWorth()} energy. If there is no adjacent fish, it will
	 * try to move this Shark to an adjacent space. If there is no free space or fish to move to,
	 * this Shark won't move. Whether or not this Shark moves, its energy is depleted by a specific
	 * amount. After this method is called, {@link #updateMortality()} is called to check if this Shark
	 * should die as a result of running out of energy.
	 */
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
			energy+= Fish.fishEnergyWorth;
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
	
	/**
	 * This method is called every movement, so in essense it is called every game tick
	 * to check to see if this Shark should die. If the Shark runs out of energy and hasn't
	 * survived for longer than {@link #invulnerabilityPeriod}, then it will die.
	 */
	public void updateMortality()
	{
		if (energy < 1 && survived > invulnerabilityPeriod)
		{
			map.nextArray[location.getY()][location.getX()] = null;
		}
	}

	/**
	 * Called to see if this Shark should reproduce. Every {@link Shark#chrononsTillReproduce} that
	 * this Shark survives for, it will {@link #reproduce()} as long as it has energy. (Normally not having energy
	 * means that this Shark should die, but if it is still in the invulnerability period it will have
	 * 0 energy without dying)
	 */
	@Override
	public void preReproduce()
	{
		if (survived % Shark.chrononsTillReproduce == 0 && energy > 0)
		{
			reproduce();
		}
	}

	/**
	 * Causes this Shark to reproduce. During reproduction, the Shark attempts to move to a fish or an empty space.
	 * If {@link #move()} returns false, meaning the Shark didn't actually move, then no reproduction will take place.
	 * If the Shark is able to move, it will spawn another Shark in its previous location
	 */
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
	
	/**
	 * Depletes this Shark's energy by a given amount. Equivalent to {@link Shark#energy} -= amount
	 * @param amount the amount of energy that this Shark should lose
	 */
	public void depleteEnergy(int amount)
	{
		energy -= amount;
	}
	
	/**
	 * Obtains the energy level of this Shark
	 * @return an integer representing this Shark's energy
	 */
	public int getEnergy()
	{
		return energy;
	}

	/*********************************************************************************************************************
	 * 																																						*
	 * 																		CLASS FIELDS																*
	 * 																																						*
	 *********************************************************************************************************************/

	/** How much energy should every Shark have when they are born? */
	public static int baseEnergy;
	
	/** How many game ticks does every Shark need to survive for in order to reproduce? */
	public static int chrononsTillReproduce;
	
	/** How many ticks does every Shark need to be alive for before they can die? */
	public static int invulnerabilityPeriod;
}