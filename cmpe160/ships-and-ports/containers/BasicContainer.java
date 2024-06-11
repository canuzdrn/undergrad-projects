
//DO_NOT_EDIT_ANYTHING_ABOVE_THIS_LINE

package containers;

public class BasicContainer extends Container{
	public BasicContainer(int ID, int weight) {
		super(ID, weight);
	}
	
	public double consumption() {
		return 2.50*getWeight();
	}
	public String getType() {
		return "B";
	}
}


//DO_NOT_EDIT_ANYTHING_BELOW_THIS_LINE

