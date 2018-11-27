package edu.wit.yeatesg.wator.objects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import edu.wit.yeatesg.wator.interfaces.Movable;
import edu.wit.yeatesg.wator.interfaces.Reproducable;

public abstract class Entity implements Movable, Reproducable
{		
	/** This entity's unique id */
	protected int id;
	
	/** The Location of this Entity in {@link Entity#map} */
	protected Location location;

	/** The number of game-ticks (chronons) that this Entity has survived for */
	protected int survived;

	/** Represents the set of the Locations of fish that are adjacent to this entity */	
	protected List<Location> adjacentFish = Collections.synchronizedList(new ArrayList<Location>());
	/** Represents the set of the Locations of fish that are adjacent to this entity */	
	protected List<Location> adjacentSharks = Collections.synchronizedList(new ArrayList<Location>());
	/** Represents the set of the Locations of fish that are adjacent to this entity */	
	protected List<Location> adjacentSpaces = Collections.synchronizedList(new ArrayList<Location>());

	/**
	 * Updates the Location list fields that represent locations the entities that are adjacent to this 
	 * Entity. In this program, adjacency is defined as any tile directly next to another tile (not
	 * diagnal). See {@link #adjacentFish}, {@link #adjacentSharks}, and {@link #adjacentSpaces}
	 */
	protected void updateAdjacencyLists()
	{
		adjacentFish.clear();
		adjacentSharks.clear();
		adjacentSpaces.clear();

		List<Location> nearby = getAdjacentLocations();
		Iterator<Location> i = nearby.iterator(); // Must be in synchronized block
		while (i.hasNext())
		{
			Location loc = i.next();
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

		List<Location> nearby = Collections.synchronizedList(new ArrayList<Location>());

		if (x < map.max_X_index)
		{
			nearby.add(new Location(y, x + 1));
		}
		if (x > map.minIndex)
		{
			nearby.add(new Location(y, x - 1));
		}
		if (y < map.max_Y_index)
		{
			nearby.add(new Location(y + 1, x));
		}
		if (y > map.minIndex)
		{
			nearby.add(new Location(y - 1, x));
		}

		return nearby;
	}


	// Class Fields
	

	/** Used to assign unique ids to entitis */
	protected static int idAssign = 0;

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
	
	/**
	 * Checks to see if this entity is equivalent to another entity. Any two entities are considered
	 * equal if they have the same id
	 * @return true if these entities are equal
	 */
	@Override
	public boolean equals(Object other)
	{
		if (other instanceof Entity && ((Entity) other).id == id)
		{
			return true;
		}
		return false;
	}
}