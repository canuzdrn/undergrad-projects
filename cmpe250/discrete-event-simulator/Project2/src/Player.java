
public class Player implements Comparable<Player>{
	public int id;
	public int skill;
	
	public boolean atTraining = false;
	public boolean atPhysio = false;
	public boolean atMassage = false;
	
	double lastTrainingDuration;
	
	public int numOfMassage = 0;
	
	public double enteredTraining;
	
	public double enteredTimeTrainingQ;
	public double enteredTimePhysioQ;
	public double enteredTimeMassageQ;
	
	public double exitTimeTrainingQ;
	public double exitTimePhysioQ;
	public double exitTimeMassageQ;
	
	public double trainingSpentTime = 0;
	public double physioQSpentTime = 0;
	public double massageQSpentTime = 0;
	
	
	Physiotherapist withThisTherapist;
	
	public Player(int id, int skill) {
		this.id = id;
		this.skill = skill;
	}
	
	public boolean isAvailable() {
		if(atTraining == false && atPhysio == false && atMassage == false) {
			return true;
		}
		return false;
	}
	
	@Override
	public int compareTo(Player o) {
		if(this.id < o.id) {
			return -1;
		}
		else if(this.id > o.id){
			return 1;
		}
		return 0;
	}
}
