
package question;

public class Operator {
	//DO_NOT_EDIT_ANYTHING_ABOVE_THIS_LINE
	int ID;
	private double talkingCharge;
	private double messageCost;
	private double networkCharge;
	private int discountRate;
	
	int talkingTime = 0;
	int nOfMEssages = 0;
	double mbUsage = 0;
	
	Operator(int ID,double talkingCharge, double messageCost ,double networkCharge,int discountRate) {
		this.ID = ID;
		this.talkingCharge = talkingCharge;
		this.messageCost = messageCost;
		this.networkCharge = networkCharge;
		this.discountRate = discountRate; 
		
	}
	
	double calculateTalkingCost(int minute,Customer customer) {
		double cost = minute * customer.getOperator().talkingCharge;
		if (customer.getAge() < 18 || customer.getAge() > 65) {
			cost -= (cost * customer.getOperator().discountRate) / 100;
		}
		return cost;
	}

	double calculateMessageCost(int quantity, Customer customer, Customer other) {
		double cost = quantity * customer.getOperator().messageCost;
		if (customer.getOperator().ID == other.getOperator().ID) {
			cost -= cost * (customer.getOperator().discountRate) / 100;
		}
		return cost;
	}

	double calculateNetworkCost(double amount) {
		return amount * networkCharge;
	
	}

	double getTalkingCharge() {
		return talkingCharge;
	}

	void setTalkingCharge(double talkingCharge) {
		this.talkingCharge = talkingCharge;
	}

	double getMessageCost() {
		return messageCost;
	}

	void setMessageCost(double messageCost) {
		this.messageCost = messageCost;
	}

	double getNetworkCharge() {
		return networkCharge;
	}

	void setNetworkCharge(double networkCharge) {
		this.networkCharge = networkCharge;
	}

	int getDiscountRate() {
		return discountRate;
	}

	void setDiscountRate(int discountRate) {
		this.discountRate = discountRate;
	}
// ------------------------------------------------------------	
// ------------------------------------------------------------	
// ------------------------------------------------------------	
	void addTalkingTime(int amount) {
		talkingTime += amount;
	}
	
	void addMessages(int amount) {
		nOfMEssages += amount;
	}
	
	void addMbUsage(int amount) {
		mbUsage += amount;
	}

// ------------------------------------------------------------	
// ------------------------------------------------------------	
// ------------------------------------------------------------	

	//DO_NOT_EDIT_ANYTHING_BELOW_THIS_LINE
}

