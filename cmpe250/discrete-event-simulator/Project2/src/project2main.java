import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.*;

public class project2main {
	public static void main(String[] args) throws FileNotFoundException {
		Locale.setDefault(new Locale("en", "US"));
		//*********************	OUTPUT DATA *****************
		int maxLengthOfTrainingQ = 0;
		int maxLengthOfPhysioQ = 0;
		int maxLengthOfMassageQ = 0;
		
		double totalwaitingTimeTrainingQ = 0;
		int totalTrainingEvents = 0;
		
		
		double totalwaitingTimePhysioQ = 0;
		int totalPhysioEvents = 0;
		
		
		double totalwaitingTimeMassageQ = 0;
		int totalMassageEvents = 0;
		
		
		double totalTrainingTime = 0;
		double totalPhysioTime = 0;
		double totalMassageTime = 0;
		
		double sumOfAllTurnaroundTimes = 0;
		
		int invalidAttempts = 0;
		int canceledAttempts = 0;
		//***************************************************
		
		Scanner in = new Scanner(new File(args[0]));
		PrintStream out = new PrintStream(new File(args[1]));
		
		double simulationTime = 0;
		
		ArrayList<Player> players = new ArrayList<>();
		
		Comparator<Event> eventComparatorByTime = new Comparator<Event>() {
            @Override
            public int compare(Event e1, Event e2) {
                if(e1.time < e2.time) {
                	return -1;
                }
                else if(e1.time > e2.time){
                	return 1;
                }
                else{
                	if(e1.type.equals("t") && e2.type.equals("t")) {
                		if(e1.player.id < e2.player.id) {
                        	return -1;
                        }
                        else if(e1.player.id > e2.player.id){
                        	return 1;
                        }
                	}
                	else if(e1.type.equals("m") && e2.type.equals("m")) {
                		if(e1.player.skill > e2.player.skill) {
                			return -1;
                		}
                		else if(e1.player.skill < e2.player.skill) {
                			return 1;
                		}
                		else {
                			if(e1.player.id < e2.player.id) {
                            	return -1;
                            }
                            else if(e1.player.id > e2.player.id){
                            	return 1;
                            }
                		}
                	}
                	
                	else if(e1.type.equals("p") && e2.type.equals("p")) {
                		if(e1.player.lastTrainingDuration > e2.player.lastTrainingDuration) {
                			return -1;
                		}
                		else if(e1.player.lastTrainingDuration < e2.player.lastTrainingDuration) {
                			return 1;
                		}
                		else {
                			if(e1.player.id < e2.player.id) {
                            	return -1;
                            }
                            else if(e1.player.id > e2.player.id){
                            	return 1;
                            }
                		}
                	}
                	return 0;
                }
            }
		};
		
		PriorityQueue<Event> allEvents = new PriorityQueue<>(eventComparatorByTime);
		
		Comparator<Event> trainingEventComparator = new Comparator<Event>() {
			@Override
			public int compare(Event o1, Event o2) {
				if(o1.time < o2.time) {
                	return -1;
                }
                else if(o1.time > o2.time){
                	return 1;
                }
                else {
                	if(o1.player.id < o2.player.id) {
                    	return -1;
                    }
                    else if(o1.player.id > o2.player.id){
                    	return 1;
                    }
                }
				return 0;
			}
		};
		PriorityQueue<Event> trainingQ = new PriorityQueue<>(trainingEventComparator);
		
		Comparator<Event> physioEventComparator = new Comparator<Event>() {
			@Override
			public int compare(Event o1, Event o2) {
				if(o1.player.lastTrainingDuration > o2.player.lastTrainingDuration) {
					return -1;
				}
				else if(o1.player.lastTrainingDuration < o2.player.lastTrainingDuration) {
					return 1;
				}
				else {
					if(o1.time < o2.time) {
	                	return -1;
	                }
	                else if(o1.time > o2.time){
	                	return 1;
	                }
	                else {
	                	if(o1.player.id < o2.player.id) {
                        	return -1;
                        }
                        else if(o1.player.id > o2.player.id){
                        	return 1;
                        }
	                }
				}
				return 0;
			}
			
		};
		PriorityQueue<Event> physioQ = new PriorityQueue<>(physioEventComparator);
		
		
		Comparator<Event> massageEventComparator = new Comparator<Event>() {
			@Override
			public int compare(Event o1, Event o2) {
				if(o1.player.skill > o2.player.skill) {
					return -1;
				}
				else if(o1.player.skill < o2.player.skill) {
					return 1;
				}
				else {
					if(o1.time < o2.time) {
	                	return -1;
	                }
	                else if(o1.time > o2.time){
	                	return 1;
	                }
	                else {
	                	if(o1.player.id < o2.player.id) {
                        	return -1;
                        }
                        else if(o1.player.id > o2.player.id){
                        	return 1;
                        }
	                }
				}
				return 0;
			}
			
		};
		PriorityQueue<Event> massageQ = new PriorityQueue<>(massageEventComparator);

//**************************************************************************************************************************************************************************************************
		int N = in.nextInt();
		
		for (int i=0;i<N;i++) {
			int playerID = in.nextInt();
			int playerSkill = in.nextInt();
			Player player = new Player(playerID,playerSkill);
			players.add(player);
		}
		
		int A = in.nextInt();
		
		for (int j=0;j<A;j++) {
			String eventType = in.next();
			Player player = players.get(in.nextInt());
			double time = Double.parseDouble(in.next());
			double duration = Double.parseDouble(in.next());
			
			Event event = new Event(eventType,player,time,duration);
			allEvents.add(event);
		}
		
		int numOfphysiotherapist = in.nextInt();
		PriorityQueue<Physiotherapist> physiotherapists = new PriorityQueue<>();
		
		for(int i=0; i<numOfphysiotherapist; i++) {
			int id = i;
			double duration = Double.parseDouble(in.next());
			Physiotherapist ph = new Physiotherapist(id, duration);
			physiotherapists.add(ph);
		}
		int availablePhysiotherapist = numOfphysiotherapist;
		
		
		int numOfTrainingCoach = in.nextInt();
		int availableTrainingCoach = numOfTrainingCoach;
		
		int numOfMasseur = in.nextInt();
		int availableMasseur= numOfMasseur;

//		****************************************************************************************************
		while(allEvents.isEmpty() == false) {
			
			Event event = allEvents.poll();
			simulationTime = event.time;
			
			if(event.type.equals("t")) {
				if(event.player.isAvailable() == false) {
					canceledAttempts++;
				}
				else {
					event.player.enteredTraining = simulationTime;
					event.player.atTraining = true;
					if(availableTrainingCoach > 0) {
						double trainingDuration = event.duration;
						event.player.lastTrainingDuration = trainingDuration;
						Event leftTraining = new Event("lt",event.player,simulationTime + trainingDuration, 0);
						allEvents.add(leftTraining);
						
						availableTrainingCoach--;
					}
					else {
						trainingQ.add(event);
						event.player.enteredTimeTrainingQ = simulationTime;
						if(trainingQ.size() > maxLengthOfTrainingQ) {
							maxLengthOfTrainingQ = trainingQ.size();
						}
					}
					totalTrainingEvents++;
					totalTrainingTime += event.duration;
				}	
			}
			
			else if(event.type.equals("lt")) {
				// CREATE PHYSIO ORDER
				Event physio = new Event("p",event.player,simulationTime,0);
				allEvents.add(physio);
				//*********************			
				event.player.atTraining = false;
				availableTrainingCoach++;
				if(trainingQ.isEmpty() == false) {
					Event benchTrains = trainingQ.poll();
					
					totalwaitingTimeTrainingQ += (simulationTime - benchTrains.player.enteredTimeTrainingQ);
					
					benchTrains.player.atTraining = true;
					double trainingDuration = benchTrains.duration;
					benchTrains.player.lastTrainingDuration = trainingDuration;
					Event leftTraining = new Event("lt",benchTrains.player,simulationTime + trainingDuration, 0);
					allEvents.add(leftTraining);
					
					availableTrainingCoach--;
				}
			}
			
			
			
			else if(event.type.equals("p")) {
				if(event.player.isAvailable() == false) {
					canceledAttempts++;
				}
				else {
					event.player.atPhysio = true;
					if(availablePhysiotherapist > 0) {
						Physiotherapist availableTherapist = physiotherapists.poll();
						event.duration = availableTherapist.serviceDuration;
						availableTherapist.busy = true;
						event.player.withThisTherapist = availableTherapist;
						
						Event leftPhysio = new Event("lp",event.player,simulationTime + event.duration, 0);
						allEvents.add(leftPhysio);
						
						availablePhysiotherapist--;
					}
					else {
						physioQ.add(event);
						event.player.enteredTimePhysioQ = simulationTime;
						if(physioQ.size() > maxLengthOfPhysioQ) {
							maxLengthOfPhysioQ = physioQ.size();
						}
					}
					totalPhysioEvents++;
				}
			}
			
			
			
			else if(event.type.equals("lp")) {
				event.player.atPhysio = false;
				sumOfAllTurnaroundTimes += (simulationTime - event.player.enteredTraining);
				event.player.withThisTherapist.busy = false;
				physiotherapists.add(event.player.withThisTherapist);
				totalPhysioTime += event.player.withThisTherapist.serviceDuration;
				event.player.withThisTherapist = null;
				availablePhysiotherapist++;

				if(physioQ.isEmpty() == false) {
					Event benchPhysio = physioQ.poll();
					double waitedTimeAtPhysioQ = (simulationTime - benchPhysio.player.enteredTimePhysioQ);
					totalwaitingTimePhysioQ += waitedTimeAtPhysioQ;
					benchPhysio.player.physioQSpentTime += waitedTimeAtPhysioQ;
					
					benchPhysio.player.atPhysio = true;
					Physiotherapist availableTherapist = physiotherapists.poll();
					availableTherapist.busy = true;
					benchPhysio.player.withThisTherapist = availableTherapist;
					double physioDuration = availableTherapist.serviceDuration;
					Event leftPhysio = new Event("lp",benchPhysio.player,simulationTime + physioDuration, 0);
					allEvents.add(leftPhysio);
					
					availablePhysiotherapist--;

				}
			}

			else if (event.type.equals("m")) {
				if(event.player.numOfMassage == 3) {
					invalidAttempts++;
				}
				else {
					if(event.player.isAvailable() == false) {
						canceledAttempts++;
					}
					else {
						event.player.atMassage = true;
						if(availableMasseur > 0) {
							Event leftMassage = new Event("lm",event.player,simulationTime + event.duration, 0);
							allEvents.add(leftMassage);
							availableMasseur--;
						}
						else {
							massageQ.add(event);
							event.player.enteredTimeMassageQ = simulationTime;
							if(massageQ.size() > maxLengthOfMassageQ) {
								maxLengthOfMassageQ = massageQ.size();
							}
						}
						event.player.numOfMassage++;
						totalMassageEvents++;
						totalMassageTime += event.duration;	
					}
				}
			}

			else if(event.type.equals("lm")) {
				event.player.atMassage = false;
				availableMasseur++;
				if(massageQ.isEmpty() == false) {
					Event benchMassager = massageQ.poll();
					double waitedTimeAtMassageQ = (simulationTime - benchMassager.player.enteredTimeMassageQ);
					benchMassager.player.massageQSpentTime += (simulationTime - benchMassager.player.enteredTimeMassageQ);
					totalwaitingTimeMassageQ += waitedTimeAtMassageQ;
					benchMassager.player.atMassage = true;
					double massageDuration = benchMassager.duration;
					Event leftMassage = new Event("lm",benchMassager.player,simulationTime + massageDuration, 0);
					allEvents.add(leftMassage);
					
					availableMasseur--;
				}
			}
		}
		//******************************************************OUTPUT***************************************
		
		//********************1
		out.println(maxLengthOfTrainingQ);
		//********************2
		out.println(maxLengthOfPhysioQ);
		//********************3
		out.println(maxLengthOfMassageQ);
		
		//*****************4

		if (totalTrainingEvents == 0) {
			out.println(-2);
		}
		else {
			double averageWaitingTimeTrainingQ = totalwaitingTimeTrainingQ / totalTrainingEvents;
			out.println(String.format("%.3f", averageWaitingTimeTrainingQ));
		}
		
		//*****************5
		if (totalPhysioEvents == 0) {
			out.println(-2);
		}
		else {
			double averageWaitingTimePhysioQ = totalwaitingTimePhysioQ / totalPhysioEvents;
			out.println(String.format("%.3f", averageWaitingTimePhysioQ));
		}
		
		//*****************6
		if (totalMassageEvents == 0) {
			out.println(-2);
		}
		else {
			double averageWaitingTimeMassageQ = totalwaitingTimeMassageQ / totalMassageEvents;
			out.println(String.format("%.3f", averageWaitingTimeMassageQ));
		}
		
		//*****************7
		if (totalTrainingEvents == 0) {
			out.println(-2);
		}
		else {
			double averageTrainingTime = totalTrainingTime / totalTrainingEvents;
			out.println(String.format("%.3f", averageTrainingTime));
		}
		
		//*****************8
		if (totalPhysioEvents == 0) {
			out.println(-2);
		}
		else {
			double averagePhysioTime = totalPhysioTime / totalPhysioEvents;
			out.println(String.format("%.3f", averagePhysioTime));
		}
		
		//*****************9
		if (totalMassageEvents == 0) {
			out.println(-2);
		}
		else {
			double averageMassageTime = totalMassageTime / totalMassageEvents;
			out.println(String.format("%.3f", averageMassageTime));
		}
		
		//*****************10
		double averageTurnaroundTime;
		if(totalTrainingEvents == 0) {
			out.println(-2);
		}
		else {
			averageTurnaroundTime = sumOfAllTurnaroundTimes / totalTrainingEvents;
			out.println(String.format("%.3f", averageTurnaroundTime));;
		}
		
		//***11
		double maxPhysioQSpentTime = players.get(0).physioQSpentTime;
		int idFor11 = 0;
		for(Player p : players) {
			if(p.physioQSpentTime > maxPhysioQSpentTime) {
				maxPhysioQSpentTime = p.physioQSpentTime;
				idFor11 = p.id;
			}
		}
		out.println(idFor11+ " " + String.format("%.3f", maxPhysioQSpentTime));
		//******************************
		
		//***12
		ArrayList<Player> playersWith3massages = new ArrayList<>();
		for(Player p : players) {
			if(p.numOfMassage == 3) {
				playersWith3massages.add(p);
			}
		}
		
		double minMassageQSpent = -2;
		int idFor12 = -2;
		if(playersWith3massages.isEmpty() == false) {
			minMassageQSpent = playersWith3massages.get(0).massageQSpentTime;
			idFor12 = 0;
		}
		else {
			idFor12 = -1;
			minMassageQSpent = -1;
		}
		for(Player p : playersWith3massages) {
			if(p.massageQSpentTime < minMassageQSpent) {
				minMassageQSpent = p.massageQSpentTime;
				idFor12 = p.id;
			}
		}
		
		out.println(idFor12+ " " + String.format("%.3f", minMassageQSpent));
		
		//*******************************
		//********************13
		out.println(invalidAttempts);
		//********************14
		out.println(canceledAttempts);
		//********************15
		out.println(String.format("%.3f", simulationTime));
	}
}
