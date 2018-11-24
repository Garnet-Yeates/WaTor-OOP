package edu.wit.yeatesg.wator.objects;
/**
 * An instance of this class represents a point (y, x) in a two dimensional
 * Array.
 * 
 * @author yeatesg
 */
public class Location
{
	/** The y index of this point in the two dimensional array */
	private int y;
	/** The x index of this point in the two dimensional array */
	private int x;

	/**
	 * Constructs a new Location at the specified x and y
	 * 
	 * @param y the y index of this Location
	 * @param x the x index of this Location
	 */
	public Location(int y, int x)
	{
		this.y = y;
		this.x = x;
	}

	/**
	 * Converts this Location to a String, formatted as "y,x"
	 */
	@Override
	public String toString()
	{
		return y + "," + x;
	}

	/**
	 * @return the x-index of this Location
	 */
	public int getX()
	{
		return x;
	}

	/**
	 * @return the y-index of this Location
	 */
	public int getY()
	{
		return y;
	}
	
	public void setX(int x)
	{
		this.x = x;
	}
	
	public void setY(int y)
	{
		this.y = y;
	}

	/**
	 * Constructs an Location from the given String and returns it.
	 * 
	 * @param s The string that should be converted to an Location
	 * @return An Location created from the String that the user entered as the
	 *         parameter
	 */
	public static Location fromString(String s)
	{
		String[] split = s.split(",");
		return new Location(Integer.parseInt(split[0]), Integer.parseInt(split[1]));
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof Location)
		{
			Location p = (Location) obj;
			if (p.x == x && p.y == y)
			{
				return true;
			}
		}
		return false;
	}
	
	public Location clone()
	{
		return new Location(y, x);
	}
}
