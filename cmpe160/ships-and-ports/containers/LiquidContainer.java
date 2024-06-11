
//DO_NOT_EDIT_ANYTHING_ABOVE_THIS_LINE

package containers;

public class LiquidContainer extends HeavyContainer {
	public LiquidContainer(int ID, int weight) {
		super(ID, weight);
	}
	
	public double consumption() {
		return 4.00*getWeight();
	}
	
	public String getType() {
		return "L";
	}

}



//DO_NOT_EDIT_ANYTHING_BELOW_THIS_LINE

