package edu.wit.yeatesg.wator.objects;

import java.util.ArrayList;
import java.util.List;

import edu.wit.yeatesg.wator.interfaces.Movable;
import edu.wit.yeatesg.wator.interfaces.Reproducable;

public abstract class Entity implements Movable, Reproducable
{		
	/** The Location of this Entity in {@link Entity#map} */
	protected Location location;

	/** The number of game-ticks (chronons) that this Entity has survived for */
	protected int survived;
	
	/** Represents sets of Locations of adjacent Fish, Sharks, and spaces relative to this Entity */
	protected List<Location> adjacentSharks,
									 adjacentFish,
									 adjacentSpaces;
	
	/**
	 * Updates the Location list fields that represent locations the entities that are adjacent to this 
	 * Entity. In this program, adjacency is defined as any tile directly next to another tile (not
	 * diagnal). See {@link #adjacentFish}, {@link #adjacentSharks}, and {@link #adjacentSpaces}
	 */
	protected void updateAdjacencyLists()
	{
		adjacentFish = new ArrayList<Location>();
		adjacentSharks = new ArrayList<Location>();
		adjacentSpaces = new ArrayList<Location>();
		
		for (Location loc : getAdjacentLocations())
		{
			Entity e = Entity.at(loc);
			
			if (e instanceof Shark)
			{
				adjacentSharks.add(loc);
			}
			if (e instanceof Fish)
			{
				adjacentFish.add(loc);
			}
			if (e == null)
			{
				adjacentSpaces.add(loc);
			}
		}
	}
	
	/**
	 * Obtains the list of all (max 4) adjacent Locations to this Entity. If there are less than 4
	 * Locations in this list, it means that this Entity exists at an edge or corner of {@link Entity#map}
	 * @return a list containing all adjacent Locations to this Entity's location
	 */
	protected List<Location> getAdjacentLocations()
	{
		int x = location.getX();
		int y = location.getY();

		List<Location> nearby = new ArrayList<Location>();

		if (x < map.maxIndex)
		{
			nearby.add(new Location(y, x + 1));
		}
		if (x > map.minIndex)
		{
			nearby.add(new Location(y, x - 1));
		}
		if (y < map.maxIndex)
		{
			nearby.add(new Location(y + 1, x));
		}
		if (y > map.minIndex)
		{
			nearby.add(new Location(y - 1, x));
		}
		
		return nearby;
	}
	
	/*********************************************************************************************************************
	 * 																																						*
	 * 																		CLASS FIELDS																*
	 * 																																						*
	 *********************************************************************************************************************/
	
	/** The Map that all entities in this simulation exist on */
	protected static Map map;
	
	/**
	 * Changes the Map that all entities will be spawned into. It is only recommended that this
	 * is changed at the beginning of the simulation and never again.
	 * @param map The new map
	 */
	public static void setMap(Map map)
	{
		Entity.map = map;
	}
	
	/**
	 * Obtains the Entity that exists at the given Location in {@link Entity#map}
	 * @param loc the location in {@link Entity#map}
	 * @return the Entity at this location, or null if no Entity exists here
	 */
	public static Entity at(Location loc)
	{
		return map.array[loc.getY()][loc.getX()];
	}
}