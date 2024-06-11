
//DO_NOT_EDIT_ANYTHING_ABOVE_THIS_LINE

package ships;

import java.util.*;


import containers.Container;
import interfaces.IShip;
import ports.Port;
/**
 * Ships are the transports we choose to transport, load, unload the containers along different ports
 * Ships keep different kind of containers at different numbers and weights
 * Ships can be created at different ports and travel among them
 * @author Can
 *
 */
public class Ship implements IShip,Comparable<Ship>{
	/**
	 * ID of the ship
	 */
	private int ID;
	/**
	 * Fuel stored in the ship
	 */
	private double fuel = 0;
	/**
	 * Port that ship currently located at
	 */
	public Port currentPort;
	/**
	 * Arraylist of all containers located at the ship
	 */
	public ArrayList<Container> containers = new ArrayList<Container>();
	/**
	 * Arraylist of basic containers' ID located at the ship
	 */
	public ArrayList<Integer> bcontainers = new ArrayList<Integer>();
	/**
	 * Arraylist of heavy containers' ID located at the ship
	 */
	public ArrayList<Integer> hcontainers = new ArrayList<Integer>();
	/**
	 * Arraylist of refrigerated containers' ID located at the ship
	 */
	public ArrayList<Integer> rcontainers = new ArrayList<Integer>();
	/**
	 * Arraylist of liquid containers' ID located at the ship
	 */
	public ArrayList<Integer> lcontainers = new ArrayList<Integer>();
	/**
	 * Maximum weight amount ship can hold
	 */
	int totalWeightCapacity;
	/**
	 * Maximum number of container ship can hold
	 */
	int maxNumberOfAllContainers;
	/**
	 * Maximum number of heavy container ship can hold
	 */
	int maxNumberOfHeavyContainers;
	/**
	 * Maximum number of refrigerated container ship can hold
	 */
	int maxNumberOfRefrigeratedContainers;
	/**
	 * Maximum number of liquid container ship can hold
	 */
	int maxNumberOfLiquidContainers;
	/**
	 * Fuel that ship consumes per KM
	 */
	double fuelConsumptionPerKM;
	
	int currentWeight = 0;
	int currentNumberOfAllContainers = 0;
	int currentNumberOfHeavyContainers = 0;
	int currentNumberOfRefrigeratedContainers = 0;
	int currentNumberOfLiquidContainers = 0;
	int currentNumberOfBasicContainers = 0;
	/**
	 * Constructs and initializes a Ship
	 * @param ID ID of the ship
	 * @param p Port that the ship created at
	 * @param totalWeightCapacity Maximum weight ship can hold
	 * @param maxNumberOfAllContainers Maximum number of containers ship can hold
	 * @param maxNumberOfHeavyContainers Maximum number of heavy containers ship can hold
	 * @param maxNumberOfRefrigeratedContainers Maximum number of refrigerated containers ship can hold
	 * @param maxNumberOfLiquidContainers Maximum number of liquid containers ship can hold
	 * @param fuelConsumptionPerKM Fuel amount that ship consumes per km
	 */
	public Ship(int ID, Port p, int totalWeightCapacity, int maxNumberOfAllContainers, int maxNumberOfHeavyContainers, int maxNumberOfRefrigeratedContainers,
			int maxNumberOfLiquidContainers, double fuelConsumptionPerKM) {
		this.ID = ID;
		this.currentPort  = p;
		p.current.add(this);
		this.totalWeightCapacity = totalWeightCapacity;
		this.maxNumberOfAllContainers = maxNumberOfAllContainers;
		this.maxNumberOfHeavyContainers = maxNumberOfHeavyContainers;
		this.maxNumberOfRefrigeratedContainers = maxNumberOfRefrigeratedContainers;
		this.maxNumberOfLiquidContainers = maxNumberOfLiquidContainers;
		this.fuelConsumptionPerKM = fuelConsumptionPerKM;
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
	 * Checks whether the total weight limit would be exceeded with the additional container given as a parameter
	 * @param cont Given container to load onto ship
	 * @return Returns true if the weight limit will not be exceeded, false if the weight limit would be exceeded with the additional container taken as a parameter
	 */
	boolean weightCheck(Container cont) {
		if (currentWeight + cont.getWeight() > totalWeightCapacity) {
			return false;
		}
		return true;
	}
	/**
	 * Checks whether the container exists at the current port that the ship located at
	 * @param cont Given container 
	 * @return Returns true if container exists at the current port, false if the container doesn't exist at the current port
	 */
	boolean currentPortCheck(Container cont) {
		if (this.currentPort.containers.contains(cont) == true) {
			return true;
		}
		return false;
	}
	/**
	 * Checks if the container may be loaded to ship by checking the container-type specific limtis 
	 * @param cont Given container
	 * @return Returns true if number of containers which are the same type of container as given container, hasn't reached the type specific limit yet, so we can load the container
	 */
	boolean availabilityCheck(Container cont) {
		int availableR = this.maxNumberOfRefrigeratedContainers - this.currentNumberOfRefrigeratedContainers;
		int availableL = this.maxNumberOfLiquidContainers - this.currentNumberOfLiquidContainers;
		int availableH = this.maxNumberOfHeavyContainers - this.currentNumberOfHeavyContainers;
		int availableAll = this.maxNumberOfAllContainers - this.currentNumberOfAllContainers;
		
		String ctype = cont.getType();
		
		if (availableAll > 0) {
			if (ctype == "R") {
				if (availableR > 0 && availableH > 0) {
					return true;
				}
			}
			if (ctype == "L") {
				if (availableL > 0 && availableH > 0) {
					return true;
				}
			}
			if (ctype == "H") {
				if (availableH > 0) {
					return true;
				}
			}
			if (ctype == "B") {
					return true;
			}
		}
		return false;
	}
	/**
	 * Initializes the loading actions for the given container if the conditions (Total weight limit, type specific limits, container's existence at the port) are provided
	 * @param cont Given container
	 * @return Returns true if conditions are provided , false if the one of the conditions is not provided
	 */
	@Override
	public boolean load(Container cont) {
		if (currentPortCheck(cont) == true && availabilityCheck(cont) == true && weightCheck(cont) == true) {
			loadingAction(cont);
			return true;
		}
		else {
			return false;
		}
	}
	/**
	 * Goes through with the container loading actions to the ship
	 * @param cont Given container
	 */
	public void loadingAction(Container cont) {
		String ctype = cont.getType();
		if (ctype == "R") {
			currentNumberOfAllContainers += 1;
			currentNumberOfHeavyContainers += 1;
			currentNumberOfRefrigeratedContainers +=1;
		}
		if (ctype == "L") {
			currentNumberOfAllContainers += 1;
			currentNumberOfHeavyContainers += 1;
			currentNumberOfLiquidContainers += 1;
		}
		if (ctype == "H") {
			currentNumberOfAllContainers += 1;
			currentNumberOfHeavyContainers += 1;
		}
		if (ctype == "B") {
			currentNumberOfAllContainers += 1;
			currentNumberOfBasicContainers += 1;
		}
		currentWeight += cont.getWeight();
		this.containers.add(cont);
		removeContainer(currentPort, cont);
	}
	/**
	 * Removes given container from the port since it is loaded to the ship
	 * @param port Port the container needed to be removed from
	 * @param cont Given container
	 */
	void removeContainer(Port port,Container cont) {
		Iterator<Container> itr = port.containers.iterator();
		while (itr.hasNext()) {
			Container element = itr.next();
			if (element.getID() == cont.getID()) {
				itr.remove();
				break;
			}
		}
	}
	/**
	 * Calculates the total fuel consumptions per km	
	 * @return Total fuel consumption per km
	 */
	public double totalfuelPerKm() {
		double fuelPer = 0;
		for (Container cont : this.containers) {
			fuelPer += cont.consumption();
		}
		fuelPer += this.fuelConsumptionPerKM;
		return fuelPer;
	}
	/**
	 * Sails the ship to the port given as a parameter
	 * @param p Destinated port
	 * @return Returns true if the fuel of the ship is sufficient to sail to the destinated port, false if the fuel is not sufficient
	 */
	@Override
	public boolean sailTo(Port p) {
		if(this.currentPort.getDistance(p)*totalfuelPerKm() <= this.fuel) {
			this.fuel -= this.currentPort.getDistance(p)*totalfuelPerKm();
			this.currentPort.outgoingShip(this);
			p.incomingShip(this);
			return true;
		}
		return false;
	}

	/**
	 * Refuels the ship as the given amount
	 * @param newFuel Given amount of fuel
	 */
	@Override
	public void reFuel(double newFuel) {
		this.fuel += newFuel;
	}
	/**
	 * Unloads the container from the ship
	 * @param cont Given container
	 * @return Returns true if the container unloaded successfully, false if not
	 */
	@Override
	public boolean unLoad(Container cont) {
		if (this.containers.contains(cont)) {
			Iterator<Container> itr = this.containers.iterator();
			while (itr.hasNext()) {
				Container element = itr.next();
				if (element.getID() == cont.getID()) {
					arrangingContainerNumbersAfterUnload(cont);
					this.currentWeight -= cont.getWeight();
					itr.remove();
					this.currentPort.containers.add(cont);
					return true;
				}
			}
		}
		return false;
	}
	public void arrangingContainerNumbersAfterUnload(Container cont) {
		String contType = cont.getType();
		currentNumberOfAllContainers -= 1;
		if (contType == "B") {
			currentNumberOfBasicContainers -= 1;
		}
		if (contType == "H") {
			currentNumberOfHeavyContainers -=  1;
		}
		if (contType == "R") {
			currentNumberOfHeavyContainers -= 1;
			currentNumberOfRefrigeratedContainers -= 1;
		}
		if (contType == "L") {
			currentNumberOfHeavyContainers -= 1;
			currentNumberOfLiquidContainers -= 1;
		}
	}
	/**
	 * Returns the current containers at the ship
	 * @return Returns the curren containers ordered according to their ID's
	 */
	public ArrayList<Container> getCurrentContainers(){
		Collections.sort(this.containers);
		return this.containers;
	}
	/**
	 * Getter method for ID of the ship
	 * @return Returns the ID of the ship
	 */
	public int getID() {
		return this.ID;
	}
	/**
	 * Getter method for fuel amount of the ship
	 * @return Returns the fuel amount of the ship
	 */
	public double getFuel() {
		return this.fuel;
	}
	
	@Override
	public int compareTo(Ship o) {
		if (this.getID() > o.getID()) {
			return 1;
		}
		else if (this.getID() < o.getID()) {
			return -1;
		}
		return 0;
	}
	
}



//DO_NOT_EDIT_ANYTHING_BELOW_THIS_LINE

