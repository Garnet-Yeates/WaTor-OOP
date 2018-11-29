package edu.wit.yeatesg.wator.objects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import edu.wit.yeatesg.wator.containers.WaTor;

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
		clearArray(array);
		clearArray(nextArray);

		max_Y_index = array.length - 1;
		max_X_index = array[1].length - 1;
		minIndex = 0;

		chrononNum = 0;	
	}

	public void nextChronon()
	{
		clearArray(nextArray);
		doEntityActions();

		array = cloneArray(nextArray);
		clearArray(nextArray);
		chrononNum++;

		container.repaint();
	}

	/**
	 * This class is a Runnable instance that is designed to cycle through a section of y-values in an Entity[yVals][xVals] array.
	 * For each y value it cycles through, it also cycles through the respective x-values of that y-value. This means that in a
	 * 1800x1000 array, if 10 ActThreads were assigned to 100 y-values each, then each thread will have to do 1800 x values instead
	 * of one thread having to do 1.8 million. In essencusing multithreading with ActThreads makes the program run much faster
	 * @author yeatesg
	 */
	class ActThread implements Runnable
	{
		private ArrayList<Integer> yVals;

		public ActThread(ArrayList<Integer> yVals)
		{
			this.yVals = yVals;
		}

		@Override
		public void run()
		{
			for (int i = 0; i < this.yVals.size(); i++)
			{
				
				int y = yVals.get(i);
				for (int x = 0; x <= max_X_index; x++)
				{
					Entity entity = Entity.at(new Location(y, x));
					if (entity != null)
					{					
						entity.move();
						entity.preReproduce();	
					}		
				}						
			}	
		}
	}

	public void doEntityActions()
	{
		ArrayList<Thread> threads = new ArrayList<Thread>();
		ArrayList<Integer> intervals = new ArrayList<>();
		double numThreads = numVerticalTiles / 15;
		double numerator;
		for (numerator = 0; numerator < numThreads; numerator++)
		{
			intervals.add((int) (array.length * (numerator / numThreads)));
		}

		for (int i = 0; i < intervals.size(); i++)
		{
			ArrayList<Integer> yVals = new ArrayList<Integer>();
			int j = i + 1;

			if (j < intervals.size())
			{
				for (int y = intervals.get(i); y < intervals.get(j); y++)
				{
					yVals.add(y);
				}
			}
			else
			{
				for (int y = intervals.get(i); y < array.length; y++)
				{
					yVals.add(y);
				}
			}

			Thread t = new Thread(new ActThread(yVals));
			threads.add(t);
			t.start();	
		}

		WaTor.waitForThreads(threads);
	}

	public void randomFill(double fishWeight, double sharkWeight)
	{
		assert (sharkWeight + fishWeight == 1);

		clearArray(array);

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
					Location loc = new Location(y, x);
					emptyTileLocs.add(loc);
				}
			}
		}

		if (!emptyTileLocs.isEmpty())
		{	
			Collections.shuffle(emptyTileLocs);
			if (amount > emptyTileLocs.size())
			{
				amount = emptyTileLocs.size();
			}

			Iterator<Location> it = emptyTileLocs.iterator();
			for (int i = 0; i < amount; i++)
			{
				if (it.hasNext())
				{
					Location space = it.next();
					if (type == Shark.class)
					{
						new Shark(space);
					}
					else if (type == Fish.class)
					{
						new Fish(space);
					}
				}
			}
		}
	}

	
	// Class fields and methods


	public static enum Preset
	{
		FAST_CYCLE, FULL_EXTINCTION, SLOW_CYCLE;
	}

	public static void setPreset(Preset s)
	{
		switch (s)
		{
		default:
		case FAST_CYCLE:
			Fish.TICKS_TILL_REPRODUCE = 8;
			Fish.ENERGY_WORTH = 3;
			Shark.INVULNERABILITY_TICKS = 3;
			Shark.BASE_ENERGY = 0;
			Shark.TICKS_TILL_REPRODUCE = 1;
			Shark.MAX_ENERGY = 12;
			break;
		case FULL_EXTINCTION:
			Fish.TICKS_TILL_REPRODUCE = 15;
			Fish.ENERGY_WORTH = 10;
			Shark.INVULNERABILITY_TICKS = 20;
			Shark.BASE_ENERGY = 0;
			Shark.TICKS_TILL_REPRODUCE = 1;
			break;
		case SLOW_CYCLE:
			Fish.TICKS_TILL_REPRODUCE = 30;
			Fish.ENERGY_WORTH = 4;
			Shark.INVULNERABILITY_TICKS = 10;
			Shark.BASE_ENERGY = 4;
			Shark.TICKS_TILL_REPRODUCE = 4;
			break;
		}
	}

	public static void clearArray(Entity[][] clearing)
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

	public static void printArray(Entity[][] array)	
	{
		for (int y = 0; y < array.length; y++)
		{
			for (int x = 0; x < array[1].length; x++)
			{	
				if (array[y][x] instanceof Shark)
				{
					System.out.print("S ");
				}
				else if (array[y][x] instanceof Fish)
				{
					System.out.print("F ");
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