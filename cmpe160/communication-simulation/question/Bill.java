
package question;

public class Bill {

	//DO_NOT_EDIT_ANYTHING_ABOVE_THIS_LINE
	private double limitingAmount;
	private double currentDebt = 0;
	private double spentForBills = 0;
	
	Bill(double limitingAmount) {
		this.limitingAmount = limitingAmount;
		
	}
	
	boolean check(double amount) {
		if (amount > limitingAmount) {
			return true;
		}
		else {
			return false;
		}
	}
	
	void add(double amount) {
		currentDebt += amount;
	}
	
	void pay(double amount) {
		if (amount > currentDebt) {
			spentForBills += currentDebt;
			currentDebt = 0;
		}
		else {
			currentDebt -= amount;
			spentForBills += amount;
		}
	}
	
	void changeTheLimit(double amount) {
		if (amount >= currentDebt) {
			limitingAmount = amount;
		}
	}

	double getLimitingAmount() {
		return limitingAmount;
	}
	


	double getCurrentDebt() {
		return currentDebt;
	}
	
	double getSpentForBills() {
		return spentForBills;
	}
	
	


	//DO_NOT_EDIT_ANYTHING_BELOW_THIS_LINE
}

