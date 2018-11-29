package edu.wit.yeatesg.wator.containers;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

import edu.wit.yeatesg.wator.objects.Entity;
import edu.wit.yeatesg.wator.objects.Fish;
import edu.wit.yeatesg.wator.objects.Location;
import edu.wit.yeatesg.wator.objects.Map;
import edu.wit.yeatesg.wator.objects.Map.Preset;
import edu.wit.yeatesg.wator.objects.Shark;

public class WaTor extends JPanel
{
	private static final long serialVersionUID = 1777168099445808538L;

	private static final int PREF_X_BOUND = 1915;
	private static final int PREF_Y_BOUND = 1000;
	
	private JFrame container;

	public static final Random R = new Random(1);

	public static void main(String[] args)
	{
		openDisplayGUI(950, 0);
		Map.setPreset(Preset.FAST_CYCLE);
	}

	private static void openDisplayGUI(int width, int gridThickness)
	{
		JFrame gameFrame = new JFrame();
		new WaTor(width, gridThickness, gameFrame);
	}
	
	public static void waitForThreads(ArrayList<Thread> threads)
	{
		boolean threadsDone = false;
		while (!threadsDone)
		{
			threadsDone = true;
			i: for (Thread tr : threads)
			{
				if (tr.isAlive())
				{
					threadsDone = false;
					break i;
				}
			}
		}
	}
	
	
	// Constructor and Instance Fields
	
	
	private int tileSize;

	private int gridlineSize;
	private int numGridlines;

	private int preferredWidth;
	private int preferredHeight;

	private int numVerticalTiles;
	private int numHorizontalTiles;

	private Map map;

	public WaTor(int numHorizontalTiles, int gridlineSize, JFrame container)
	{
		this.container = container;
		setSpatialFields(numHorizontalTiles, gridlineSize);

		map = new Map(this.numHorizontalTiles, numVerticalTiles, this);

		map.randomFill(0.99, 0.01);
		repaint();

		timer.setInitialDelay(0);
		timer.start();
		secondsTimer.start();	
	}
	
	/**
	 * Every 10 milliseconds a new game tick occurs (100 per second). The simulation can't actually run that
	 * fast and it runs from 4-40 times per second depending on how many entities are on screen
	 */
	public void tick()
	{
		tickNum++;
		if (tickNum > INITIAL_DELAY)
		{
			map.nextChronon();
		}
	}
	
	/**
	 * Sets the size of this component
	 * @param numHorizontalTiles
	 * @param gridlineSize
	 */
	private void setSpatialFields(int numHorizontalTiles, int gridlineSize)
	{
		this.tileSize = (int) (((double) PREF_X_BOUND - (double) numGridlines*gridlineSize) / (double) numHorizontalTiles);
		System.out.println(this.tileSize);
						
		this.numHorizontalTiles = numHorizontalTiles;
		
		this.numGridlines = this.numHorizontalTiles + 1;
		this.gridlineSize = gridlineSize;
				
		this.numVerticalTiles = (int) (((double) PREF_Y_BOUND / (double) PREF_X_BOUND) * (double) this.numHorizontalTiles);
		System.out.println(this.numHorizontalTiles + " " + numVerticalTiles);
		
		this.preferredHeight = numVerticalTiles*tileSize + numGridlines*gridlineSize - 10;
		this.preferredWidth = this.numHorizontalTiles*tileSize + numGridlines*gridlineSize - 10;
		
		System.out.println(this.numHorizontalTiles + " x " + numVerticalTiles);
		
		this.setPreferredSize(new Dimension(this.preferredWidth, this.preferredHeight));
		container.getContentPane().add(this);
		container.pack();
		container.getContentPane().setBackground(Color.LIGHT_GRAY);
		container.setResizable(false);
		container.setVisible(true);
		container.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		container.add(this);		
	}

	@Override
	public Dimension getPreferredSize()
	{
		return new Dimension(preferredWidth, preferredHeight);
	}

	private Color fishColor = Color.GREEN;
	private Color sharkColor = Color.BLUE;
	private Color spaceColor = Color.BLACK;

	class PaintThread implements Runnable
	{
		private Graphics g;
		private ArrayList<Integer> yVals;

		public PaintThread(ArrayList<Integer> yVals, Graphics g)
		{
			this.yVals = yVals;
			this.g = g.create();
		}

		@Override
		public void run()
		{
			for (int i = 0; i < this.yVals.size(); i++)
			{
				int y = yVals.get(i);
				int yPos = gridlineSize + (y * gridlineSize) + (y * tileSize);
				int xPos = gridlineSize;
				for (int x = 0; x < map.array[1].length; x++)
				{
					Entity entity = Entity.at(new Location(y, x));
					if (entity instanceof Shark)
					{
						g.setColor(sharkColor);
						g.fillRect(xPos, yPos, tileSize, tileSize);
					}
					else if (entity instanceof Fish)
					{
						g.setColor(fishColor);
						g.fillRect(xPos, yPos, tileSize, tileSize);
					}
					xPos += gridlineSize + tileSize;
				}
			}
		}
	}
	
	@Override
	public void paint(Graphics g)
	{
		g.setColor(spaceColor);
		g.fillRect(0, 0, preferredWidth + 10, preferredWidth + 10);

		ArrayList<Thread> threads = new ArrayList<>();
		ArrayList<Integer> intervals = new ArrayList<>();
		double numThreads = numVerticalTiles / 25;
		double numerator;
		for (numerator = 0; numerator < numThreads; numerator++)
		{
			intervals.add((int) (map.array.length * (numerator / numThreads)));
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
				for (int y = intervals.get(i); y < map.array.length; y++)
				{
					yVals.add(y);
				}
			}

			Thread t = new Thread(new PaintThread(yVals, g));
			threads.add(t);
			t.start();	
		}

		WaTor.waitForThreads(threads);
	}
	
	int secsPassed = 0;
	ArrayList<Double> speedList = new ArrayList<>();
	
	public Timer secondsTimer = new Timer(1000, new ActionListener()
	{
		
		@Override
		public void actionPerformed(ActionEvent e)
		{
			secsPassed++;
			speedList.add((double) map.chrononNum / (double)secsPassed);
		}
	});
	
	public double averageSimulationSpeed()
	{
		double sum = 0;
		for (double d : speedList)
		{
			sum += d;	
		}
		return sum / (double) speedList.size(); // Ticks per second
	}
	

	class ChrononClock implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent arg0)
		{
			tick();
			if (map.chrononNum >= 5 && map.chrononNum % 5 == 0)
			{
				System.out.println(map.chrononNum + " : " + averageSimulationSpeed());
			}
			
		}
	}

	private int delay = 10;
	private Timer timer = new Timer(delay, new ChrononClock());

	private int tickNum = 0;

	public int INITIAL_DELAY = 350;

}
