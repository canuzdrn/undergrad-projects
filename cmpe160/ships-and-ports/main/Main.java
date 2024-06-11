
//DO_NOT_EDIT_ANYTHING_ABOVE_THIS_LINE

package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;

import java.util.Scanner;
import java.util.*;

import containers.*;
import ports.Port;
import ships.Ship;

import java.util.ArrayList;

public class Main {
	public static void main(String[] args) throws FileNotFoundException {
		
		//
		// Main receives two arguments: path to input file and path to output file.
		// You can assume that they will always be provided, so no need to check them.
		// Scanner and PrintStream are already defined for you.
		// Use them to read input and write output.
		// 
		// Good Luck!
		// 
		
		Scanner in = new Scanner(new File(args[0]));
		PrintStream out = new PrintStream(new File(args[1]));
		
		int N = in.nextInt();
		
		ArrayList<Container> myContainers = new ArrayList<Container>();
		ArrayList<Ship> myShips = new ArrayList<Ship>();
		ArrayList<Port> myPorts = new ArrayList<Port>();
		
		int containerID = 0;
		int shipID = 0;
		int portID = 0;
		
		for (int i=0;i<=N;i++) {
			String line = in.nextLine();
			String[] lineList = line.split(" ");
			
			if (lineList[0].equals("1")) {
				int pID = Integer.parseInt(lineList[1]);
				int weight = Integer.parseInt(lineList[2]);
				if (line.contains("R")) {
					RefrigeratedContainer C = new RefrigeratedContainer(containerID,weight);
					myPorts.get(pID).containers.add(C);
					myContainers.add(C);
				}
				else if (line.contains("L")) {
					LiquidContainer C = new LiquidContainer(containerID,weight);
					myPorts.get(pID).containers.add(C);
					myContainers.add(C);
				}
				else {
					if (weight <= 3000) {			
						BasicContainer C = new BasicContainer(containerID,weight);
						myPorts.get(pID).containers.add(C);
						myContainers.add(C);
					}
					else {
						HeavyContainer C = new HeavyContainer(containerID,weight);
						myPorts.get(pID).containers.add(C);
						myContainers.add(C);
					}
				}
				containerID += 1;
			}
			
			if (lineList[0].equals("2")) {
				int pID = Integer.parseInt(lineList[1]);
				int maxWeight = Integer.parseInt(lineList[2]);
				int maxCont = Integer.parseInt(lineList[3]);
				int maxHeavy = Integer.parseInt(lineList[4]);
				int maxRef = Integer.parseInt(lineList[5]);
				int maxLiq = Integer.parseInt(lineList[6]);
				double fuelPerKm = Double.parseDouble(lineList[7]);
				myShips.add(new Ship(shipID,myPorts.get(pID),maxWeight,maxCont,maxHeavy,maxRef,maxLiq,fuelPerKm));
				shipID += 1;
			}
			
			if (lineList[0].equals("3")) {
				double xPort = Double.parseDouble(lineList[1]);
				double yPort = Double.parseDouble(lineList[2]);
				myPorts.add(new Port(portID,xPort,yPort));
				portID += 1;
			}
			
			if (lineList[0].equals("4")) {
				int sID = Integer.parseInt(lineList[1]);
				int cID = Integer.parseInt(lineList[2]);
				myShips.get(sID).load(myContainers.get(cID));
			}
			
			if (lineList[0].equals("5")) {
				int sID = Integer.parseInt(lineList[1]);
				int cID = Integer.parseInt(lineList[2]);
				myShips.get(sID).unLoad(myContainers.get(cID));
			}
			if (lineList[0].equals("6")) {
				int sID = Integer.parseInt(lineList[1]);
				int pID = Integer.parseInt(lineList[2]);
				myShips.get(sID).sailTo(myPorts.get(pID));
			}
			
			if (lineList[0].equals("7")) {
				int sID = Integer.parseInt(lineList[1]);
				double addedFuel = Double.parseDouble(lineList[2]);
				myShips.get(sID).reFuel(addedFuel);
			}
		}
		

		
// ##################################################################################################################################
		for (Port port : myPorts) {
			out.printf("Port "+port.getID()+": (%.2f"+", "+"%.2f)\n",port.getX(),port.getY());
			Collections.sort(port.current);
			port.containerDistrubiton();
			if (port.bcontainers.size() > 0) {
				Collections.sort(port.bcontainers);
				String outstr = ("  BasicContainer:");
				for (int bc : port.bcontainers) {
					outstr += (" " + bc);
				}
				out.print(outstr+"\n");;
			}
			if (port.hcontainers.size() > 0) {
				Collections.sort(port.hcontainers);
				String outstr = ("  HeavyContainer:");
				for (int hc : port.hcontainers) {
					outstr += (" "+hc);
				}
				out.print(outstr+"\n");;
			}
			if (port.rcontainers.size() > 0) {
				Collections.sort(port.rcontainers);
				String outstr = ("  RefrigeratedContainer:");
				for (int rc : port.rcontainers) {
					outstr += (" "+rc);
				}
				out.print(outstr + "\n");;
			}
			if (port.lcontainers.size() > 0) {
				Collections.sort(port.lcontainers);
				String outstr = ("  LiquidContainer:");
				for (int lc : port.lcontainers) {
					outstr += (" "+lc);
				}
				out.print(outstr + "\n");;
			}
			for (Ship ship : port.current) {
				out.printf("  Ship "+ship.getID()+": %.2f\n",ship.getFuel());
				ship.containerDistrubiton();
				if (ship.bcontainers.size() > 0) {
					Collections.sort(port.bcontainers);
					String outstr = ("    BasicContainer:");
					for (int bc : ship.bcontainers) {
						outstr += (" "+bc);
					}
					out.print(outstr+"\n");;
				}
				if (ship.hcontainers.size() > 0) {
					Collections.sort(port.hcontainers);
					String outstr = ("    HeavyContainer:");
					for (int hc : ship.hcontainers) {
						outstr += (" "+hc);
					}
					out.print(outstr+"\n");;
				}
				if (ship.rcontainers.size() > 0) {
					Collections.sort(port.rcontainers);
					String outstr = ("    RefrigeratedContainer:");
					for (int rc : ship.rcontainers) {
						outstr += (" "+rc);
					}
					out.print(outstr + "\n");
				}
				if (ship.lcontainers.size() > 0) {
					Collections.sort(port.lcontainers);
					String outstr = ("    LiquidContainer:");
					for (int lc : ship.lcontainers) {
						outstr += (" " + lc);
					}
					out.print(outstr + "\n");
				}
			}
		}
// ##################################################################################################################################

		
		in.close();
		out.close();
	}
}



//DO_NOT_EDIT_ANYTHING_BELOW_THIS_LINE

