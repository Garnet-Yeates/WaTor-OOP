package edu.wit.yeatesg.wator.containers;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

import edu.wit.yeatesg.wator.objects.Entity;
import edu.wit.yeatesg.wator.objects.Fish;
import edu.wit.yeatesg.wator.objects.Location;
import edu.wit.yeatesg.wator.objects.Map;
import edu.wit.yeatesg.wator.objects.Shark;

public class WaTor extends JPanel
{
	private static final long serialVersionUID = 1777168099445808538L;
	
	private static final int PREF_X_BOUND = 1800;
	private static final int PREF_Y_BOUND = 1000;
	
	public static final Random R = new Random();
	
	private int tileSize;
	
	private int gridlineSize;
	private int numGridlines;
	
	private int preferredWidth;
	private int preferredHeight;
	
	private int numVerticalTiles;
	
	private Map map;
	
	public static void main(String[] args)
	{
		openDisplayGUI(540, 0);
	}
	
	private static void openDisplayGUI(int width, int gridThickness)
	{
		JFrame gameFrame = new JFrame();

		WaTor gamePanel = new WaTor(width, gridThickness);
		gamePanel.setPreferredSize(new Dimension(gamePanel.preferredWidth, gamePanel.preferredHeight));
		gameFrame.getContentPane().add(gamePanel);
		gameFrame.pack();
		gameFrame.getContentPane().setBackground(Color.LIGHT_GRAY);
		gameFrame.setResizable(false);
		gameFrame.setVisible(true);
		gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		gameFrame.add(gamePanel);		
	}
	
	/**
	 * Create the panel.
	 */
	public WaTor(int numHorizontalTiles, int gridlineSize)
	{
		presetFast();
		setSpatialFields(numHorizontalTiles, gridlineSize);
		
		System.out.println(numHorizontalTiles);
		System.out.println(numVerticalTiles);
		map = new Map(numHorizontalTiles, numVerticalTiles, this);

		map.randomFill(0.98, 0.02);
		repaint();
		
		timer.setInitialDelay(0);
		timer.start();
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
		Fish.TICKS_TILL_REPRODUCE = 8;
		Fish.ENERGY_WORTH = 5;

		Shark.INVULNERABILITY_TICKS = 15;
		Shark.BASE_ENERGY = 0;
		Shark.TICKS_TILL_REPRODUCE = 2;
	}

	class Clock implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent arg0)
		{
			tick();
		}
	}
	
	private int delay = 10;
	private Timer timer = new Timer(delay, new Clock());
	
	private int tickNum = 0;
	
	public void tick()
	{
		tickNum++;
		if (tickNum > 50)
		{
			map.nextChronon();
		}
	}

	private void setSpatialFields(int numHorizontalTiles, int gridlineSize)
	{
		this.numGridlines = numHorizontalTiles + 1;
		this.gridlineSize = gridlineSize;
		
		this.tileSize = (PREF_X_BOUND - numGridlines*gridlineSize) / numHorizontalTiles;
		
		this.numVerticalTiles = (PREF_Y_BOUND - numGridlines*gridlineSize) / tileSize;
		
		this.preferredHeight = numVerticalTiles*tileSize + numGridlines*gridlineSize - 10;
		this.preferredWidth = numHorizontalTiles*tileSize + numGridlines*gridlineSize - 10;	
	}

	@Override
	public Dimension getPreferredSize()
	{
		return new Dimension(preferredWidth, preferredHeight);
	}
	
	private Color gridlineColor = Color.DARK_GRAY;
	private Color fishColor = Color.GREEN;
	private Color sharkColor = Color.BLUE;
	private Color spaceColor = Color.BLACK;
	
	@Override
	public void paint(Graphics g)
	{
		g.setColor(gridlineColor);
		g.fillRect(0, 0, preferredWidth, preferredWidth);

		int yPos = gridlineSize;
		for (int y = 0; y < map.array.length; y++)
		{
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
				else
				{
					g.setColor(spaceColor);
					g.fillRect(xPos, yPos, tileSize, tileSize);
				}
				
				xPos += gridlineSize + tileSize;
			}
			
			yPos += gridlineSize + tileSize;

		}
	}

}
