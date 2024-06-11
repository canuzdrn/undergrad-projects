package elements;

import java.util.*;
/**
 * Our Market class is where all transactions occur and recorded.
 * There is only one Market in our model which can be initialized via given fee amount
 * @author Can
 *
 */
public class Market {
	private PriorityQueue<SellingOrder> sellingOrders;
	private PriorityQueue<BuyingOrder> buyingOrders;
	private ArrayList<Transaction> transactions;
	int fee;
	public int successfulTransactions = 0;
	/**
	 * Market constructor
	 * @param fee Amount of fee that sellers pay during the transactions
	 */
	public Market(int fee) {
		this.fee = fee;
		this.sellingOrders = new PriorityQueue<>();
		this.buyingOrders = new PriorityQueue<>();
		this.transactions = new ArrayList<>();
	}
	/**
	 * Adds given sell order to the sellingOrders PQ
	 * @param order Given Selling Order
	 */
	public void giveSellOrder(SellingOrder order) {
		sellingOrders.add(order);
	}
	/**
	 * Adds given sell order to the sellingOrders PQ and blocks the given trader's coins as the given amount
	 * @param trader Trader who gave the selling order
	 * @param order Given selling order
	 */
	public void mySellOrder(Trader trader,SellingOrder order) {
		sellingOrders.add(order);
		trader.getWallet().transferToBlockedCoins(order.getAmount());
	}
	/**
	 * Adds given buy order to the buyingOrders PQ
	 * @param order Given Buying Order
	 */
	public void giveBuyOrder(BuyingOrder order) {
		buyingOrders.add(order);
	}
	/**
	 * Adds given buy order to the buyingOrders PQ and blocks the given trader's dollars as the given amount
	 * @param trader Trader who gave the buying order
	 * @param order Given buying order
	 */
	public void myBuyOrder(Trader trader,BuyingOrder order) {
		buyingOrders.add(order);
		trader.getWallet().transferToBlockedDollars(order.getPrice()*order.getAmount());
	}
	/**
	 * Makes open market operations
	 * @param price Desired price
	 * @param traders ArrayList of traders
	 */
	public void makeOpenMarketOperation(double price,ArrayList<Trader> traders) {
		while (buyingOrders.size() > 0) {
			if (buyingOrders.peek().getPrice() >= price) {
				SellingOrder sellOrder = new SellingOrder(0,buyingOrders.peek().getAmount(),buyingOrders.peek().getPrice());
				mySellOrder(traders.get(0), sellOrder);
				checkTransactions(traders);
			}
			else {
				break;
			}
		}
		
		while (sellingOrders.size() > 0) {
			if (sellingOrders.peek().getPrice() <= price) {
				BuyingOrder buyOrder = new BuyingOrder(0,sellingOrders.peek().getAmount(),sellingOrders.peek().getPrice());
				myBuyOrder(traders.get(0), buyOrder);
				checkTransactions(traders);
			}
			else {
				break;
			}
		}
		
	}

	public PriorityQueue<SellingOrder> getSellingOrders() {
		return sellingOrders;
	}

	public void setSellingOrders(PriorityQueue<SellingOrder> sellingOrders) {
		this.sellingOrders = sellingOrders;
	}

	public PriorityQueue<BuyingOrder> getBuyingOrders() {
		return buyingOrders;
	}

	public void setBuyingOrders(PriorityQueue<BuyingOrder> buyingOrders) {
		this.buyingOrders = buyingOrders;
	}

	public ArrayList<Transaction> getTransactions() {
		return transactions;
	}

	public void setTransactions(ArrayList<Transaction> transactions) {
		this.transactions = transactions;
	}
	/**
	 * Checks if there is any need for transaction by comparing the prices of the top of the queues and if there is any, makes the transactions	
	 * @param traders ArrayList of traders
	 */
	public void checkTransactions(ArrayList<Trader> traders) {
		while (buyingOrders.size() > 0 && sellingOrders.size() > 0) {
			if (buyingOrders.peek().getPrice() >= sellingOrders.peek().getPrice()) {
				SellingOrder sellingorder = sellingOrders.poll();
				BuyingOrder buyingorder = buyingOrders.poll();
				Transaction transaction = new Transaction(sellingorder,buyingorder);
				transactions.add(transaction);
				
				double buyerPays;
				double sellerGains;
				double buyerLockedBefore;
				double extraAmount;
				
				double sellerLocked;
				double sellerSold;
				double extraCoins;
				Trader seller = traders.get(sellingorder.getTraderID());
				Trader buyer = traders.get(buyingorder.getTraderID());
				if (buyingorder.getAmount() > sellingorder.getAmount()) {
					seller.getWallet().blockedCoins -= sellingorder.getAmount();
					buyer.getWallet().addCoins(sellingorder.getAmount());
					
					if (buyingorder.getPrice() > sellingorder.getPrice()) {
						
						buyerLockedBefore = buyingorder.getPrice() * buyingorder.getAmount();
						buyerPays = sellingorder.getAmount() * sellingorder.getPrice();
						extraAmount = buyerLockedBefore - buyerPays;
						buyer.getWallet().blockedDollars -= buyerPays;
						buyer.getWallet().transferFromBlockedDollars(extraAmount);
						
						sellerGains = (sellingorder.getAmount() * sellingorder.getPrice()) * (1-(this.fee/1000.0));
						seller.getWallet().addDollar(sellerGains);
						
					}
					else if (buyingorder.getPrice() == sellingorder.getPrice()) {
						buyerLockedBefore = buyingorder.getPrice() * buyingorder.getAmount();
						buyerPays = sellingorder.getAmount() * sellingorder.getPrice();
						buyer.getWallet().blockedDollars -= buyerPays;
						extraAmount = buyerLockedBefore - buyerPays;
						buyer.getWallet().transferFromBlockedDollars(extraAmount);
						
						sellerGains = (sellingorder.getAmount() * sellingorder.getPrice()) * (1-(this.fee/1000.0));
						seller.getWallet().addDollar(sellerGains);
					}
					
					BuyingOrder remainingBuyingOrder = new BuyingOrder(buyingorder.getTraderID(),buyingorder.getAmount()-sellingorder.getAmount(),buyingorder.getPrice());
					myBuyOrder(buyer, remainingBuyingOrder);
					
				}
				else if (buyingorder.getAmount() < sellingorder.getAmount()) {
					seller.getWallet().blockedCoins -= buyingorder.getAmount();
					buyer.getWallet().addCoins(buyingorder.getAmount());
					if (buyingorder.getPrice() > sellingorder.getPrice()) {
						buyerLockedBefore = buyingorder.getPrice() * buyingorder.getAmount();
						buyerPays = buyingorder.getAmount() * sellingorder.getPrice();
						buyer.getWallet().blockedDollars -= buyerPays;
						extraAmount = buyerLockedBefore - buyerPays;
						buyer.getWallet().transferFromBlockedDollars(extraAmount);
						
						sellerGains = (buyingorder.getAmount() * sellingorder.getPrice()) * (1-(this.fee/1000.0));
						seller.getWallet().addDollar(sellerGains);
						sellerLocked = sellingorder.getAmount();
						sellerSold = buyingorder.getAmount();
						extraCoins = sellerLocked - sellerSold;
						seller.getWallet().transferFromBlockedCoins(extraCoins);
					}
					else if (buyingorder.getPrice() == sellingorder.getPrice()) {
						buyerPays = buyingorder.getAmount() * sellingorder.getPrice();
						buyer.getWallet().blockedDollars -= buyerPays;
						
						sellerGains = (buyingorder.getAmount() * sellingorder.getPrice()) * (1-(this.fee/1000.0));
						seller.getWallet().addDollar(sellerGains);
						sellerLocked = sellingorder.getAmount();
						sellerSold = buyingorder.getAmount();
						extraCoins = sellerLocked - sellerSold;
						seller.getWallet().transferFromBlockedCoins(extraCoins);
					}
					
					SellingOrder remainingSellingOrder = new SellingOrder(sellingorder.getTraderID(),sellingorder.getAmount()-buyingorder.getAmount(),sellingorder.getPrice());
					mySellOrder(seller, remainingSellingOrder);
					
				}
				else {
					seller.getWallet().blockedCoins -= buyingorder.getAmount();
					buyer.getWallet().addCoins(buyingorder.getAmount());
					
					if (buyingorder.getPrice() > sellingorder.getPrice()) {
						buyerPays = buyingorder.getAmount() * sellingorder.getPrice();
						buyer.getWallet().blockedDollars -= buyerPays;
						buyerLockedBefore = buyingorder.getPrice() * buyingorder.getAmount();
						extraAmount = buyerLockedBefore - buyerPays;
						buyer.getWallet().transferFromBlockedDollars(extraAmount);
						
						sellerGains = (buyingorder.getAmount() * sellingorder.getPrice()) * (1-(this.fee/1000.0));
						seller.getWallet().addDollar(sellerGains);
					}
					else if (buyingorder.getPrice() == sellingorder.getPrice()) {
						buyerPays = buyingorder.getAmount() * sellingorder.getPrice();
						buyer.getWallet().blockedDollars -= buyerPays;
						
						sellerGains = (buyingorder.getAmount() * sellingorder.getPrice()) * (1-(this.fee/1000.0));
						seller.getWallet().addDollar(sellerGains);
					}
				}	
				successfulTransactions++;
			}
			else {
				break;
			}
		}
	}
	
}
