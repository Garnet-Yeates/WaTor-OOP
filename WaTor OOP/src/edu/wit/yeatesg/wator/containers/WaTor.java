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
	
	private static final int PREF_XY_BOUND = 1000;
	
	public static final Random R = new Random(123456);
	
	private int tileSize;
	
	private int gridlineSize;
	private int numGridlines;
	
	private int preferredLength;
	
	private Map map;
	
	public static void main(String[] args)
	{
		openDisplayGUI(450, 0);
	}
	
	private static void openDisplayGUI(int width, int gridThickness)
	{
		JFrame gameFrame = new JFrame();

		WaTor gamePanel = new WaTor(width, gridThickness);
		gamePanel.setPreferredSize(new Dimension(gamePanel.preferredLength, gamePanel.preferredLength));
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
	public WaTor(int numTiles, int gridlineSize)
	{
		presetFast();
				
		setSpatialFields(numTiles, gridlineSize);
		
		map = new Map(numTiles, this);
		map.randomFill(0.98, 0.02);
		repaint();
		
		timer.setInitialDelay(0);
		timer.start();
	}
	
	public void presetFast()
	{
		Fish.chrononsTillReproduce = 5;
		Fish.fishEnergyWorth = 3;

		Shark.invulnerabilityPeriod = 3;
		Shark.baseEnergy = 0;
		Shark.chrononsTillReproduce = 1;
	}

	public void presetFullExtinction()
	{
		Fish.chrononsTillReproduce = 15;
		Fish.fishEnergyWorth = 10;

		Shark.invulnerabilityPeriod = 20;
		Shark.baseEnergy = 0;
		Shark.chrononsTillReproduce = 1;
	}

	public void presetCycle1()
	{
		Fish.chrononsTillReproduce = 8;
		Fish.fishEnergyWorth = 5;

		Shark.invulnerabilityPeriod = 15;
		Shark.baseEnergy = 0;
		Shark.chrononsTillReproduce = 2;
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
		if (tickNum > 300)
		{
			map.nextChronon();
		}
	}

	private void setSpatialFields(int numTiles, int gridlineSize)
	{
		
		this.numGridlines = numTiles + 1;
		this.gridlineSize = gridlineSize;
		
		this.tileSize = (PREF_XY_BOUND - numGridlines*gridlineSize) / numTiles;
		
		this.preferredLength = numTiles*tileSize + numGridlines*gridlineSize - 10;		
	}

	@Override
	public Dimension getPreferredSize()
	{
		return new Dimension(preferredLength, preferredLength);
	}
	
	private Color gridlineColor = Color.DARK_GRAY;
	private Color fishColor = Color.GREEN;
	private Color sharkColor = Color.BLUE;
	private Color spaceColor = Color.BLACK;
	
	@Override
	public void paint(Graphics g)
	{
		g.setColor(gridlineColor);
		g.fillRect(0, 0, preferredLength, preferredLength);

		int yPos = gridlineSize;
		for (int y = 0; y < map.array.length; y++)
		{
			int xPos = gridlineSize;
			for (int x = 0; x < map.array.length; x++)
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
