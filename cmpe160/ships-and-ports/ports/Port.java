
//DO_NOT_EDIT_ANYTHING_ABOVE_THIS_LINE

package ports;

import java.util.*;

import containers.*;
import interfaces.IPort;
import ships.Ship;
/**
 * Port is being used as a depository for containers, unloading and loading destination for ships
 * Ports are being located in the x-y coordinate and their location determined by given x and y coordinate
 * @author Can
 *
 */
public class Port implements IPort {
	/**
	 * ID of the port
	 */
	private int ID;
	/**
	 * X coordinate of the port
	 */
	private double X;
	/**
	 * Y coordiante of the port
	 */
	private double Y;
	/**
	 * Arraylist of all containers located at the port
	 */
	public ArrayList<Container> containers = new ArrayList<Container>();
	/**
	 * Arraylist of all ships that have visited this port
	 */
	public ArrayList<Ship> history = new ArrayList<Ship>();
	/**
	 * Arraylist of all ships located at this port at current time
	 */
	public ArrayList<Ship> current = new ArrayList<Ship>();
	/**
	 * Arraylist of basic containers' ID located at the port
	 */
	public ArrayList<Integer> bcontainers = new ArrayList<Integer>();
	/**
	 * Arraylist of heavy containers' ID located at the port
	 */
	public ArrayList<Integer> hcontainers = new ArrayList<Integer>();
	/**
	 * Arraylist of refrigerated containers' ID located at the port
	 */
	public ArrayList<Integer> rcontainers = new ArrayList<Integer>();
	/**
	 * Arraylist of liquid containers' ID located at the port
	 */
	public ArrayList<Integer> lcontainers = new ArrayList<Integer>();
	
	/**
	 * Constructs and initializes a Port at position (x,y) with id : ID
	 * @param ID ID of the port
	 * @param X X coordinate of the port
	 * @param Y Y coordinate of the port
	 */
	public Port(int ID,double X,double Y){
		this.ID = ID;
		this.X = X;
		this.Y = Y;
	}
	/**
	 * Seperates the containers described their type accordingly, to their specified arraylists (Basic, Heavy, Refrigerated or Liquid)
	 */
	public void containerDistrubiton() {
		for (Container cont :containers) {
			if (cont.getType() == "B") {
				bcontainers.add(cont.getID());
			}
			if (cont.getType() == "H") {
				hcontainers.add(cont.getID());
			}
			if (cont.getType() == "R") {
				rcontainers.add(cont.getID());
			}
			if (cont.getType() == "L") {
				lcontainers.add(cont.getID());
			}
		}
	}
	/**
	 * Calculates and returns the distance between two ports 
	 * @param other Given port 
	 * @return The distance between the port method called from and the port given as a parameter
	 */
	public double getDistance(Port other){
		double portX = this.getX();
		double portY = this.getY();
		double destinationX = other.getX();
		double destinationY = other.getY();
		double xDiff = Math.abs(destinationX-portX);  
		double yDiff = Math.abs(destinationY-portY);
		double distanceSqaured = xDiff*xDiff + yDiff*yDiff;
		double distance = Math.pow(distanceSqaured, 0.5);
		return distance;
	}
	/**
	 * Adds incoming ship to the port's current arraylist and changes incoming ship's current port as the destinated port
	 * @param s Incoming ship
	 */
	@Override
	public void incomingShip(Ship s) {
		if (this.current.contains(s) == false) {
			current.add(s);
			s.currentPort = this;
		}
	}
	/**
	 * Adds outgoing ship to the port's history arraylist and removes the ship from port's current arraylist
	 * @param s Outgoing ship
	 */
	@Override
	public void outgoingShip(Ship s) {
		if (s.currentPort.history.contains(s) == false && s.currentPort.current.contains(s) == true){
			s.currentPort.history.add(s);
		}
		Iterator<Ship> itr = s.currentPort.current.iterator();
		while (itr.hasNext()) {
			Ship element = itr.next();
			if (element.getID() == s.getID()) {
				itr.remove();
				break;
			}
		}
	}
	/**
	 * Getter method for ID of the port
	 * @return ID of the port
	 */
	public int getID() {
		return this.ID;
	}
	/**
	 * Getter method for the x coordinate of the port
	 * @return x coordinate of the port
	 */
	public double getX() {
		return this.X;
	}
	/**
	 * Getter method for the y coordinate of the port
	 * @return y coordinate of the port
	 */
	public double getY() {
		return this.Y;
	}
}



//DO_NOT_EDIT_ANYTHING_BELOW_THIS_LINE

