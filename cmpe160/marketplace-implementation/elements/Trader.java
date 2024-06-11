package elements;
/**
 * Traders are the users of the market
 * They are getting initialized with their own wallet which includes the amount of coins and dollars they have
 * @author Can
 *
 */
public class Trader {
	private int id;
	private Wallet wallet;
	
	public static int numberOfUsers = 0;
	/**
	 * Trader constructor
	 * @param dollars
	 * @param coins
	 */
	public Trader(double dollars, double coins) {
		wallet = new Wallet(dollars,coins);
	}
	/**
	 * This method runs only when a transaction occurs
	 * This method only being used for transferring coins between traders
	 * @param amount
	 * @param price
	 * @param market
	 * @return
	 */
	public int sell(double amount, double price, Market market) {
		this.getWallet().blockedCoins -= amount;
		this.getWallet().addDollar((amount*price)*(1-(market.fee/1000.0)));
		return 0;
	}
	/**
	 * This method runs only when a transaction occurs
	 * This method only being used for transferring dollars between traders
	 * @param amount
	 * @param price
	 * @param market
	 * @return
	 */
	public int buy(double amount, double price, Market market) {
		this.getWallet().blockedDollars -= amount*price;
		this.getWallet().addCoins(amount);
		return 0;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Wallet getWallet() {
		return wallet;
	}

	public void setWallet(Wallet wallet) {
		this.wallet = wallet;
	}
	
}
