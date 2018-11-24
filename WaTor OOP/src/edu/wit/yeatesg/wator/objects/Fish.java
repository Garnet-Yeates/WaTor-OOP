package edu.wit.yeatesg.wator.objects;

import edu.wit.yeatesg.wator.containers.WaTor;

public class Fish extends Entity
{
	/**
	 * Constructs and spawns a new Fish in {@link Map#array}
	 * @param loc The y,x location in the Array to put this shark into
	 */
	public Fish(Location loc)
	{
		this(map.array, loc);
	}
	
	/**
	 * Constructs and spawns a new fish in the given array. For reproduction,
	 * this constructor should be called with {@link Map#nextArray}. For regular
	 * spawning, this should be called with {@link Map#array}. See {@link #Fish(Location)}
	 * @param array the array to spawn the Fish into
	 * @param loc the y,x location in the array to put this Fish at
	 */
	public Fish(Entity[][] array, Location loc)
	{
		array[loc.getY()][loc.getX()] = this;
		survived = 0;
		location = loc;
	}

	/**
	 * Tries to move this fish to an adjacent space. If there exists an adjacent space, this fish
	 * will move to it. If there isn't an adjacent space, this fish will do nothing
	 */
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
	
	/**
	 * Called to see if this Fish should reproduce. Every {@link Fish#chrononsTillReproduce} that
	 * this Fish survives for, it will {@link #reproduce()}
	 */
	@Override
	public void preReproduce()
	{
		if (survived % Fish.chrononsTillReproduce == 0)
		{
			reproduce();
		}
	}

	/**
	 * Causes this Fish to reproduce. During reproduction, the Fish attempts to move to an empty space.
	 * If {@link #move()} returns false, meaning the Fish didn't actually move, then no reproduction will take place.
	 * If the Fish was able to move, it will spawn another Fish in its previous location
	 */
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
	
	/*********************************************************************************************************************
	 * 																																						*
	 * 																		CLASS FIELDS																*
	 * 																																						*
	 *********************************************************************************************************************/	
	
	/** Every time a fish survives for {@link #chrononsTillReproduce} chronons, it will reproduce */
	public static int chrononsTillReproduce;
	
	/** How much energy should a Shark gain from eating any fish? */
	public static int fishEnergyWorth;
}