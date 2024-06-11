package elements;
/**
 * Order class is the parent class of SellingOrder and BuyingOrder
 * @author Can
 *
 */
public class Order {
	
	double amount;
	double price;
	int traderID;
	/**
	 * Order constructor
	 * @param traderID
	 * @param amount
	 * @param price
	 */
	public Order(int traderID, double amount, double price) {
		
		this.traderID = traderID;
		this.amount = amount;
		this.price= price;
	}

	public double getAmount() {
		return amount;
	}

	public double getPrice() {
		return price;
	}

	public int getTraderID() {
		return traderID;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public void setTraderID(int traderID) {
		this.traderID = traderID;
	}
	
	
}
