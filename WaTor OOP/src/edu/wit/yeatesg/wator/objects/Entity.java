package edu.wit.yeatesg.wator.objects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.wit.yeatesg.wator.containers.Map;
import edu.wit.yeatesg.wator.interfaces.Movable;
import edu.wit.yeatesg.wator.interfaces.Reproducable;

public abstract class Entity implements Movable, Reproducable
{		
	protected Location location;

	protected int survived;
	
	protected int reproductionRate;

	protected List<Location> adjacentSharks;
	protected List<Location> adjacentFish;
	protected List<Location> adjacentSpaces;
	
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
	
	protected List<Location> getAdjacentLocations()
	{
		int x = location.getX();
		int y = location.getY();

		List<Location> nearby = Collections.synchronizedList(new ArrayList<Location>());

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
	
	public void setLocation(Location loc)
	{
		this.location = loc;
	}
	
	// Class Fields and Methods below
	
	protected static Map map;
	
	public static void setMap(Map map)
	{
		Entity.map = map;
	}
	
	public static Entity at(Location p)
	{
		return map.array[p.getY()][p.getX()];
	}
}