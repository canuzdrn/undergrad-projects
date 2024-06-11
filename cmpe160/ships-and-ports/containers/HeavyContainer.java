
//DO_NOT_EDIT_ANYTHING_ABOVE_THIS_LINE

package containers;

public class HeavyContainer extends Container{
	public HeavyContainer(int ID, int weight) {
		super(ID, weight);
	}
	
	public double consumption() {
		return 3.00*getWeight();
	}
	
	public String getType() {
		return "H";
	}
}



//DO_NOT_EDIT_ANYTHING_BELOW_THIS_LINE

