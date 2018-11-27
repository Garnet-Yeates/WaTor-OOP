package edu.wit.yeatesg.wator.objects;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import javax.swing.Timer;

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
		
		tim.start();
	}
	
	int secsPassed = 0;
	ArrayList<Double> chroPerSecTable = new ArrayList<>();
	
	public Timer tim = new Timer(1000, new ActionListener()
	{
		
		@Override
		public void actionPerformed(ActionEvent e)
		{
			secsPassed++;
			chroPerSecTable.add((double)chrononNum / (double)secsPassed);
		}
	});


	public void nextChronon()
	{
		clear(nextArray);
		doEntityMovements();

		array = cloneArray(nextArray);
		clear(nextArray);
		chrononNum++;
		
      container.repaint();
	}
		
	class ArrayReader implements Runnable
	{
		private ArrayList<Integer> yVals;
		
		public ArrayReader(ArrayList<Integer> yVals)
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
			
		}
		
	}
	

	public void doEntityMovements()
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
			
			Thread t = new Thread(new ArrayReader(yVals));
			threads.add(t);
			t.start();	
		}
		
		
		WaTor.waitForThreads(threads);
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
	
	public void presetFast()
	{
		Fish.TICKS_TILL_REPRODUCE = 6;
		Fish.ENERGY_WORTH = 3;

		Shark.INVULNERABILITY_TICKS = 3;
		Shark.BASE_ENERGY = 0;
		Shark.TICKS_TILL_REPRODUCE = 1;
		Shark.MAX_ENERGY = 12;
	}

	public void idek()
	{
		Fish.TICKS_TILL_REPRODUCE = 24;
		Fish.ENERGY_WORTH = 6;

		Shark.INVULNERABILITY_TICKS = 9;
		Shark.BASE_ENERGY = 0;
		Shark.TICKS_TILL_REPRODUCE = 3;
		Shark.MAX_ENERGY = 9;
	}

	public void presetFullExtinction()
	{
		Fish.TICKS_TILL_REPRODUCE = 15;
		Fish.ENERGY_WORTH = 10;

		Shark.INVULNERABILITY_TICKS = 20;
		Shark.BASE_ENERGY = 0;
		Shark.TICKS_TILL_REPRODUCE = 1;
	}

	public void presetCycle1()
	{
		Fish.TICKS_TILL_REPRODUCE = 30;
		Fish.ENERGY_WORTH = 4;

		Shark.INVULNERABILITY_TICKS = 10;
		Shark.BASE_ENERGY = 4;
		Shark.TICKS_TILL_REPRODUCE = 4;
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