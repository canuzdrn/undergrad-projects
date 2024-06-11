package elements;
/**
 * SelingOrder is the child class of Order class
 * Initializes when a selling order is eligible to be created by a trader
 * @author Can
 *
 */
public class SellingOrder extends Order implements Comparable<SellingOrder>{
	/**
	 * SellingOrder constructor which inherits from it's parent class
	 * @param traderID
	 * @param amount
	 * @param price
	 */
	public SellingOrder(int traderID, double amount, double price) {
		super(traderID, amount, price);
		// TODO Auto-generated constructor stub
	}

	@Override
	public int compareTo(SellingOrder o) {
		if (this.price > o.price) {
			return 1;
		}
		else if (this.price < o.price) {
			return -1;
		}
		else {
			if (this.amount < o.amount) {
				return 1;
			}
			else if (this.amount > o.amount) {
				return -1;
			}
			else {
				if (this.traderID > o.traderID) {
					return 1;
				}
				else if (this.traderID < o.traderID) {
					return -1;
				}
			}
		}
		return 0;
	}

}
