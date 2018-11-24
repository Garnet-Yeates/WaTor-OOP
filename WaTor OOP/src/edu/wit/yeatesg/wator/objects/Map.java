package edu.wit.yeatesg.wator.objects;

import java.util.ArrayList;

import edu.wit.yeatesg.wator.containers.WaTor;
import edu.wit.yeatesg.wator.interfaces.Movable;
import edu.wit.yeatesg.wator.interfaces.Reproducable;

public class Map
{
	private WaTor container;

	private int numHorizontalTiles;
	private int numVerticalTiles;
	
	public Entity[][] array;
	public Entity[][] nextArray;
	public int max_X_index;
	public int max_Y_index;
	public int minIndex;
	
	public int chrononNum;

	public Map(int numHorizontalTiles, int numVerticalTiles, WaTor container)
	{
		Entity.setMap(this);
		this.container = container;

		this.numHorizontalTiles = numHorizontalTiles;
		this.numVerticalTiles = numVerticalTiles;
		
		array = new Entity[numVerticalTiles][numHorizontalTiles];
		nextArray = new Entity[numVerticalTiles][numHorizontalTiles];
		clear(array);
		clear(nextArray);

		max_Y_index = array.length - 1;
		max_X_index = array[1].length - 1;
		minIndex = 0;

		chrononNum = 0;			
	}

	public void nextChronon()
	{
		clear(nextArray);
		doEntityMovements();

		array = cloneArray(nextArray);
		clear(nextArray);
		chrononNum++;

		container.repaint();
	}

	public void doEntityMovements()
	{
		Location loc = new Location(0, 0);
		for (int y = 0; y <= max_Y_index; y++)
		{
			for (int x = 0; x <= max_X_index; x++)
			{
				loc.setY(y);
				loc.setX(x);
				Entity entity = Entity.at(loc);
				
				if (entity instanceof Movable)
				{
					entity.move();
				}
				if (entity instanceof Reproducable)
				{
					entity.preReproduce();	
				}				
			}

		}

	}

	public void clear(Entity[][] clearing)
	{
		for (int y = 0; y < clearing.length; y++)
		{
			for (int x = 0; x < clearing[y].length; x++)
			{
				clearing[y][x] = null;
			}
		}
	}

	private static Entity[][] cloneArray(Entity[][] arr)
	{
		Entity[][] clone = new Entity[arr.length][arr[1].length];
		for (int y = 0; y < arr.length; y++)
		{
			for (int x = 0; x < arr[y].length; x++)
			{
				clone[y][x] = arr[y][x];
			}
		}
		return clone;
	}

	public void randomFill(double fishWeight, double sharkWeight)
	{
		assert (sharkWeight + fishWeight == 1);

		clear(array);

		int maxCells = (int) (numHorizontalTiles * numVerticalTiles * 0.45);
		int minCells = (int) (numHorizontalTiles * numVerticalTiles * 0.25);

		int totalEntitiesToAdd = WaTor.R.nextInt(maxCells - minCells) + minCells;
		int numSharksToAdd = (int) (totalEntitiesToAdd * sharkWeight);
		int numFishToAdd = (int) (totalEntitiesToAdd * fishWeight);

		spawnMultiple(Shark.class, numSharksToAdd);
		spawnMultiple(Fish.class, numFishToAdd);

		container.repaint();
	}

	public <T> void spawnMultiple(T type, int amount)
	{
		ArrayList<Location> emptyTileLocs = new ArrayList<Location>();
		for (int y = 0; y < array.length; y++)
		{
			for (int x = 0; x < array[1].length; x++)
			{
				if (array[y][x] == null)
				{
					emptyTileLocs.add(new Location(y, x));
				}
			}
		}

		if (!emptyTileLocs.isEmpty())
		{	
			if (amount > emptyTileLocs.size())
			{
				amount = emptyTileLocs.size();
			}

			for (int i = 0; i < amount; i++)
			{
				i: while (true)
				{
					int randIndex = WaTor.R.nextInt(emptyTileLocs.size());
					Location space = emptyTileLocs.get(randIndex);
					if (type == Shark.class)
					{
						new Shark(space);
					}
					else if (type == Fish.class)
					{
						new Fish(space);
					}

					break i;
				}
			}
		}
	}

	public void print()
	{
		for (int y = 0; y <= max_Y_index; y++)
		{
			for (int x = 0; x <= max_X_index; x++)
			{	
				if (array[y][x] instanceof Shark)
				{
					System.out.print("S ");
					//					System.out.print(array[y][x] + " ");
				}
				else if (array[y][x] instanceof Fish)
				{
					System.out.print("F ");
					//					System.out.print(array[y][x] + ",0 ");
				}
				else
				{
					System.out.print("+ ");
				}
			}
			System.out.println();
		}

		System.out.println("\n\n");
	}
}