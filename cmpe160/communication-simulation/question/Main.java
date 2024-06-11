
package question;

import static org.junit.Assert.assertArrayEquals;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Locale;
import java.util.Scanner;

public class Main {


	public static void main(String args[]) {

		Customer[] customers;
		Operator[] operators;

		int C, O, N;

		File inFile = new File(args[0]);  // args[0] is the input file
		File outFile = new File(args[1]);  // args[1] is the output file
		try {
			PrintStream outstream = new PrintStream(outFile);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}

		Scanner reader;
		try {
			reader = new Scanner(inFile);
		} catch (FileNotFoundException e) {
			System.out.println("Cannot find input file");
			return;
		}


		C = reader.nextInt();
		O = reader.nextInt();
		N = reader.nextInt();

		customers = new Customer[C];
		operators = new Operator[O];

		//DO_NOT_EDIT_ANYTHING_ABOVE_THIS_LINE
		PrintStream outstream1;
		try {
		        outstream1 = new PrintStream(outFile);
		}catch(FileNotFoundException e2) {
		        e2.printStackTrace();
		        return;
		}

		while(reader.hasNextLine()) {
			String[] givenLine;
			String line = reader.nextLine();
			givenLine = line.split(" ");

			if (givenLine[0].equals("1")) {
				int customersNull = 0;
				for(int i=0;i<C;i++) {
					if(customers[i] == null) {
						customersNull += 1;
					}
				}
				String name = givenLine[1];
				int age = Integer.parseInt(givenLine[2]);
				int operatorID = Integer.parseInt(givenLine[3]);
				double limitingAmount = Double.parseDouble(givenLine[4]);
				int customerID = C - customersNull;
				
				customers[customerID] = new Customer(customerID,name,age,operators[operatorID],limitingAmount);

			}
			
			if (givenLine[0].equals("2")) {
				int operatorsNull = 0;
				for(int i=0;i<O;i++) {
					if(operators[i] == null) {
						operatorsNull += 1;
					}
				}
				int ID = O - operatorsNull;
				double talkingCharge = Double.parseDouble(givenLine[1]);
				double messageCost = Double.parseDouble(givenLine[2]);
				double networkCharge = Double.parseDouble(givenLine[3]);
				int discountRate = Integer.parseInt(givenLine[4]);
				
				operators[ID] = new Operator(ID,talkingCharge,messageCost,networkCharge,discountRate);
				
			}
			if (givenLine[0].equals("3")) {
				int customer1 = Integer.parseInt(givenLine[1]);
				int customer2 = Integer.parseInt(givenLine[2]);
				int time = Integer.parseInt(givenLine[3]);
				customers[customer1].talk(time, customers[customer2]);
			}
			if (givenLine[0].equals("4")) {
				int customer1 = Integer.parseInt(givenLine[1]);
				int customer2 = Integer.parseInt(givenLine[2]);
				int quantity = Integer.parseInt(givenLine[3]);
				customers[customer1].message(quantity, customers[customer2]);
			}
			if (givenLine[0].equals("5")) {
				int customer1 = Integer.parseInt(givenLine[1]);
				double amount = Double.parseDouble(givenLine[2]);
				customers[customer1].connection(amount);
			}
			if (givenLine[0].equals("6")) {
				int customer1 = Integer.parseInt(givenLine[1]);
				double amount = Double.parseDouble(givenLine[2]);
				customers[customer1].getBill().pay(amount);
			}
			if (givenLine[0].equals("7")) {
				int customer1 = Integer.parseInt(givenLine[1]);
				int operator1 = Integer.parseInt(givenLine[2]);
				customers[customer1].setOperator(operators[operator1]);
			}
			if (givenLine[0].equals("8")) {
				int customer1 = Integer.parseInt(givenLine[1]);
				double amount = Double.parseDouble(givenLine[2]);
				customers[customer1].getBill().changeTheLimit(amount);;
			}
		}
		
		Customer maxTalker = customers[0];
		Customer maxMessager = customers[0];
		Customer maxNetUser = customers[0];
		
		for(int i=0;i<C;i++) {
			int numTalk = customers[i].talkTime;
			int numMessage = customers[i].messages;
			double netAmount = customers[i].netUsage;
			
			if (numTalk > maxTalker.talkTime) {
				maxTalker = customers[i];
			}
			if (numMessage > maxMessager.messages) {
				maxMessager = customers[i];
			}
			if (netAmount > maxNetUser.netUsage) {
				maxNetUser = customers[i];
			}
		}
		
		for (int k=0;k<O;k++) {
			String net = String.format(Locale.US,"%.2f",operators[k].mbUsage);
			outstream1.print("Operator " + operators[k].ID + " : " + operators[k].talkingTime  + " " + operators[k].nOfMEssages + " " + net + "\n");
		}
		for (int j=0;j<C;j++) {
			String moneySpent = String.format(Locale.US,"%.2f",customers[j].getBill().getSpentForBills());
			String debt = String.format(Locale.US,"%.2f",customers[j].getBill().getCurrentDebt());
			outstream1.print("Customer " + customers[j].ID + " : " + moneySpent  + " " + debt + "\n");
		}
		outstream1.println(maxTalker.name + " : " + maxTalker.talkTime);
		outstream1.println(maxMessager.name + " : " + maxMessager.messages);
		outstream1.println(maxNetUser.name + " : " + String.format(Locale.US,"%.2f",maxNetUser.netUsage));
		outstream1.close();
		reader.close();

		//DO_NOT_EDIT_ANYTHING_BELOW_THIS_LINE
	} 
}

