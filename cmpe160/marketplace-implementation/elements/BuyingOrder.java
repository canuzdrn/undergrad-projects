package elements;
/**
 * BuyingOrder is the child class of Order class
 * Initializes when buying order is eligible to be created by a trader
 * @author Can
 *
 */
public class BuyingOrder extends Order implements Comparable<BuyingOrder>{
	/**
	 * BuyingOrder constructor which inherits from it's parent class
	 * @param traderID
	 * @param amount
	 * @param price
	 */
	public BuyingOrder(int traderID, double amount, double price) {
		super(traderID, amount, price);
		// TODO Auto-generated constructor stub
	}

	@Override
	public int compareTo(BuyingOrder o) {
		if (this.price > o.price) {
			return -1;
		}
		else if (this.price < o.price) {
			return 1;
		}
		else {
			if (this.amount > o.amount) {
				return -1;
			}
			else if (this.amount < o.amount) {
				return 1;
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