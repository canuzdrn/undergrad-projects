
//DO_NOT_EDIT_ANYTHING_ABOVE_THIS_LINE

package containers;
/**
 * Containers have different types, weights and unique IDs which are determined by the creation of the containers
 * Containers have different types and categorized as basic,heavy,refrigerated and liquid while refrigerated and liquid containers created as subclasses of heavy container
 * Containers consumes fuel which differ from type to type and proportional to the containers' weight
 * @author Can
 *
 */
public abstract class Container implements Comparable<Container>{
	private int ID;
	private int weight;
	/**
	 * Constructs and initializes a Container
	 * @param ID ID of the container
	 * @param weight Weight of the container
	 */
	public Container(int ID, int weight){
		this.ID = ID;
		this.weight = weight;
	}
	/**
	 * Calculates and returns the fuel consumption of the container per km
	 * @return Returns the fuel consumption of the container per km 
	 */
	abstract public double consumption();
	/**
	 * Checks the two container's id whether they are same or not 
	 * @param other Given container
	 * @return Returns true if the ids are equal so the compared two containers are the same containers, false if not
	 */
	public boolean equals(Container other) {
		if (this.getID() == other.getID() && this.getWeight() == other.getWeight() && this.getType() == other.getType()) {
			return true;
		}
		return false;
	}
	/**
	 * Returns the type of the container (BasicContainer : B , HeavyContainer : H , RefrigeratedContainer : R , LiquidContainer : L)
	 * @return Returns the type of the container (null as default, overriden at the subclasses of the class "Container")
	 */
	public String getType() {
		return null;
	}
	/**
	 * Getter method for the ID of the container
	 * @return Returns the ID of the container
	 */
	public int getID() {
		return ID;
	}
	/**
	 * Setter method for the ID of the container
	 * @param iD ID that wanted to be set
	 */
	public void setID(int iD) {
		ID = iD;
	}
	/**
	 * Getter method for the weight of the container
	 * @return Returns the weight of the container
	 */
	public int getWeight() {
		return weight;
	}
	/**
	 * Setter method for the ID of the container
	 * @param weight Weight that wanted to be set
	 */
	public void setWeight(int weight) {
		this.weight = weight;
	}
	
	public int compareTo(Container o) {
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

