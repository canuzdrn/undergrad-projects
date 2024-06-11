package executable;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;

import java.util.*;

import elements.*;

public class Main {
	
	public static Random myRandom;
	
	public static void main(String[] args) throws FileNotFoundException {
		
		Scanner in = new Scanner(new File(args[0]));
		PrintStream out = new PrintStream(new File(args[1]));
		
		int seed = Integer.parseInt(in.nextLine());
		myRandom = new Random(seed);
		
		int invalidQueries = 0;
		
		String secondLine = in.nextLine();
		String[] secondlineList = secondLine.split(" ");
		
		int transactionFee = Integer.parseInt(secondlineList[0]);
		int numberOfUsers = Integer.parseInt(secondlineList[1]);
		int numberOfQueries = Integer.parseInt(secondlineList[2]);
		
		ArrayList<Trader> traders = new ArrayList<>();
		Market market = new Market(transactionFee);
		
		for(int i=0;i<numberOfUsers;i++) {
			String userLine = in.nextLine();
			String[] userLineList = userLine.split(" ");
			double dollar = Double.parseDouble(userLineList[0]);	
			double coins = Double.parseDouble(userLineList[1]);
			int id = Trader.numberOfUsers;
			Trader myTrader = new Trader(dollar,coins);
			myTrader.setId(id);
			traders.add(myTrader);
			Trader.numberOfUsers++;
		}
		
		for(int i=0;i<numberOfQueries;i++) {
			String queryLine = in.nextLine();
			String[] queryList = queryLine.split(" ");
			
			if(queryList[0].equals("3")) {
				int traderid = Integer.parseInt(queryList[1]);
				double amountDollar = Double.parseDouble(queryList[2]);
				traders.get(traderid).getWallet().addDollar(amountDollar);
			}
			
			if(queryList[0].equals("4")) {
				int traderid = Integer.parseInt(queryList[1]);
				double amountDollar = Double.parseDouble(queryList[2]);
				if (traders.get(traderid).getWallet().getDollars() < amountDollar) {
					invalidQueries++;
				}
				else {
					traders.get(traderid).getWallet().withdrawDollar(amountDollar);
				}
			}
			
			if(queryList[0].equals("5")) {
				int traderid = Integer.parseInt(queryList[1]);
				Trader t = traders.get(traderid);
				out.println("Trader "+traderid+": "+t.getWallet().getDollars()+"$ "+t.getWallet().getCoins()+"PQ");
			}
			
			if(queryList[0].equals("10")) {
				int traderid = Integer.parseInt(queryList[1]);
				Trader t = traders.get(traderid);
				double price = Double.parseDouble(queryList[2]);
				double amount = Double.parseDouble(queryList[3]);
				BuyingOrder bOrder = new BuyingOrder(traderid,amount,price);
				double totalAmount = price*amount;
				if(t.getWallet().getDollars() >= totalAmount) {
					market.myBuyOrder(t, bOrder);
				}
				else {
					invalidQueries++;
				}
				market.checkTransactions(traders);
			}
			
			if(queryList[0].equals("11")) {
				int traderid = Integer.parseInt(queryList[1]);
				Trader t = traders.get(traderid);
				double amount = Double.parseDouble(queryList[2]);
				double price;
				if (market.getSellingOrders().size() == 0) {
					invalidQueries++;
				}
				else {
					price = market.getSellingOrders().peek().getPrice();
					BuyingOrder bOrder = new BuyingOrder(traderid,amount,price);
					double totalAmount = price*amount;
					if(t.getWallet().getDollars() >= totalAmount) {
						market.myBuyOrder(t, bOrder);
					}
					else {
						invalidQueries++;
					}
					market.checkTransactions(traders);
				}
			}
			
			if(queryList[0].equals("20")) {
				int traderid = Integer.parseInt(queryList[1]);
				Trader t = traders.get(traderid);
				double price = Double.parseDouble(queryList[2]);
				double amount = Double.parseDouble(queryList[3]);
				SellingOrder sOrder = new SellingOrder(traderid,amount,price);
				if(t.getWallet().getCoins() >= amount) {
					market.mySellOrder(t,sOrder);
				}
				else {
					invalidQueries++;
				}
				market.checkTransactions(traders);
			}
			
			if(queryList[0].equals("21")) {
				int traderid = Integer.parseInt(queryList[1]);
				Trader t = traders.get(traderid);
				double amount = Double.parseDouble(queryList[2]);
				double price;
				if (market.getBuyingOrders().size() == 0) {
					invalidQueries++;
				}
				else {
					price = market.getBuyingOrders().peek().getPrice();
					SellingOrder sOrder = new SellingOrder(traderid,amount,price);
					if(t.getWallet().getCoins() >= amount) {
						market.mySellOrder(t,sOrder);
					}
					else {
						invalidQueries++;
					}
					market.checkTransactions(traders);
				}
			}
			
			if(queryList[0].equals("500")) {
				double totalDollar = 0;
				double totalCoins = 0;
				for(BuyingOrder bo : market.getBuyingOrders()) {
					totalDollar += bo.getPrice()*bo.getAmount();
				}
				for(SellingOrder so : market.getSellingOrders()) {
					totalCoins += so.getAmount();
				}
				out.printf("Current market size: %.5f %.5f\n",totalDollar,totalCoins);
			}
			
			if(queryList[0].equals("501")) {
				out.println("Number of successful transactions: "+market.successfulTransactions);
			}
			
			if(queryList[0].equals("502")) {
				out.println("Number of invalid queries: "+invalidQueries);
			}
			
			if(queryList[0].equals("505")) {
				double cp_buying = 0;
				double cp_selling = 0;
				double cp_average = 0;
				if (market.getBuyingOrders().size() > 0 && market.getSellingOrders().size() > 0) {
					cp_buying = market.getBuyingOrders().peek().getPrice();
					cp_selling = market.getSellingOrders().peek().getPrice();
					cp_average = (cp_buying + cp_selling) / 2.0;
				}
				else if (market.getBuyingOrders().size() > 0 && market.getSellingOrders().size() == 0) {
					cp_buying = market.getBuyingOrders().peek().getPrice();
					cp_average = market.getBuyingOrders().peek().getPrice();
				}
				else if (market.getBuyingOrders().size() == 0 && market.getSellingOrders().size() > 0) {
					cp_selling = market.getSellingOrders().peek().getPrice();
					cp_average = market.getSellingOrders().peek().getPrice();
				}
				out.printf("Current prices: %.5f %.5f %.5f\n",cp_buying,cp_selling,cp_average);
			}
			
			if(queryList[0].equals("555")) {
				for(Trader t : traders) {
					out.printf("Trader "+t.getId()+": %.5f$ %.5fPQ\n",t.getWallet().getTotalDollars(),t.getWallet().getTotalCoins());
				}
			}
			
			if(queryList[0].equals("666")) {
				double price = Double.parseDouble(queryList[1]);
				market.makeOpenMarketOperation(price, traders);
			}
			
			if(queryList[0].equals("777")) {
				for(Trader t : traders) {
					double reward = myRandom.nextDouble()*10;
					t.getWallet().addCoins(reward);
				}
			}
		}
	}
}

