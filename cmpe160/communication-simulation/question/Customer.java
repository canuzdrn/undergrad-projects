
package question;

public class Customer {
	
	//DO_NOT_EDIT_ANYTHING_ABOVE_THIS_LINE
	int ID;
	String name;
	private int age;
	private Operator operator;
	private Bill bill;
	
	int talkTime = 0;
	int messages = 0;
	double netUsage = 0;
	
	
	Customer(int ID,String name,int age,Operator operator,double limitingAmount) {
		this.ID = ID;
		this.name = name;
		this.age = age;
		this.operator = operator;
		bill = new Bill(limitingAmount);

		
	}
	
	void talk(int minute,Customer other) {
		
		if(this.ID != other.ID) {
			double cgoingToPay = operator.calculateTalkingCost(minute, this);
			if (bill.check(bill.getCurrentDebt() + cgoingToPay) == false) {
				bill.add(cgoingToPay);
				operator.addTalkingTime(minute);
				other.operator.addTalkingTime(minute);
				talkTime += minute;
				other.talkTime += minute;
			}
		}
	}
	
	void message(int quantity,Customer other) {
		if(this.ID != other.ID) {
			double cPay = operator.calculateMessageCost(quantity, this, other);
			if (bill.check(bill.getCurrentDebt() + cPay) == false) {
				bill.add(cPay);
				operator.addMessages(quantity);
				messages += quantity;
			}
		}	
	}
	
	void connection(double amount) {
		double cgoingToPay = operator.calculateNetworkCost(amount);
		if (bill.check(bill.getCurrentDebt() + cgoingToPay) == false) {
			netUsage += amount;
			operator.mbUsage += amount;
			bill.add(cgoingToPay);
		}
	}
	
	int getAge() {
		return age;
	}
	
	void setAge(int age) {
		this.age = age;
	}

	Operator getOperator() {
		return operator;
	}

	void setOperator(Operator operator) {
		this.operator = operator;
	}

	Bill getBill() {
		return bill;
	}

	void setBill(Bill bill) {
		this.bill = bill;
	}

	//DO_NOT_EDIT_ANYTHING_BELOW_THIS_LINE
}

