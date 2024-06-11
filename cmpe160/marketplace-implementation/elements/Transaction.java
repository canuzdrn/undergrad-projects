package elements;
/**
 * Transaction is the class that initializes after each successful transaction
 * All transactions are stored in an ArrayList called transactions
 * @author Can
 *
 */
public class Transaction {
	private SellingOrder sellingOrder;
	private BuyingOrder buyingOrder;
	/**
	 * Transaction constructor
	 * @param sellingOrder
	 * @param buyingOrder
	 */
	public Transaction(SellingOrder sellingOrder, BuyingOrder buyingOrder) {
		this.sellingOrder = sellingOrder;
		this.buyingOrder = buyingOrder;
	}
	public SellingOrder getSellingOrder() {
		return sellingOrder;
	}
	public BuyingOrder getBuyingOrder() {
		return buyingOrder;
	}
	public void setSellingOrder(SellingOrder sellingOrder) {
		this.sellingOrder = sellingOrder;
	}
	public void setBuyingOrder(BuyingOrder buyingOrder) {
		this.buyingOrder = buyingOrder;
	}
	
	
}
