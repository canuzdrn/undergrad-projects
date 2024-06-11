package elements;


/**
 * Wallet class is the class where we store the amount of dollars and coins that each user stores
 * Also where the withdraw and deposit actions are processed
 * @author Can
 *
 */
public class Wallet {
	private double dollars;
	private double coins;
	public double blockedDollars = 0;
	public double blockedCoins = 0;
	/**
	 * Wallet constructor
	 * @param dollars
	 * @param coins
	 */
	public Wallet(double dollars, double coins) {
		this.dollars = dollars;
		this.coins = coins;
	}

	public double getDollars() {
		return dollars;
	}

	public void setDollars(double dollars) {
		this.dollars = dollars;
	}
	/**
	 * Deposit action processes in this method
	 * @param amount
	 */
	public void addDollar(double amount) {
		this.dollars += amount;
	}
	/**
	 * Withdraw action processes in this method
	 * @param amount
	 */
	public void withdrawDollar(double amount) {
		this.dollars -= amount;
	}

	public double getCoins() {
		return coins;
	}

	public void setCoins(double coins) {
		this.coins = coins;
	}
	/**
	 * This method is used in transactions to transfer coins between two traders
	 * @param amount
	 */
	public void addCoins(double amount) {
		this.coins += amount;
	}
	/**
	 * Blocks the given amount of dollar
	 * @param amount
	 */
	public void transferToBlockedDollars(double amount) {
		this.dollars -= amount;
		this.blockedDollars += amount;
	}
	/**
	 * Unblocks the given amount of dollar
	 * @param amount
	 */
	public void transferFromBlockedDollars(double amount) {
		this.dollars += amount;
		this.blockedDollars -= amount;
	}
	
	public double getBlockedDollars() {
		return blockedDollars;
	}

	public void setBlockedDollars(double blockedDollars) {
		this.blockedDollars = blockedDollars;
	}
	
	public void transferToBlockedCoins(double amount) {
		this.coins -= amount;
		this.blockedCoins += amount;
	}
	
	public void transferFromBlockedCoins(double amount) {
		this.coins += amount;
		this.blockedCoins -= amount;
	}
	

	public double getBlockedCoins() {
		return blockedCoins;
	}

	public void setBlockedCoins(double blockedCoins) {
		this.blockedCoins = blockedCoins;
	}

	public double getTotalDollars() {
		return this.dollars + this.blockedDollars;
	}

	public double getTotalCoins() {
		return this.coins + this.blockedCoins;
	}	
}
